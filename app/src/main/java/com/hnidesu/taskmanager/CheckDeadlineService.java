package com.hnidesu.taskmanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hnidesu.taskmanager.activity.MainActivity;
import com.hnidesu.taskmanager.utility.DBUtil;
import com.hnidesu.taskmanager.utility.NotificationUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CheckDeadlineService extends Service {

    private final Timer mTimer=new Timer();
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                CheckTasks();
            }
        },0,30*1000);
    }

    @Override
    public void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }
}