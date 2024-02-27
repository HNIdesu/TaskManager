package com.hnidesu.taskmanager.utility;

import android.util.Log;

import androidx.annotation.StringRes;

import com.hnidesu.taskmanager.MyApplication;

public class LogUtil {
    public static final String TAG="TaskManager";
    public static void Log(String msg){
        Log.d(TAG,msg);
    }

    public static void Log(@StringRes int resId){
        Log.d(TAG,MyApplication.getInstance().getString(resId));
    }

    public static void Error(String msg,Throwable e){
        Log.e(TAG,msg,e);
    }

    public static void Error(@StringRes int resId, Throwable e){
        Log.e(TAG,MyApplication.getInstance().getString(resId),e);
    }
}
