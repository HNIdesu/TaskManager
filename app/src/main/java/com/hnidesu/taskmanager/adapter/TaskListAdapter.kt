package com.hnidesu.taskmanager.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.activity.EditTaskActivity
import com.hnidesu.taskmanager.base.TaskCollection
import com.hnidesu.taskmanager.database.TaskEntity
import com.hnidesu.taskmanager.widget.view.CheckBoxEx
import com.hnidesu.taskmanager.widget.view.CheckBoxEx.OnCheckChangeListener
import java.text.SimpleDateFormat
import java.util.Locale


class TaskListAdapter(private val mContext: Context) : RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {
    private var mTaskSource: TaskCollection? = null
    @JvmField
    var onTaskFinishChangeListener: OnTaskFinishChangeListener? = null
    var selectedIndex: Int=-1
        private set
    @JvmField
    var selectedItem: TaskEntity? = null

    interface OnTaskFinishChangeListener {
        fun onFinish(taskItem: TaskEntity?)
        fun onNotFinish(taskItem: TaskEntity?)
    }

    fun setDataSource(taskItemList: TaskCollection?) {
        mTaskSource = taskItemList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deadlineView: TextView
        val finishCheckBox: CheckBoxEx
        val titleView: TextView

        init {
            titleView = itemView.findViewById<View>(R.id.textview_title) as TextView
            deadlineView = itemView.findViewById<View>(R.id.textview_deadline) as TextView
            finishCheckBox = itemView.findViewById<View>(R.id.checkbox_is_finished) as CheckBoxEx
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root = LayoutInflater.from(mContext).inflate(R.layout.item_task, parent, false)
        return ViewHolder(root)
    }


    override fun onBindViewHolder(holder: ViewHolder, p: Int) {
        val position=holder.adapterPosition
        val item=mTaskSource?.get(position) ?: return
        holder.finishCheckBox.setOnCheckChangeListener(object : OnCheckChangeListener {
            override fun onChecked() {
                onTaskFinishChangeListener?.onFinish(item)
            }
            override fun onNotChecked() {
                onTaskFinishChangeListener?.onNotFinish(item)
            }
        })
        holder.itemView.setOnClickListener { _ ->
            val intent = Intent(mContext, EditTaskActivity::class.java)
            intent.putExtra("task_id", item.createTime)
            mContext.startActivity(intent)
        }
        holder.titleView.text = item.title

        holder.deadlineView.text ="${mContext.getString(R.string.deadline)}:${SimpleDateFormat(
            "yyyy-MM-dd HH:mm", Locale.US
        ).format(item.deadline)}"
        holder.finishCheckBox.setChecked(item.isFinished == 1)
        holder.itemView.setOnLongClickListener(object : OnLongClickListener {
            override fun onLongClick(view: View): Boolean {
                selectedIndex = position
                selectedItem = item
                return false
            }
        })
    }


    override fun getItemCount(): Int {
        val taskCollection = mTaskSource
        return taskCollection?.size ?: 0
    }


}
