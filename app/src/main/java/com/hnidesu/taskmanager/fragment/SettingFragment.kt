package com.hnidesu.taskmanager.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.database.TaskEntity
import com.hnidesu.taskmanager.manager.TaskManager
import com.hnidesu.taskmanager.service.CheckDeadlineService
import com.hnidesu.taskmanager.util.LogUtil
import com.hnidesu.taskmanager.util.ToastUtil
import com.hnidesu.taskmanager.widget.dialog.ImportDataDialog
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class SettingFragment : PreferenceFragmentCompat() {
    private val mExportDataLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val context = requireContext()
        val data = result.data?.data
        if (result.resultCode != Activity.RESULT_CANCELED && data != null) {
            try {
                context.contentResolver.openOutputStream(data)?.use { outputStream ->
                    val json=JSONObject()
                    json.put("version",1)
                    val taskList= TaskManager.getTasks()
                    val taskListJsonArray=JSONArray()
                    json.put("tasks",taskListJsonArray)
                    for (task in taskList) {
                        taskListJsonArray.put(JSONObject().apply {
                            put("create_time", task.createTime)
                            put("title", task.title)
                            put("content", task.content)
                            put("is_finished", task.isFinished)
                            put("deadline", task.deadline)
                            put("is_encrypted", task.isEncrypted)
                            put("last_modified_time", task.lastModifiedTime)
                        })
                    }
                    outputStream.write(json.toString().toByteArray())
                }
            } catch (e: Exception) {
                LogUtil.error(getString(R.string.save_data_failed), e)
                ToastUtil.toastLong(context,R.string.save_data_failed)
            }
        } else {
            ToastUtil.toastShort(context,R.string.cancelled)
        }
    }
    private val mImportDataLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        object : ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult) {
                val context = requireContext()
                val data=result.data?.data
                if (result.resultCode != -1 || data == null) {
                    ToastUtil.toastShort(context,R.string.cancelled)
                    return
                }
                try {
                    val json=JSONObject(context.contentResolver.openInputStream(data)?.use {inputStream->
                        InputStreamReader(inputStream).use { reader->
                            BufferedReader(reader).use {
                                it.readText()
                            }
                        }
                    }!!)
                    val taskListJsonArray=json.getJSONArray("tasks")
                    val taskList= mutableListOf<TaskEntity>()
                    for (i in 0 until taskListJsonArray.length()) {
                        val taskJson=taskListJsonArray.getJSONObject(i)
                        taskList.add(TaskEntity(
                            taskJson.getLong("create_time"),
                            taskJson.getString("content"),
                            taskJson.getString("title"),
                            taskJson.getInt("is_finished"),
                            taskJson.getLong("deadline"),
                            taskJson.getLong("last_modified_time"),
                            taskJson.getInt("is_encrypted")
                        ))
                    }
                    ImportDataDialog(context, taskList).show()
                } catch (ex:Exception) {
                    LogUtil.error(getString(R.string.import_failed),ex)
                    ToastUtil.toastLong(context,R.string.import_failed)
                }
            }
        })


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName="setting"
        setPreferencesFromResource(R.xml.preferences,rootKey)
        preferenceManager.findPreference<Preference>("import_data")?.setOnPreferenceClickListener {
            val intent = Intent().apply {
                setAction("android.intent.action.PICK")
            }
            mImportDataLauncher.launch(intent)
            true
        }
        preferenceManager.findPreference<Preference>("export_data")?.setOnPreferenceClickListener {
            val intent = Intent().apply {
                setAction("android.intent.action.CREATE_DOCUMENT")
                setType("*/*")
                addCategory("android.intent.category.OPENABLE")
                putExtra(
                    "android.intent.extra.TITLE",
                    "tasklist.json"
                )
            }
            mExportDataLauncher.launch(intent)
            true
        }
        preferenceManager.findPreference<SwitchPreference>("deadline_notification")?.setOnPreferenceChangeListener{_,newValue->
            val intent = Intent(context, CheckDeadlineService::class.java)
            if (newValue==true)
                context?.startService(intent)
            else
                context?.stopService(intent)
            true
        }
    }
}
