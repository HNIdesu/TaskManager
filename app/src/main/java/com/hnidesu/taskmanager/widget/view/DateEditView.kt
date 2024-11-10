package com.hnidesu.taskmanager.widget.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter


@SuppressLint("ClickableViewAccessibility")
class DateEditView(context: Context, attrs: AttributeSet? = null) : AppCompatEditText(
    context, attrs
) {
    private var mTouchFlag = false
    var date: LocalDate = LocalDate.now()
        set(value) {
            setText(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(value))
            field = value
        }

    init {
        setText(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date))
        setOnTouchListener { _, _ ->
            if (mTouchFlag)
                return@setOnTouchListener false
            mTouchFlag = true
            DatePickerDialog(context, { datePicker, _, _, _ ->
                date = LocalDate.of(datePicker.year, datePicker.month + 1, datePicker.dayOfMonth)
            }, date.year, date.monthValue - 1, date.dayOfMonth).apply {
                setOnDismissListener {
                    mTouchFlag = false
                }
            }.show()
            return@setOnTouchListener true
        }
    }
}
