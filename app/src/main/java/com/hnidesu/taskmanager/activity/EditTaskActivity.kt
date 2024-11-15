package com.hnidesu.taskmanager.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActionMenuView
import androidx.core.widget.addTextChangedListener
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.database.TaskEntity
import com.hnidesu.taskmanager.databinding.ActivityEditTaskBinding
import com.hnidesu.taskmanager.manager.TaskManager
import com.hnidesu.taskmanager.util.HashUtil
import com.hnidesu.taskmanager.util.LogUtil
import com.hnidesu.taskmanager.util.ToastUtil

class EditTaskActivity : AppCompatActivity() {
    private var mOnMenuItemClickListener: ActionMenuView.OnMenuItemClickListener? = null
    private var mPreviousTextHash: Long = 0
    private var mEntity: TaskEntity? = null
    private var mActivityEditTaskBinding: ActivityEditTaskBinding? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_task_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val onMenuItemClickListener = mOnMenuItemClickListener
        onMenuItemClickListener?.onMenuItemClick(item)
        return super.onOptionsItemSelected(item)
    }

    fun onExit() {
        val entity = mEntity ?: return
        val temp: String? = mActivityEditTaskBinding?.edittext?.getText()?.toString()
        if (temp != null && HashUtil.crc32Digest(temp) != mPreviousTextHash) {
            AlertDialog.Builder(this).setCancelable(false)
                .setMessage(R.string.request_content_change).setPositiveButton(
                    getString(R.string.save)
                ) { _, _ ->
                    try {
                        TaskManager.updateTask(
                            entity.copy(
                                content = temp,
                                lastModifiedTime = System.currentTimeMillis()
                            )
                        )
                        finish()
                    } catch (e: Exception) {
                        ToastUtil.toastLong(this, getString(R.string.save_failed))
                        LogUtil.error(getString(R.string.save_failed), e)
                    }
                }
                .setNegativeButton(
                    R.string.do_not_save
                ) { _, _ -> finish() }.create().show()
        } else {
            finish()
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditTaskBinding.inflate(layoutInflater).also {
            mActivityEditTaskBinding = it
        }
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onExit()
            }
        })

        try {
            val taskId = intent.getLongExtra("task_id", 0)
            val entity = TaskManager.findTask(taskId)!!.also {
                mEntity = it
            }
            val taskTitle = entity.title
            supportActionBar?.title = taskTitle
            mPreviousTextHash = HashUtil.crc32Digest(entity.content)
            binding.edittext.apply {
                addTextChangedListener {
                    val hash = HashUtil.crc32Digest(text.toString())
                    supportActionBar?.title = if (hash != mPreviousTextHash) "$taskTitle*" else taskTitle
                }
                setText(entity.content)
            }
            mOnMenuItemClickListener = ActionMenuView.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.option_close -> {
                        onExit()
                        true
                    }

                    R.id.option_save -> {
                        val content = binding.edittext.getText().toString()
                        TaskManager.updateTask(
                            entity.copy(
                                content = content,
                                lastModifiedTime = System.currentTimeMillis()
                            )
                        )
                        mPreviousTextHash = HashUtil.crc32Digest(content)
                        supportActionBar?.title = taskTitle
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
        } catch (e: Exception) {
            binding.edittext.setText(R.string.open_editor_failed)
            LogUtil.error(getString(R.string.open_editor_failed), e)
        }

    }

}
