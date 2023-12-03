package com.hnidesu.taskmanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class CheckDeadlineService extends Service {
    public CheckDeadlineService() {
    }
    private Timer mTimer;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result= super.onStartCommand(intent, flags, startId);
        if(mTimer==null){
            mTimer=new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    MyApplication.getInstance().CheckTasks();
                }
            },0,30*1000);

        }

        return result;
    }
}