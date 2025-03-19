package com.hnidesu.taskmanager.widget.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AlertDialog
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.database.TaskTitleEntity
import com.hnidesu.taskmanager.databinding.ItemTaskTitleBinding
import com.hnidesu.taskmanager.databinding.WindowSetTaskBinding
import com.hnidesu.taskmanager.manager.DatabaseManager
import com.hnidesu.taskmanager.util.HashUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime

class SetTaskDialogFactory(
    private val mContext: Context,
    private val mDefaultDateTime: LocalDateTime,
    private val mDefaultTitle: String,
    private val mOnFinishListener: OnFinishListener,
    private val mTitle: String
) {
    interface OnFinishListener {
        fun onCancel()
        fun onSet(title: String, date: LocalDateTime)
    }

    constructor(context: Context, listener: OnFinishListener, title: String) :
            this(
                context,
                LocalDateTime.now(),
                context.getString(R.string.untitled_task),
                listener,
                title
            )

    private class TaskTitleAdapter : BaseAdapter(), Filterable {
        var itemList: List<TaskTitleEntity> = listOf()
            set(value) {
                field = value
                notifyDataSetChanged()
            }
        private var mOnTitleSelect: ((title: String) -> Unit)? = null
        override fun getCount(): Int = itemList.size
        override fun getItem(position: Int): Any = itemList[position]
        override fun getItemId(position: Int): Long = itemList[position].id.toLong()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val binding = ItemTaskTitleBinding.inflate(LayoutInflater.from(parent!!.context))
            val item = getItem(position) as TaskTitleEntity
            binding.text.text = item.text
            binding.btnDelete.setOnClickListener {
                runBlocking {
                    DatabaseManager.myDatabase.taskTitleDao.delete(item)
                }
            }
            binding.text.setOnClickListener {
                mOnTitleSelect?.invoke(item.text)
            }
            return binding.root
        }

        fun setOnTitleSelectListener(listener: (title: String) -> Unit) {
            mOnTitleSelect = listener
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    return FilterResults()
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                }
            }
        }
    }

    fun create(): Dialog {
        val binding = WindowSetTaskBinding.inflate(LayoutInflater.from(mContext))
        binding.edittextTitle.setText(mDefaultTitle)
        binding.edittextTitle.setSelectAllOnFocus(true)
        binding.edittextDeadlineDate.date = mDefaultDateTime.toLocalDate()
        binding.edittextDeadlineTime.time = mDefaultDateTime.toLocalTime()
        val adapter = TaskTitleAdapter()
        binding.edittextTitle.setAdapter(adapter)
        val job = CoroutineScope(Dispatchers.IO).launch {
            DatabaseManager.myDatabase.taskTitleDao.getAll().collectLatest {
                withContext(Dispatchers.Main) { adapter.itemList = it }
            }
        }
        adapter.setOnTitleSelectListener {
            binding.edittextTitle.setText(it)
            binding.edittextTitle.setSelection(it.length)
        }
        return AlertDialog.Builder(mContext)
            .setCancelable(false).setView(binding.root)
            .setPositiveButton(R.string.ok) { _: DialogInterface?, _: Int ->
                val title = binding.edittextTitle.text.toString()
                mOnFinishListener.onSet(
                    title,
                    LocalDateTime.of(
                        binding.edittextDeadlineDate.date,
                        binding.edittextDeadlineTime.time
                    )
                )
                runBlocking {
                    DatabaseManager.myDatabase.taskTitleDao.insertOrUpdate(
                        TaskTitleEntity(
                            id = HashUtil.crc32Digest(title),
                            text = title,
                            updateTime = System.currentTimeMillis()
                        )
                    )
                }
            }.setOnDismissListener { job.cancel() }
            .setNegativeButton(R.string.cancel) { _: DialogInterface?, _: Int -> mOnFinishListener.onCancel() }
            .setTitle(mTitle)
            .create()
    }
}
