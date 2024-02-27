package com.hnidesu.taskmanager.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.hnidesu.taskmanager.R;
import com.hnidesu.taskmanager.ui.DateEditView;
import com.hnidesu.taskmanager.ui.TimeEditView;

import java.util.Date;

public class SetTaskDialog extends PopupWindow {

    private OnFinishListener onFinishListener;
    public SetTaskDialog(@NonNull Context ctx, @LayoutRes int resId, OnFinishListener listener){
        super(ctx);
        setFocusable(true);
        this.onFinishListener=listener;
        View view= LayoutInflater.from(ctx).inflate(resId,null);
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        getContentView().findViewById(R.id.button_cancel).setOnClickListener(v -> {
            onFinishListener.onCancel(SetTaskDialog.this);
            SetTaskDialog.this.dismiss();
        });
        getContentView().findViewById(R.id.button_ok).setOnClickListener(v -> {
            onFinishListener.onSet(SetTaskDialog.this);
            SetTaskDialog.this.dismiss();
        });

    }

    public void setDate(Date date){
        DateEditView et_date=getContentView().findViewById(R.id.edittext_deadline_date);
        TimeEditView et_time=getContentView().findViewById(R.id.edittext_deadline_time);
        et_date.setDate(date.getYear(),date.getMonth(),date.getDate());
        et_time.setTime(date.getHours(),date.getMinutes());
    }

    public Date getDate(){
        DateEditView et_date=getContentView().findViewById(R.id.edittext_deadline_date);
        TimeEditView et_time=getContentView().findViewById(R.id.edittext_deadline_time);
        return new Date(et_date.getDate()+et_time.getTime());
    }

    public String getTitle(){
        EditText et=getContentView().findViewById(R.id.edittext_title);
        return et.getText().toString();
    }


    public void setTitle(String title){
        EditText et=getContentView().findViewById(R.id.edittext_title);
        et.setText(title);
    }


    public interface OnFinishListener{
        void onSet(SetTaskDialog dialog);
        void onCancel(SetTaskDialog dialog);

    }

    public void popup(View parent){
        this.showAtLocation(parent, Gravity.CENTER,0,0);
    }

}
