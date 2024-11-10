package com.hnidesu.taskmanager

import android.app.Application
import android.content.Intent
import com.hnidesu.taskmanager.manager.SettingManager
import com.hnidesu.taskmanager.service.CheckDeadlineService
import com.jakewharton.threetenabp.AndroidThreeTen

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        if (SettingManager.getDefaultSetting(this).getBoolean("deadline_notification", false))
            startService(Intent(this, CheckDeadlineService::class.java))
    }
}
