package com.hnidesu.taskmanager.widget.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.database.TaskEntity
import com.hnidesu.taskmanager.databinding.DialogImportDataBinding
import com.hnidesu.taskmanager.manager.TaskManager
import com.hnidesu.taskmanager.util.ToastUtil

class ImportDataDialog(context: Context, taskItems: List<TaskEntity>) : Dialog(context) {
    private val mDialogImportDataBinding: DialogImportDataBinding =
        DialogImportDataBinding.inflate(LayoutInflater.from(context))

    init {
        val binding = mDialogImportDataBinding
        setContentView(
            mDialogImportDataBinding.root
        )
        setOnDismissListener {
            ToastUtil.toastLong(context, R.string.cancelled)
        }
        binding.btnAppend.setOnClickListener {
            try {
                TaskManager.addTasks(taskItems)
                ToastUtil.toastLong(context, R.string.import_succeed)
            } catch (e: Exception) {
                ToastUtil.toastLong(context, R.string.import_failed)
            }
            dismiss()
        }
        binding.btnOverride.setOnClickListener {
            try {
                TaskManager.clear()
                TaskManager.addTasks(taskItems)
                ToastUtil.toastLong(context, R.string.import_succeed)
            } catch (e: Exception) {
                ToastUtil.toastLong(context, R.string.import_failed)
            }
            dismiss()
        }
        binding.textViewImportInfo.text =
            String.format(getContext().getString(R.string.notice_import_data), taskItems.size)
    }


}
