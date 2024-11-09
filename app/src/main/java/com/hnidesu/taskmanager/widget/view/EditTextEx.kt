package com.hnidesu.taskmanager.widget.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class EditTextEx(context: Context, attrs: AttributeSet?) : AppCompatEditText(
    context, attrs
) {
    private var mTextChangeListener: TextChangeListener? = null

    interface TextChangeListener {
        fun onTextChange(view: View?, text: CharSequence?)
    }

    fun setTextChangeListener(textChangeListener: TextChangeListener?) {
        mTextChangeListener = textChangeListener
    }

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        mTextChangeListener?.onTextChange(this, text)
    }
}
