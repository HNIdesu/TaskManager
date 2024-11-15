package com.hnidesu.taskmanager.fragment

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.activity.EditTaskActivity
import com.hnidesu.taskmanager.adapter.TaskListAdapter
import com.hnidesu.taskmanager.base.SortType
import com.hnidesu.taskmanager.base.filter.Filter
import com.hnidesu.taskmanager.base.filter.FilterChain
import com.hnidesu.taskmanager.database.TaskEntity
import com.hnidesu.taskmanager.databinding.FragmentTaskListBinding
import com.hnidesu.taskmanager.eventbus.TaskListChangeEvent
import com.hnidesu.taskmanager.manager.SettingManager
import com.hnidesu.taskmanager.manager.TaskManager
import com.hnidesu.taskmanager.util.ToastUtil
import com.hnidesu.taskmanager.widget.dialog.SetTaskDialogFactory
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId


class TaskListFragment : Fragment() {
    private val mFilterOption = object {
        var hideExpiredTask: Boolean = false
        var hideFinishedTask: Boolean = false
        var reverseSort: Boolean = false
        var sortType: SortType = SortType.CreationAsc
    }
    private var mFragmentTaskListBinding: FragmentTaskListBinding? = null
    private var mAutoUpdate: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentTaskListBinding.inflate(layoutInflater).also {
            mFragmentTaskListBinding = it
        }.root
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        val binding = mFragmentTaskListBinding!!
        mAutoUpdate = false
        val context = requireContext()
        val adapter = TaskListAdapter()
        adapter.onTaskFinishChangeListener = object : TaskListAdapter.OnTaskFinishChangeListener {
            override fun onFinish(taskItem: TaskEntity?) {
                if (taskItem == null)
                    return
                TaskManager.updateTask(
                    taskItem.copy(isFinished = 1, lastModifiedTime = System.currentTimeMillis())
                )
            }

            override fun onNotFinish(taskItem: TaskEntity?) {
                if (taskItem == null)
                    return
                TaskManager.updateTask(
                    taskItem.copy(isFinished = 0, lastModifiedTime = System.currentTimeMillis())
                )
            }

        }

        binding.recyclerview.apply {
            setAdapter(adapter)
            registerForContextMenu(this)
        }

        binding.buttonFilterTask.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }
        binding.buttonAddTask.setOnClickListener {
            SetTaskDialogFactory(
                context,
                object : SetTaskDialogFactory.OnFinishListener {
                    override fun onCancel() {}

                    override fun onSet(title: String, date: LocalDateTime) {
                        val current = System.currentTimeMillis()
                        val taskEntity = TaskEntity(
                            current,
                            "",
                            title,
                            0,
                            date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                            current,
                            0
                        )
                        TaskManager.addTask(taskEntity)
                        ToastUtil.toastShort(context, R.string.add_success)
                        val intent = Intent(context, EditTaskActivity::class.java).apply {
                            putExtra("task_id", taskEntity.createTime)
                        }
                        startActivity(intent)
                    }

                },
                getString(R.string.add_task)
            ).create().show()
        }
        val sharedPrefs = SettingManager.getDefaultSetting(context)
        val binder = mapOf(
            0 to SortType.CreationAsc,
            1 to SortType.ModifiedAsc,
            2 to SortType.DeadlineAsc
        )

        binding.filterPanel.switchHideExpiredTask.apply {
            setOnCheckedChangeListener { _, z ->
                mFilterOption.hideExpiredTask = z
                sharedPrefs.edit().putBoolean("hide_expired_task", z).apply()
                if (mAutoUpdate)
                    update()
            }
            isChecked = sharedPrefs.getBoolean("hide_expired_task", false)
        }
        binding.filterPanel.switchHideFinishedTask.apply {
            setOnCheckedChangeListener { _, z ->
                mFilterOption.hideFinishedTask = z
                sharedPrefs.edit().putBoolean("hide_finished_task", z).apply()
                if (mAutoUpdate)
                    update()
            }
            isChecked = sharedPrefs.getBoolean("hide_finished_task", false)
        }

        binding.filterPanel.checkboxReverse.apply {
            setOnCheckedChangeListener { _, z ->
                if (mFilterOption.reverseSort != z) {
                    mFilterOption.reverseSort = z
                    sharedPrefs.edit().putBoolean("reverse_sort", z).apply()
                    if (mAutoUpdate)
                        update()
                }
            }
            isChecked = sharedPrefs.getBoolean("reverse_sort", false)
        }

        binding.filterPanel.spinnerSort.apply {
            setAdapter(
                ArrayAdapter(
                    context,
                    R.layout.list_item,
                    arrayOf(
                        getString(R.string.creation_date),
                        getString(R.string.last_modified_date),
                        getString(R.string.deadline)
                    )
                )
            )

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    var sortType: SortType? = binder[position]
                    if (sortType == null)
                        sortType = mFilterOption.sortType
                    mFilterOption.sortType = sortType
                    if (binder.containsKey(position)) {
                        sharedPrefs.edit().putInt("sort_by", position).apply()
                    }
                    if (mAutoUpdate)
                        update()
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
            setSelection(sharedPrefs.getInt("sort_by", 0))
        }
        mAutoUpdate = true
        update()
        EventBus.getDefault().register(this)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = MenuInflater(context)
        inflater.inflate(R.menu.menu_context_all_tesk_single, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option_delete -> {
                val adapter = mFragmentTaskListBinding?.recyclerview?.adapter as? TaskListAdapter
                    ?: return false
                AlertDialog.Builder(requireContext()).setMessage(R.string.if_sure_to_delete)
                    .setNegativeButton(
                        R.string.cancel
                    ) { _, _ ->
                        ToastUtil.toastShort(
                            requireContext(),
                            R.string.cancelled
                        )
                    }.setPositiveButton(
                        R.string.ok
                    ) { _, _ ->
                        val selectedIndex = adapter.selectedIndex
                        val selectedItem = adapter.itemList[selectedIndex]
                        TaskManager.deleteTask(selectedItem)
                    }.setOnCancelListener { }.create().show()
                return true
            }

            R.id.option_edit -> {
                val adapter = mFragmentTaskListBinding?.recyclerview?.adapter as? TaskListAdapter
                    ?: return false
                val selectedIndex = adapter.selectedIndex
                val selectedItem = adapter.itemList[selectedIndex]
                SetTaskDialogFactory(
                    requireContext(),
                    Instant.ofEpochMilli(selectedItem.deadline).atZone(ZoneId.systemDefault())
                        .toLocalDateTime(),
                    selectedItem.title,
                    object : SetTaskDialogFactory.OnFinishListener {
                        override fun onCancel() {}
                        override fun onSet(title: String, date: LocalDateTime) {
                            val newItem = selectedItem.copy(
                                title = title,
                                deadline = date.atZone(ZoneId.systemDefault()).toInstant()
                                    .toEpochMilli(),
                                lastModifiedTime = System.currentTimeMillis()
                            )
                            TaskManager.updateTask(newItem)
                        }
                    },
                    getString(R.string.edit_task)
                ).create().show()
                return true
            }

            else -> return super.onContextItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    private fun update() {
        val adapter = mFragmentTaskListBinding?.recyclerview?.adapter as? TaskListAdapter ?: return
        val filterChain = FilterChain<TaskEntity>()
        if (mFilterOption.hideExpiredTask) {
            filterChain.add(object : Filter<TaskEntity> {
                override fun match(t: TaskEntity): Boolean {
                    return t.deadline > System.currentTimeMillis()
                }
            })
        }
        if (mFilterOption.hideFinishedTask) {
            filterChain.add(object : Filter<TaskEntity> {
                override fun match(t: TaskEntity): Boolean {
                    return t.isFinished == 0
                }
            })
        }
        val sortType = if (mFilterOption.reverseSort)
            SortType.reverse(mFilterOption.sortType)
        else
            mFilterOption.sortType
        val filteredTasks = TaskManager.getTasks(filterChain)
        val sortedList = when (sortType) {
            SortType.CreationAsc -> filteredTasks.sortedBy { it.createTime }
            SortType.CreationDesc -> filteredTasks.sortedByDescending { it.createTime }
            SortType.ModifiedAsc -> filteredTasks.sortedBy { it.lastModifiedTime }
            SortType.ModifiedDesc -> filteredTasks.sortedByDescending { it.lastModifiedTime }
            SortType.DeadlineAsc -> filteredTasks.sortedBy { it.deadline }
            SortType.DeadlineDesc -> filteredTasks.sortedByDescending { it.deadline }
        }
        adapter.itemList = sortedList
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: Any?) {
        when (event) {
            is TaskListChangeEvent -> update()
        }
    }


}
