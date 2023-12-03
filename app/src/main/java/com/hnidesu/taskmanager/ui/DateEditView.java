package com.hnidesu.taskmanager.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;

import java.util.Date;

public class DateEditView extends androidx.appcompat.widget.AppCompatEditText {

    boolean touchFlag=false;
    public DateEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                DatePickerDialog dialog;
                if(touchFlag)return false;
                touchFlag=true;
                if(year==-1){
                    Date curDate=new Date(System.currentTimeMillis());
                    setDate(curDate.getYear(),curDate.getMonth(),curDate.getDate());
                }
                dialog=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        DateEditView.this.setText(String.format("%04d-%02d-%02d",datePicker.getYear(),datePicker.getMonth()+1,datePicker.getDayOfMonth()));
                        setDate(datePicker.getYear()-1900,datePicker.getMonth(),datePicker.getDayOfMonth());
                    }
                },year+1900,month,date);
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

    private int year=-1;
    private int month;
    private int date;


    public long getDate(){
        Date date=new Date(0);
        date.setYear(this.year);
        date.setMonth(this.month);
        date.setDate(this.date);
        return date.getTime();
    }


    public void setDate(int year,int month,int date){
        this.year=year;
        this.month=month;
        this.date=date;
        this.setText(String.format("%04d-%02d-%02d",year+1900,month+1,date));
    }
}
