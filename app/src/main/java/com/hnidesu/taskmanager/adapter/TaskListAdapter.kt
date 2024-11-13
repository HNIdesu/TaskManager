package com.hnidesu.taskmanager.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.activity.EditTaskActivity
import com.hnidesu.taskmanager.database.TaskEntity
import com.hnidesu.taskmanager.widget.view.CheckBoxEx
import com.hnidesu.taskmanager.widget.view.CheckBoxEx.OnCheckChangeListener
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter


class TaskListAdapter(private val mContext: Context) :
    RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {
    var itemList: List<TaskEntity> = listOf()
        set(value) {
            val result=DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return field.size
                }
                override fun getNewListSize(): Int {
                    return value.size
                }
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return field[oldItemPosition].createTime == value[newItemPosition].createTime
                }
                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return field[oldItemPosition] == value[newItemPosition]
                }
            })
            field = value
            result.dispatchUpdatesTo(this)
        }
    var selectedIndex = -1
    var onTaskFinishChangeListener: OnTaskFinishChangeListener? = null

    interface OnTaskFinishChangeListener {
        fun onFinish(taskItem: TaskEntity?)
        fun onNotFinish(taskItem: TaskEntity?)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deadlineView: TextView = itemView.findViewById<View>(R.id.textview_deadline) as TextView
        val finishCheckBox: CheckBoxEx = itemView.findViewById<View>(R.id.checkbox_is_finished) as CheckBoxEx
        val titleView: TextView = itemView.findViewById<View>(R.id.textview_title) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val root = LayoutInflater.from(mContext).inflate(R.layout.item_task, parent, false)
        return ViewHolder(root)
    }


    override fun onBindViewHolder(holder: ViewHolder, p: Int) {
        val item= itemList[p]
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
        val deadlineSting=Instant.ofEpochMilli(item.deadline).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        holder.deadlineView.text = String.format(mContext.getString(R.string.deadline_format), deadlineSting)
        holder.finishCheckBox.setChecked(item.isFinished == 1)
        holder.itemView.setOnLongClickListener {
            selectedIndex = holder.adapterPosition
            false
        }
    }


    override fun getItemCount(): Int {
        return itemList.size
    }
}
