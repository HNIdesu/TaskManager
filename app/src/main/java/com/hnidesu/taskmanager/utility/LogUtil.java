package com.hnidesu.taskmanager.utility;

public class LogUtil {
    public static final String TAG="TaskManager: ";
    public static void Log(String msg){
        System.out.println(TAG+msg);
    }

    public static void Error(String msg,Exception e){
        System.out.println(TAG+msg+"\nError:"+e.getMessage());
        e.printStackTrace();
    }
}
