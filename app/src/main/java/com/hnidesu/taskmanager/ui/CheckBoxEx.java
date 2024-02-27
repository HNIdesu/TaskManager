package com.hnidesu.taskmanager.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.hnidesu.taskmanager.R;


public class CheckBoxEx extends AppCompatCheckBox {
    public interface OnCheckChangeListener{
        void onChecked();
        void onNotChecked();
    }
    private OnCheckChangeListener onCheckChangeListener;

    public void setOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
        this.onCheckChangeListener = onCheckChangeListener;
    }

    private boolean mIsUserClick;

    public CheckBoxEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener((v, event) -> {
            if(event.getAction()==MotionEvent.ACTION_DOWN)
                mIsUserClick =true;
            return false;
        });
        this.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(!mIsUserClick)
                return;
            mIsUserClick=false;
            if(isChecked){
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setMessage(R.string.whether_check_finished)
                        .setNegativeButton(R.string.cancel, (dialogInterface, i) -> CheckBoxEx.this.setChecked(false))
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    if (onCheckChangeListener != null)
                        onCheckChangeListener.onChecked();
                }).create().show();
            }else{
                new AlertDialog.Builder(context)
                        .setMessage(R.string.whether_cancel_check_finished)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    if (onCheckChangeListener != null)
                        onCheckChangeListener.onNotChecked();
                }).setNegativeButton(R.string.cancel, (dialogInterface, i) -> CheckBoxEx.this.setChecked(true))
                        .create().show();
            }
        });
    }


}
