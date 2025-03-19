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
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.activity.EditTaskActivity
import com.hnidesu.taskmanager.adapter.TaskListAdapter
import com.hnidesu.taskmanager.base.SortType
import com.hnidesu.taskmanager.database.TaskEntity
import com.hnidesu.taskmanager.databinding.FragmentTaskListBinding
import com.hnidesu.taskmanager.manager.DatabaseManager
import com.hnidesu.taskmanager.manager.SettingManager
import com.hnidesu.taskmanager.util.ToastUtil
import com.hnidesu.taskmanager.widget.dialog.SetTaskDialogFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private var mFetchTaskListJob: Job? = null;

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
                CoroutineScope(Dispatchers.IO).launch {
                    DatabaseManager.myDatabase.taskDao.updateTask(
                        taskItem.copy(isFinished = 1, lastModifiedTime = System.currentTimeMillis())
                    )
                }
            }

            override fun onNotFinish(taskItem: TaskEntity?) {
                if (taskItem == null)
                    return
                CoroutineScope(Dispatchers.IO).launch {
                    DatabaseManager.myDatabase.taskDao.updateTask(
                        taskItem.copy(isFinished = 0, lastModifiedTime = System.currentTimeMillis())
                    )
                }
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
                        CoroutineScope(Dispatchers.IO).launch {
                            DatabaseManager.myDatabase.taskDao.insertTask(taskEntity)
                        }
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
                    R.layout.item_sort_option,
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
                        CoroutineScope(Dispatchers.IO).launch {
                            DatabaseManager.myDatabase.taskDao.deleteTask(selectedItem)
                        }
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
                            CoroutineScope(Dispatchers.IO).launch {
                                DatabaseManager.myDatabase.taskDao.updateTask(newItem)
                            }
                        }
                    },
                    getString(R.string.edit_task)
                ).create().show()
                return true
            }

            else -> return super.onContextItemSelected(item)
        }
    }

    private fun update() {
        val adapter = mFragmentTaskListBinding?.recyclerview?.adapter as? TaskListAdapter ?: return
        mFetchTaskListJob?.cancel()
        val builder= SupportSQLiteQueryBuilder.builder("tasks")
        val selections = arrayListOf<String>()
        val selectionArgs = arrayListOf<String>()
        if (mFilterOption.hideFinishedTask) {
            selections.add("is_finished = 0")
        }
        if (mFilterOption.hideExpiredTask) {
            selections.add("deadline > ?")
            selectionArgs.add(System.currentTimeMillis().toString())
        }
        builder.selection(selections.joinToString(" and "),selectionArgs.toArray())
        val sortType = if (mFilterOption.reverseSort)
            SortType.reverse(mFilterOption.sortType)
        else
            mFilterOption.sortType
        when(sortType){
            SortType.CreationAsc -> builder.orderBy("create_time ASC")
            SortType.CreationDesc -> builder.orderBy("create_time DESC")
            SortType.ModifiedAsc -> builder.orderBy("last_modified_time ASC")
            SortType.ModifiedDesc -> builder.orderBy("last_modified_time DESC")
            SortType.DeadlineAsc -> builder.orderBy("deadline ASC")
            SortType.DeadlineDesc -> builder.orderBy("deadline DESC")
        }
        val sql = builder.create()
        mFetchTaskListJob = CoroutineScope(Dispatchers.IO).launch {
            DatabaseManager.myDatabase.taskDao.getTasks(sql).collectLatest{
                withContext(Dispatchers.Main){
                    adapter.itemList = it
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mFetchTaskListJob?.cancel()
    }
}
