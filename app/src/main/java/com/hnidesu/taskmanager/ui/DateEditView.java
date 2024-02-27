package com.hnidesu.taskmanager.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;

import java.util.Date;

public class DateEditView extends androidx.appcompat.widget.AppCompatEditText {
    boolean touchFlag=false;
    public DateEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener((v, event) -> {
            if(touchFlag)return false;
            touchFlag=true;
            if(year==-1){
                Date curDate=new Date(System.currentTimeMillis());
                setDate(curDate.getYear(),curDate.getMonth(),curDate.getDate());
            }
            final DatePickerDialog dialog=new DatePickerDialog(getContext(), (datePicker, year, month, dayOfMonth) -> {
                DateEditView.this.setText(String.format("%04d-%02d-%02d",datePicker.getYear(),datePicker.getMonth()+1,datePicker.getDayOfMonth()));
                setDate(datePicker.getYear()-1900,datePicker.getMonth(),datePicker.getDayOfMonth());
            },year+1900,month,date);
            dialog.setOnDismissListener(dialog1 -> touchFlag=false);
            dialog.show();
            return true;
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
