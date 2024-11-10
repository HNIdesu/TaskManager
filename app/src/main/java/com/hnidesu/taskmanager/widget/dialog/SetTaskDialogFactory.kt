package com.hnidesu.taskmanager.widget.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.widget.view.DateEditView
import com.hnidesu.taskmanager.widget.view.TimeEditView
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

    constructor(context: Context, listener: OnFinishListener, title: String):
        this(
            context,
            LocalDateTime.now(),
            context.getString(R.string.untitled_task),
            listener,
            title
        )

    fun create(): Dialog {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.window_set_task, null, false)
        val taskTitleView = view.findViewById<EditText>(R.id.edittext_title)
        val deadlineDate = view.findViewById<DateEditView>(R.id.edittext_deadline_date)
        val deadlineTime = view.findViewById<TimeEditView>(R.id.edittext_deadline_time)
        taskTitleView.setText(mDefaultTitle)
        deadlineDate.date = mDefaultDateTime.toLocalDate()
        deadlineTime.time = mDefaultDateTime.toLocalTime()
        return AlertDialog.Builder(mContext)
            .setCancelable(false).setView(view)
            .setPositiveButton(R.string.ok) { _: DialogInterface?, _: Int ->
                mOnFinishListener.onSet(
                    taskTitleView.text.toString(),
                    LocalDateTime.of(deadlineDate.date, deadlineTime.time))
            }
            .setNegativeButton(R.string.cancel) { _: DialogInterface?, _: Int -> mOnFinishListener.onCancel() }
            .setTitle(mTitle)
            .create()
    }
}
