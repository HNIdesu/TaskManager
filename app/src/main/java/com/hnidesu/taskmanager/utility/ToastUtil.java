package com.hnidesu.taskmanager.utility;

import android.widget.Toast;

import androidx.annotation.StringRes;

import com.hnidesu.taskmanager.MyApplication;

public class ToastUtil {


    public static void ToastShort(String text){
        Toast.makeText(MyApplication.getInstance().getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }
    public static void ToastLong(String text){
        Toast.makeText(MyApplication.getInstance().getApplicationContext(),text,Toast.LENGTH_SHORT).show();
    }

    public static void ToastShort(@StringRes int resId){
        Toast.makeText(MyApplication.getInstance().getApplicationContext(),resId,Toast.LENGTH_SHORT).show();
    }
    public static void ToastLong(@StringRes int resId){
        Toast.makeText(MyApplication.getInstance().getApplicationContext(),resId,Toast.LENGTH_LONG).show();
    }
}
