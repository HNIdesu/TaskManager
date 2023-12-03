package com.hnidesu.taskmanager.ui;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;

public class TimeEditView extends androidx.appcompat.widget.AppCompatEditText{
    boolean touchFlag=false;
    public TimeEditView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(touchFlag)return false;
                touchFlag=true;
                if(hour==-1){
                    Date date=new Date(System.currentTimeMillis());
                    setTime(date.getHours(),date.getMinutes());
                }
                TimePickerDialog dialog=new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        TimeEditView.this.setText(String.format("%02d:%02d",hourOfDay,minute));
                        setTime(view.getHour(),view.getMinute());

                    }
                },hour,minute,true);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        touchFlag=false;
                    }
                });
                dialog.show();
                return true;
            }
        });
    }
    private int hour=-1;
    private int minute;



    public long getTime(){
        Date date=new Date(0);
        date.setHours(hour);
        date.setMinutes(minute);
        return date.getTime();
    }

    public void setTime(int hour,int minute){
        this.hour=hour;
        this.minute=minute;
        this.setText(String.format("%02d:%02d",hour,minute));
    }
}
