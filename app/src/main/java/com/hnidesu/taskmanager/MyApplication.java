package com.hnidesu.taskmanager;

import android.app.Application;
import android.content.Intent;

public class MyApplication extends Application {

    private static MyApplication instance;
    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        final Intent intent=new Intent(getApplicationContext(),CheckDeadlineService.class);
        startService(intent);
    }
}
