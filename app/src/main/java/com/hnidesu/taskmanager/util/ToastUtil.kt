package com.hnidesu.taskmanager.util

import android.content.Context
import android.widget.Toast

object ToastUtil {
    fun toastShort(context: Context,text: String?) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun toastLong(context: Context,text: String?) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun toastShort(context: Context,resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
    }

    fun toastLong(context: Context,resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
    }

}
