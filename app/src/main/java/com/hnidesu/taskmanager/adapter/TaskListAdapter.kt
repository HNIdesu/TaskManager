package com.hnidesu.taskmanager.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.activity.EditTaskActivity
import com.hnidesu.taskmanager.database.TaskEntity
import com.hnidesu.taskmanager.databinding.ItemTaskBinding
import com.hnidesu.taskmanager.widget.view.CheckBoxEx.OnCheckChangeListener
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter


class TaskListAdapter : RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {
    var itemList: List<TaskEntity> = listOf()
        set(value) {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize(): Int {
                    return field.size
                }

                override fun getNewListSize(): Int {
                    return value.size
                }

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return field[oldItemPosition].createTime == value[newItemPosition].createTime
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
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

    class ViewHolder(
        val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    private fun getDeadlineText(context: Context, deadline: Long): CharSequence? {
        val current = System.currentTimeMillis()
        val minutes = (deadline - current) / 60000
        val sb = SpannableStringBuilder()

        when {
            minutes < 0 -> {
                sb.append(context.getString(R.string.deadline_expired))
                sb.setSpan(
                    ForegroundColorSpan(Color.parseColor("#757575")),
                    0,
                    sb.length,
                    SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }

            minutes < 1 -> {
                sb.append(context.getString(R.string.deadline_less_than_one_minute))
                sb.setSpan(
                    ForegroundColorSpan(Color.parseColor("#D32F2F")),
                    0,
                    sb.length,
                    SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }

            minutes < 60 -> {
                sb.append(
                    String.format(
                        context.getString(R.string.deadline_remaining_minutes),
                        minutes
                    )
                )
                sb.setSpan(
                    ForegroundColorSpan(Color.parseColor("#FBC02D")),
                    0,
                    sb.length,
                    SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }

            minutes < 24 * 60 -> {
                sb.append(
                    String.format(
                        context.getString(R.string.deadline_remaining_hours),
                        minutes / 60
                    )
                )
                sb.setSpan(
                    ForegroundColorSpan(Color.parseColor("#388E3C")),
                    0,
                    sb.length,
                    SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }

            minutes < 30 * 24 * 60 -> {
                sb.append(
                    String.format(
                        context.getString(R.string.deadline_remaining_days),
                        minutes / (24 * 60)
                    )
                )
                sb.setSpan(
                    ForegroundColorSpan(Color.parseColor("#1976D2")),
                    0,
                    sb.length,
                    SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }

            else -> return null
        }

        return sb
    }


    override fun onBindViewHolder(holder: ViewHolder, p: Int) {
        val item = itemList[p]
        val binding = holder.binding
        val context = holder.itemView.context
        binding.checkboxIsFinished.isChecked = item.isFinished == 1
        binding.checkboxIsFinished.setOnCheckChangeListener(object : OnCheckChangeListener {
            override fun onChecked() {
                onTaskFinishChangeListener?.onFinish(item)
            }

            override fun onNotChecked() {
                onTaskFinishChangeListener?.onNotFinish(item)
            }
        })
        binding.textviewTitle.text = item.title
        binding.textviewDeadline.text = getDeadlineText(context, item.deadline) ?: String.format(
            context.getString(R.string.deadline_format),
            Instant.ofEpochMilli(item.deadline).atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        )
        binding.root.setOnClickListener { _ ->
            val intent = Intent(context, EditTaskActivity::class.java)
            intent.putExtra("task_id", item.createTime)
            context.startActivity(intent)
        }
        binding.root.setOnLongClickListener {
            selectedIndex = holder.adapterPosition
            false
        }
    }


    override fun getItemCount(): Int {
        return itemList.size
    }
}
