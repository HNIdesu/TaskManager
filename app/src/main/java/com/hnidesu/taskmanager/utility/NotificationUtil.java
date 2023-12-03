package com.hnidesu.taskmanager.utility;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hnidesu.taskmanager.MyApplication;
import com.hnidesu.taskmanager.R;

public class NotificationUtil {

    public static final String ChannelId="hnidesu_taskmanager";
    private Context mContext;
    private static NotificationUtil instance;

    private NotificationUtil(Context ctx){
        mContext=ctx;

    }
    public static NotificationUtil getInstance(){
        if(instance==null)
            instance=new NotificationUtil(MyApplication.getInstance().getApplicationContext());
        return instance;
    }

    public void setContext(Context ctx) {
        this.mContext = ctx;
    }

    public void sendUrgent(String msg,@Nullable Intent intent){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(mContext,ChannelId);
        builder.setSmallIcon(R.mipmap.ic_launcher).
                setContentTitle(mContext.getString(R.string.notification_title_deadline)).
                setContentText(msg).
                setAutoCancel(true).
                setTicker(msg).
                setWhen(System.currentTimeMillis()).
                setPriority(NotificationCompat.PRIORITY_HIGH);
        if(intent!=null)
            builder.setContentIntent(PendingIntent.getActivity (MyApplication.getInstance().getApplicationContext(),5937,intent,PendingIntent.FLAG_UPDATE_CURRENT));
        Notification notification=builder.build();
        NotificationManagerCompat.from(mContext).notify((int)(System.currentTimeMillis()/1000),notification);
    }
}
