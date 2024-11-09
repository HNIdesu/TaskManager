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
import android.widget.CheckBox
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.activity.EditTaskActivity
import com.hnidesu.taskmanager.adapter.TaskListAdapter
import com.hnidesu.taskmanager.base.SortType
import com.hnidesu.taskmanager.base.TaskCollection
import com.hnidesu.taskmanager.base.filter.Filter
import com.hnidesu.taskmanager.base.filter.FilterChain
import com.hnidesu.taskmanager.database.TaskEntity
import com.hnidesu.taskmanager.manager.SettingManager
import com.hnidesu.taskmanager.manager.TaskManager
import com.hnidesu.taskmanager.util.ToastUtil
import com.hnidesu.taskmanager.widget.dialog.SetTaskDialogFactory
import java.util.Date
import kotlin.concurrent.thread

class TaskListFragment : Fragment() {
    private var mAutoUpdate = false
    val mDataSource = TaskCollection()
    private val mFilterOption = object {
        var hideExpiredTask:Boolean=false
        var hideFinishedTask:Boolean=false
        var reverseSort:Boolean=false
        var sortType:SortType=SortType.CreationAsc
    }
    private var mItemView: View? = null
    var mRecyclerViewAdapter: TaskListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val itemView = inflater.inflate(R.layout.fragment_task_list, container, false)
        mItemView = itemView
        val context = requireContext()
        val adapter = TaskListAdapter(context)
        adapter.taskSource=mDataSource
        adapter.onTaskFinishChangeListener = object : TaskListAdapter.OnTaskFinishChangeListener{
            override fun onFinish(taskItem: TaskEntity?) {
                thread{
                    if (TaskManager.setTaskFinish(context,taskItem!!, true)) {
                        requireActivity().runOnUiThread { update(null) }
                    }
                }
            }

            override fun onNotFinish(taskItem: TaskEntity?) {
                thread{
                    if (TaskManager.setTaskFinish(context,taskItem!!, false)) {
                        requireActivity().runOnUiThread { update(null) }
                    }
                }
            }

        }
        mRecyclerViewAdapter = adapter
        itemView.findViewById<RecyclerView>(R.id.recyclerview).apply {
            setAdapter(mRecyclerViewAdapter)
            registerForContextMenu(this)
        }

        val drawerLayout: DrawerLayout =
            itemView.findViewById<View>(R.id.drawer_layout) as DrawerLayout
        itemView.findViewById<View>(R.id.button_filter_task).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }
        itemView.findViewById<View>(R.id.button_add_task)
            .setOnClickListener {
                SetTaskDialogFactory(
                    context,
                    object : SetTaskDialogFactory.OnFinishListener {
                        override fun onCancel() {}

                        override fun onSet(title: String, date: Date) {
                            val current = System.currentTimeMillis()
                            val taskEntity = TaskEntity(current, "", title, 0, date.time, current, 0)
                            TaskManager.addTask(context,taskEntity)
                            val insertIndex = mDataSource.add(taskEntity)
                            val taskListAdapter = mRecyclerViewAdapter
                            taskListAdapter?.notifyItemInserted(insertIndex)
                            ToastUtil.toastShort(context,R.string.add_success)
                            val intent = Intent(context, EditTaskActivity::class.java)
                            intent.putExtra("task_id", taskEntity.createTime)
                            startActivity(intent)
                        }

                    },
                    getString(R.string.add_task)
                ).create().show()
            }
        val sharedPrefs = SettingManager.getDefaultSetting(context)
        itemView.findViewById<SwitchCompat>(R.id.switch_hide_expired_task).apply {
            setOnCheckedChangeListener { _, z ->
                mFilterOption.hideExpiredTask = z
                sharedPrefs.edit().putBoolean("hide_expired_task", z).apply()
                if (mAutoUpdate) {
                    update(null)
                }
            }
            setChecked(sharedPrefs.getBoolean("hide_expired_task", false))
        }

        itemView.findViewById<SwitchCompat>(R.id.switch_hide_finished_task).apply {
            setOnCheckedChangeListener { _, z ->
                mFilterOption.hideFinishedTask = z
                sharedPrefs.edit().putBoolean("hide_finished_task", z).apply()
                if (mAutoUpdate) {
                    update(null)
                }
            }
            setChecked(sharedPrefs.getBoolean("hide_finished_task", false))
        }

        itemView.findViewById<CheckBox>(R.id.checkbox_reverse).apply {
            setOnCheckedChangeListener { _, z ->
                if (mFilterOption.reverseSort != z) {
                    mFilterOption.reverseSort = z
                    sharedPrefs.edit().putBoolean("reverse_sort", z).apply()
                    if (mAutoUpdate)
                        update(null)
                }
            }
            setChecked(sharedPrefs.getBoolean("reverse_sort", false))
        }

        val binder= mapOf(
            0 to SortType.CreationAsc,
            1 to SortType.ModifiedAsc,
            2 to SortType.DeadlineAsc
        )

        itemView.findViewById<Spinner>(R.id.spinner_sort).apply {
            setAdapter(
                ArrayAdapter(
                    requireContext(),
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
                    mFilterOption.sortType=sortType
                    if (binder.containsKey(position)) {
                        sharedPrefs.edit().putInt("sort_by", position).apply()
                    }
                    if (mAutoUpdate)
                        update(null)
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
            setSelection(sharedPrefs.getInt("sort_by", 0))
        }

        update(null)
        mAutoUpdate = true
        return itemView
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = MenuInflater(context)
        inflater.inflate(R.menu.menu_context_all_tesk_single, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.option_delete -> {
                val adapter = mRecyclerViewAdapter ?: return false
                AlertDialog.Builder(requireContext()).setMessage(R.string.if_sure_to_delete)
                    .setNegativeButton(R.string.cancel
                    ) { _, _ ->
                        ToastUtil.toastShort(
                            requireContext(),
                            R.string.cancelled
                        )
                    }.setPositiveButton(R.string.ok
                    ) { _, _ ->
                        val selectedIndex = adapter.selectedIndex
                        val selectedItem=adapter.taskSource?.get(selectedIndex)
                        if (selectedItem != null) {
                            TaskManager.deleteTask(requireContext(), selectedItem)
                            adapter.taskSource?.removeAt(selectedIndex)
                            adapter.notifyItemRemoved(selectedIndex)
                        }
                    }.setOnCancelListener { }.create().show()
                return true
            }

            R.id.option_edit -> {
                val adapter = mRecyclerViewAdapter ?: return false
                val selectedIndex = adapter.selectedIndex
                val selectedItem= adapter.taskSource?.get(selectedIndex)
                if(selectedItem!=null){
                    SetTaskDialogFactory(
                        requireContext(),
                        Date(selectedItem.deadline),
                        selectedItem.title,
                        object :SetTaskDialogFactory.OnFinishListener{
                            override fun onCancel() {}
                            override fun onSet(title: String, date: Date) {
                                selectedItem.deadline = date.time
                                selectedItem.title = title
                                selectedItem.lastModifiedTime = System.currentTimeMillis()
                                TaskManager.updateTask(requireContext(),selectedItem)
                                update(null)
                            }
                        },
                        getString(R.string.edit_task)
                    ).create().show()
                }
                return true
            }

            else -> return super.onContextItemSelected(item)
        }
    }

    fun update(status: Any?) {
        val adapter: TaskListAdapter? = mRecyclerViewAdapter
        if (adapter != null) {
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
            thread{
                mDataSource.clear()
                val taskCollection = mDataSource
                val sortType = if (mFilterOption.reverseSort)
                    SortType.reverse(mFilterOption.sortType)
                else
                    mFilterOption.sortType
                taskCollection.sortType = sortType
                mDataSource.replace(TaskManager.getTasks(requireContext(),filterChain).toMutableList())
                requireActivity().runOnUiThread { adapter.notifyDataSetChanged() }
            }
        }
    }


}
