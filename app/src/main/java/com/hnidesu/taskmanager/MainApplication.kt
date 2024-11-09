package com.hnidesu.taskmanager

import android.app.Application
import android.content.Intent
import com.hnidesu.taskmanager.manager.SettingManager
import com.hnidesu.taskmanager.service.CheckDeadlineService

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (SettingManager.getDefaultSetting(this).getBoolean("deadline_notification", false))
            startService(Intent(this, CheckDeadlineService::class.java))
    }
}
