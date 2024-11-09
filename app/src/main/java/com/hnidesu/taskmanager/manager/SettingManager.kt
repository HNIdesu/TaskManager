package com.hnidesu.taskmanager.manager

import android.content.Context
import android.content.SharedPreferences


object SettingManager {
    fun getDefaultSetting(context: Context): SharedPreferences {
        return context.getSharedPreferences("setting", Context.MODE_PRIVATE)
    }

}
