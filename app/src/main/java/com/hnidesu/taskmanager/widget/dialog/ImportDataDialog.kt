package com.hnidesu.taskmanager.widget.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.database.TaskEntity
import com.hnidesu.taskmanager.manager.TaskManager
import com.hnidesu.taskmanager.util.ToastUtil

class ImportDataDialog(context: Context, taskItems: List<TaskEntity>) : Dialog(context) {
    private val btnAppend: Button
    private val btnOverride: Button
    private val textViewImportInfo: TextView

    init {
        setContentView(
            LayoutInflater.from(context).inflate(R.layout.dialog_import_data,null, false)
        )
        textViewImportInfo = findViewById<View>(R.id.textView_importInfo) as TextView
        btnOverride = findViewById<View>(R.id.btn_override) as Button
        btnAppend = findViewById<View>(R.id.btn_append) as Button
        setOnDismissListener {
            ToastUtil.toastLong(context,R.string.cancelled)
        }
        btnAppend.setOnClickListener {
            try {
                TaskManager.addTasks(taskItems)
                ToastUtil.toastLong(context,R.string.import_succeed)
            } catch (e: Exception) {
                ToastUtil.toastLong(context,R.string.import_failed)
            }
            dismiss()
        }
        btnOverride.setOnClickListener {
            try {
                TaskManager.clear()
                TaskManager.addTasks(taskItems)
                ToastUtil.toastLong(context, R.string.import_succeed)
            } catch (e: Exception) {
                ToastUtil.toastLong(context, R.string.import_failed)
            }
            dismiss()
        }

        textViewImportInfo.text = String.format(getContext().getString(R.string.notice_import_data), taskItems.size)
    }


}
