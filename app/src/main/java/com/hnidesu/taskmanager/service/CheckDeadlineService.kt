package com.hnidesu.taskmanager.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.activity.MainActivity
import com.hnidesu.taskmanager.manager.DatabaseManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Timer
import java.util.TimerTask

class CheckDeadlineService : Service() {
    private val mTimer = Timer()
    private val mCheckDeadlineTask = CheckDeadlineTask()
    private val mForegroundChannelId = "CheckDeadlineForegroundService"
    private val mChannelId = "taskmanager"
    private val mForegroundNotificationId = 100
    private val mLastCheckedTasks: Set<Long?> = setOf()

    fun sendUrgent(id: Int, msg: String?, intent: Intent?) {
        val builder =
            NotificationCompat.Builder(this, mChannelId).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(
                    getString(R.string.notification_title_deadline)
                ).setContentText(msg).setAutoCancel(true).setWhen(
                System.currentTimeMillis()
            ).setPriority(1)
        if (intent != null)
            builder.setContentIntent(PendingIntent.getActivity(this, 5937, intent, PendingIntent.FLAG_UPDATE_CURRENT))
        val notification = builder.build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(id, notification)
        }

    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private inner class CheckDeadlineTask : TimerTask() {
        override fun run() {
            val now = System.currentTimeMillis()
            val sql = SupportSQLiteQueryBuilder.builder("tasks").selection("deadline > ? and deadline < ?", arrayOf(now,now+10*60*1000)).create()//十分钟以内进行提醒
            val tasks= runBlocking {
                DatabaseManager.myDatabase.taskDao.getTasks(sql).first()
            }
            val checkDeadlineService = this@CheckDeadlineService
            for (taskItem in tasks) {
                if (!checkDeadlineService.mLastCheckedTasks.contains(taskItem.createTime)) {
                    val message = String.format(
                        checkDeadlineService.getString(R.string.task_will_end_in_minutes),
                        taskItem.title,
                            Math.round((taskItem.deadline - System.currentTimeMillis()) / 60000.0)
                    )
                    checkDeadlineService.sendUrgent(
                        (taskItem.createTime / 1000).toInt(),
                        message,
                        Intent(checkDeadlineService, MainActivity::class.java)
                    )
                }
            }
        }
    }

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= 26) {
            getSystemService(NotificationManager::class.java).createNotificationChannels(
                listOf(
                    NotificationChannel(
                        mForegroundChannelId,
                        getString(R.string.foreground_service_of_check_deadline_service),
                        NotificationManager.IMPORTANCE_DEFAULT
                    ), NotificationChannel(
                        mChannelId, getString(R.string.upcoming_task_notification), NotificationManager.IMPORTANCE_HIGH
                    )
                )
            )
        }
        startForeground(
            mForegroundNotificationId,
            NotificationCompat.Builder(this, mForegroundChannelId).build()
        )
        mTimer.schedule(mCheckDeadlineTask, 0L, 30000L)
    }

    override fun onDestroy() {
        mTimer.cancel()
        if (Build.VERSION.SDK_INT < 33) {
            @Suppress("DEPRECATION")
            stopForeground(true)
        } else {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        super.onDestroy()
    }
}
