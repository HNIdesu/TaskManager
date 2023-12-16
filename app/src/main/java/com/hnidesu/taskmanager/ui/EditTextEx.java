package com.hnidesu.taskmanager.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

public class EditTextEx extends AppCompatEditText {
    public interface TextChangeListener{
        void onTextChange(View view, CharSequence text);
    }
    private TextChangeListener mTextChangeListener;
    public EditTextEx(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTextChangeListener(TextChangeListener textChangeListener) {
        this.mTextChangeListener = textChangeListener;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if(mTextChangeListener!=null)
            mTextChangeListener.onTextChange(this,text);
    }
}
