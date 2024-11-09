package com.hnidesu.taskmanager.util

import android.util.Log

object LogUtil{
    private const val TAG = "TaskManager"
    fun debug(msg: String) {
        Log.d(TAG, msg)
    }

    fun error(msg: String?, e: Throwable?) {
        Log.e(TAG, msg, e)
    }
}
