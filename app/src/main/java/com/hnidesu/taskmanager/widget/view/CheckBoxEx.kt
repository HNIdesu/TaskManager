package com.hnidesu.taskmanager.widget.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import com.hnidesu.taskmanager.R

@SuppressLint("ClickableViewAccessibility")
class CheckBoxEx(context: Context, attrs: AttributeSet?) : AppCompatCheckBox(
    context, attrs
) {
    private var mIsUserClick = false
    private var onCheckChangeListener: OnCheckChangeListener? = null

    interface OnCheckChangeListener {
        fun onChecked()
        fun onNotChecked()
    }

    init {
        setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == 0) {
                mIsUserClick = true
            }
            false
        }
        setOnCheckedChangeListener(object : OnCheckedChangeListener {
            override fun onCheckedChanged(compoundButton: CompoundButton, z: Boolean) {
                if (mIsUserClick) {
                    mIsUserClick = false
                    if (isChecked) {
                        AlertDialog.Builder(context).setCancelable(false)
                            .setMessage(R.string.whether_check_finished).setNegativeButton(
                                R.string.cancel,
                                object : DialogInterface.OnClickListener {
                                    override fun onClick(dialogInterface: DialogInterface, i: Int) {
                                        setChecked(false)
                                    }
                                }).setPositiveButton(R.string.ok, object : DialogInterface.OnClickListener {
                                override fun onClick(dialogInterface: DialogInterface, i: Int) {
                                    onCheckChangeListener?.onChecked()
                                }
                            }).create().show()
                        return
                    }
                    AlertDialog.Builder(context)
                        .setMessage(R.string.whether_cancel_check_finished).setCancelable(false)
                        .setPositiveButton(R.string.ok, object : DialogInterface.OnClickListener {
                            override fun onClick(dialogInterface: DialogInterface, i: Int) {
                                onCheckChangeListener?.onNotChecked()
                            }
                        }).setNegativeButton(R.string.cancel, object : DialogInterface.OnClickListener {
                            override fun onClick(dialogInterface: DialogInterface, i: Int) {
                                setChecked(false)
                            }
                        }).create().show()
                }
            }
        })
    }

    fun setOnCheckChangeListener(onCheckChangeListener: OnCheckChangeListener?) {
        this.onCheckChangeListener = onCheckChangeListener
    }
}
