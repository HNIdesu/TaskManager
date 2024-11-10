package com.hnidesu.taskmanager.widget.view

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

@SuppressLint("ClickableViewAccessibility")
class TimeEditView(context: Context, attrs: AttributeSet? = null) : AppCompatEditText(
    context, attrs
) {
    var time: LocalTime = LocalTime.now()
        set(value) {
            setText(DateTimeFormatter.ofPattern("HH:mm").format(value))
            field = value
        }
    private var mTouchFlag = false

    init {
        setText(DateTimeFormatter.ofPattern("HH:mm").format(time))
        setOnTouchListener { _, _ ->
            if (mTouchFlag) {
                return@setOnTouchListener false
            }
            mTouchFlag = true
            val dialog = TimePickerDialog(context, { timePicker, _, _ ->
                time = LocalTime.of(timePicker.hour, timePicker.minute)
            }, time.hour, time.minute, true)
            dialog.setOnDismissListener { _ ->
                mTouchFlag = false
            }
            dialog.show()
            return@setOnTouchListener true
        }
    }

}
