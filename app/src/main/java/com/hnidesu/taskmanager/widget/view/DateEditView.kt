package com.hnidesu.taskmanager.widget.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import java.util.Date
import kotlin.jvm.internal.Intrinsics


@SuppressLint("ClickableViewAccessibility")
class DateEditView(context: Context, attrs: AttributeSet?) : AppCompatEditText(
    context, attrs
) {
    private var mDate = 0
    private var mMonth = 0
    private var mTouchFlag = false
    private var mYear: Int

    init {
        mYear = -1
        setOnTouchListener { _, motionEvent ->
            if (mTouchFlag)
                return@setOnTouchListener false
            mTouchFlag = true
            if (mYear == -1) {
                val curDate = Date(System.currentTimeMillis())
                setDate(curDate.year, curDate.month, curDate.date)
            }
            val dialog = DatePickerDialog(context, { datePicker, i, i2, i3 ->
                val format = String.format(
                    "%04d-%02d-%02d",
                    arrayOf(
                        datePicker.year,
                        datePicker.month + 1,
                        datePicker.dayOfMonth
                    )
                )
                setText(format)
                setDate(datePicker.year - 1900, datePicker.month, datePicker.dayOfMonth)
            }, mYear + 1900, mMonth, mDate)
            dialog.setOnDismissListener { dialogInterface ->
                mTouchFlag = false
            }
            dialog.show()
            return@setOnTouchListener true
        }
    }

    fun getDate(): Long {
        val date = Date(0L)
        date.year = mYear
        date.setMonth(mMonth)
        date.date = this.mDate
        return date.time
    }

    fun setDate(year: Int, month: Int, date: Int) {
        this.mYear = year
        this.mMonth = month
        this.mDate = date
        val format =
            String.format("%04d-%02d-%02d", *arrayOf<Any>(year + 1900, month + 1, date).copyOf(3))
        Intrinsics.checkNotNullExpressionValue(format, "format(...)")
        setText(format)
    }


}
