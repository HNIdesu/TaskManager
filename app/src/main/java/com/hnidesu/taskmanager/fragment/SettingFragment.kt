package com.hnidesu.taskmanager.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.base.DatabaseTaskSource
import com.hnidesu.taskmanager.manager.SettingManager.getDefaultSetting
import com.hnidesu.taskmanager.service.CheckDeadlineService
import com.hnidesu.taskmanager.util.LogUtil
import com.hnidesu.taskmanager.util.ToastUtil
import com.hnidesu.taskmanager.widget.dialog.ImportDataDialog
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class SettingFragment : Fragment() {
    private val mExportDataLauncher: ActivityResultLauncher<Intent>
    private val mImportDataLauncher: ActivityResultLauncher<Intent>

    init {
        mExportDataLauncher= registerForActivityResult(
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

        mImportDataLauncher = registerForActivityResult(
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

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val context=requireContext()
        val rootView = inflater.inflate(R.layout.fragment_setting, container, false)
        rootView.findViewById<Button>(R.id.btn_export_data).setOnClickListener { view ->
            val intent = Intent()
            intent.setAction("android.intent.action.CREATE_DOCUMENT")
            intent.setType("*/*")
            intent.addCategory("android.intent.category.OPENABLE")
            intent.putExtra(
                "android.intent.extra.TITLE",
                "database_com.hnidesu.taskmanager.db"
            )
            mExportDataLauncher.launch(intent)
        }
        rootView.findViewById<Button>(R.id.btn_import_data).setOnClickListener { view ->
            val intent = Intent()
            intent.setAction("android.intent.action.PICK")
            mImportDataLauncher.launch(intent)
        }

        val view = rootView.findViewById<SwitchCompat>(R.id.enable_task_notification)
        val sharedPrefs = getDefaultSetting(context)
        view.setChecked(sharedPrefs.getBoolean("deadline_notification", false))
        view.setOnCheckedChangeListener { _, checked ->
            sharedPrefs.edit().putBoolean("deadline_notification", checked).apply()
            val intent =
                Intent(context, CheckDeadlineService::class.java)
            if (checked) {
                context.startService(intent)
            } else {
                context.stopService(intent)
            }
        }
        return rootView
    }

}
