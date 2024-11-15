package com.hnidesu.taskmanager.widget.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.databinding.WindowSetTaskBinding
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

    fun create(): Dialog {
        val binding = WindowSetTaskBinding.inflate(LayoutInflater.from(mContext))
        binding.edittextTitle.setText(mDefaultTitle)
        binding.edittextDeadlineDate.date = mDefaultDateTime.toLocalDate()
        binding.edittextDeadlineTime.time = mDefaultDateTime.toLocalTime()
        return AlertDialog.Builder(mContext)
            .setCancelable(false).setView(binding.root)
            .setPositiveButton(R.string.ok) { _: DialogInterface?, _: Int ->
                mOnFinishListener.onSet(
                    binding.textviewTitle.text.toString(),
                    LocalDateTime.of(
                        binding.edittextDeadlineDate.date,
                        binding.edittextDeadlineTime.time
                    )
                )
            }
            .setNegativeButton(R.string.cancel) { _: DialogInterface?, _: Int -> mOnFinishListener.onCancel() }
            .setTitle(mTitle)
            .create()
    }
}
