package com.hnidesu.taskmanager.widget.view

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import java.util.Date


@SuppressLint("ClickableViewAccessibility")
class TimeEditView(context: Context, attrs: AttributeSet?) : AppCompatEditText(
    context, attrs
) {
    private var hour: Int
    private var mTouchFlag = false
    private var minute = 0

    init {
        hour = -1
        setOnTouchListener { _, _ ->
            if (mTouchFlag) {
                return@setOnTouchListener false
            }
            mTouchFlag = true
            if (hour == -1) {
                val date = Date(System.currentTimeMillis())
                setTime(date.hours, date.minutes)
            }
            val dialog = TimePickerDialog(context, { timePicker, _, _ ->
                setText(String.format("%02d:%02d", timePicker.hour, timePicker.minute))
                setTime(timePicker.hour, timePicker.minute)
            }, hour, minute, true)
            dialog.setOnDismissListener { _ ->
                mTouchFlag = false
            }
            dialog.show()
            return@setOnTouchListener true
        }
    }

    val time: Long
        get() {
            val date = Date(0L)
            date.hours = hour
            date.minutes = minute
            return date.time
        }

    fun setTime(hour: Int, minute: Int) {
        this.hour = hour
        this.minute = minute
        setText(String.format("%02d:%02d", hour, minute))
    }

}
