package com.hnidesu.taskmanager.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.hnidesu.taskmanager.dialog.SimplePopupWindow;

public class CheckBoxEx extends AppCompatCheckBox {
    private View popupWindowContainer;

    public interface OnCheckChangeListener{
        void onChecked();
        void onNotChecked();
    }
    private OnCheckChangeListener onCheckChangeListener;

    public void setOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
        this.onCheckChangeListener = onCheckChangeListener;
    }

    private boolean isUserClick;
    public void setPopupWindowContainer(View popupWindowContainer) {
        this.popupWindowContainer = popupWindowContainer;
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                    isUserClick=true;
                return false;
            }
        });
        this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isUserClick)
                    return;
                isUserClick=false;
                SimplePopupWindow popupWindow;
                if(isChecked){
                    popupWindow = new SimplePopupWindow(getContext(), "是否确定已完成", new SimplePopupWindow.OnDialogResultListener() {
                        @Override
                        public void onCancel(SimplePopupWindow dialog) {
                            CheckBoxEx.this.setChecked(false);
                        }

                        @Override
                        public void onOk(SimplePopupWindow dialog) {
                            if (onCheckChangeListener != null)
                                onCheckChangeListener.onChecked();
                        }
                    });
                }else{

                    popupWindow = new SimplePopupWindow(getContext(), "是否确定取消已完成", new SimplePopupWindow.OnDialogResultListener() {
                        @Override
                        public void onCancel(SimplePopupWindow dialog) {
                            CheckBoxEx.this.setChecked(true);
                        }

                        @Override
                        public void onOk(SimplePopupWindow dialog) {
                            if (onCheckChangeListener != null)
                                onCheckChangeListener.onNotChecked();
                        }
                    });

                }
                popupWindow.popup(popupWindowContainer);
            }
        });

    }

    public CheckBoxEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


}
