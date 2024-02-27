package com.hnidesu.taskmanager.utility;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hnidesu.taskmanager.MyApplication;
import com.hnidesu.taskmanager.R;

public class NotificationUtil {

    public static final String ChannelId="hnidesu_taskmanager";

    private static NotificationUtil instance;

    public static NotificationUtil getInstance(){
        if(instance==null)
            instance=new NotificationUtil();
        return instance;
    }

    public void sendUrgent(String msg,@Nullable Intent intent){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(MyApplication.getInstance(),ChannelId);
        builder.setSmallIcon(R.mipmap.ic_launcher).
                setContentTitle(MyApplication.getInstance().getString(R.string.notification_title_deadline)).
                setContentText(msg).
                setAutoCancel(true).
                setTicker(msg).
                setWhen(System.currentTimeMillis()).
                setPriority(NotificationCompat.PRIORITY_HIGH);
        if(intent!=null)
            builder.setContentIntent(PendingIntent.getActivity (MyApplication.getInstance().getApplicationContext(),5937,intent,PendingIntent.FLAG_UPDATE_CURRENT));
        Notification notification=builder.build();
        NotificationManagerCompat.from(MyApplication.getInstance()).notify((int)(System.currentTimeMillis()/1000),notification);
    }
}
