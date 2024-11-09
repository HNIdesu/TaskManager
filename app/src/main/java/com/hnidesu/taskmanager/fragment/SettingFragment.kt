package com.hnidesu.taskmanager.fragment

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
import com.hnidesu.taskmanager.base.DatabaseTaskSource
import com.hnidesu.taskmanager.service.CheckDeadlineService
import com.hnidesu.taskmanager.util.LogUtil
import com.hnidesu.taskmanager.util.ToastUtil
import com.hnidesu.taskmanager.widget.dialog.ImportDataDialog
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class SettingFragment : PreferenceFragmentCompat() {
    private val mExportDataLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val context = requireContext()
        val data = result.data
        val data2 = data?.data
        if (result.resultCode == -1 && data2 != null) {
            val file = context.getDatabasePath("database.db")
            try {
                context.contentResolver.openOutputStream(data2).use { outputStream ->
                    FileInputStream(file).use { fileInputStream ->
                        val buffer = ByteArray(65536)
                        while (true) {
                            val bytesRead = fileInputStream.read(buffer)
                            if (bytesRead == -1)
                                break
                            outputStream!!.write(buffer, 0, bytesRead)
                        }
                    }
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
                val tempFile = File(context.cacheDir.absolutePath + "/temp_database.db")
                tempFile.deleteOnExit()
                try {
                    val inputStream= try {
                        context.contentResolver.openInputStream(data)
                    } catch (e: Exception) {
                        LogUtil.error(context.getString(R.string.import_data_failed), e)
                        ToastUtil.toastLong(context,R.string.import_data_failed)
                        return
                    } ?: throw Exception("读取文件失败")
                    inputStream.use {
                        FileOutputStream(tempFile).use { fileOutputStream->
                            val buffer = ByteArray(65536)
                            while (true) {
                                val bytesRead = it.read(buffer)
                                if (bytesRead == -1) {
                                    break
                                }
                                fileOutputStream.write(buffer, 0, bytesRead)
                            }
                        }
                    }
                    val taskList = DatabaseTaskSource(context).getTasks(null).toList()
                    ImportDataDialog(context, taskList).show()
                    tempFile.deleteOnExit()
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
                    "database_com.hnidesu.taskmanager.db"
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
