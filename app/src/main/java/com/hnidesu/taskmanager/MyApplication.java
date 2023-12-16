package com.hnidesu.taskmanager;

import android.app.Application;
import android.content.Intent;

import com.hnidesu.taskmanager.activity.MainActivity;
import com.hnidesu.taskmanager.component.Observer;
import com.hnidesu.taskmanager.fragment.AllTaskFragment;
import com.hnidesu.taskmanager.fragment.UnfinishedTaskFragment;
import com.hnidesu.taskmanager.utility.DBUtil;
import com.hnidesu.taskmanager.utility.NotificationUtil;

import java.util.ArrayList;
import java.util.List;


public class MyApplication extends Application {

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    void CheckTasks(){
        List<String> tasks= DBUtil.getInstance().getEndingTasks(1000*5*60);
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        if(tasks.size()>0){
            NotificationUtil.getInstance().sendUrgent(String.format(getString(R.string.notification_count_of_ending_task),tasks.size()),intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        ArrayList<Observer> list=new ArrayList<>();
        list.add(AllTaskFragment.getInstance());
        list.add(UnfinishedTaskFragment.getInstance());
        DBUtil.getInstance().setObserverList(list);
        Intent intent=new Intent(getApplicationContext(),CheckDeadlineService.class);
        startService(intent);
    }
}
