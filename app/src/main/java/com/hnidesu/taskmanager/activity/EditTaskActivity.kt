package com.hnidesu.taskmanager.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActionMenuView
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.database.TaskEntity
import com.hnidesu.taskmanager.manager.TaskManager
import com.hnidesu.taskmanager.util.HashUtil
import com.hnidesu.taskmanager.util.LogUtil
import com.hnidesu.taskmanager.util.ToastUtil
import com.hnidesu.taskmanager.widget.view.EditTextEx
import com.hnidesu.taskmanager.widget.view.EditTextEx.TextChangeListener

class EditTaskActivity : AppCompatActivity() {
    private var mOnMenuItemClickListener: ActionMenuView.OnMenuItemClickListener? = null
    private var mPreviousTextHash: Long = 0
    private var mViewHolder: ViewHolder? = null
    private var mEntity:TaskEntity?=null

    inner class ViewHolder {
        val editText: EditTextEx = findViewById(R.id.edittext)

        fun bindViews() {
            try {
                val entity=mEntity?:return
                val taskTitle = entity.title
                val supportActionBar = supportActionBar
                supportActionBar?.title = taskTitle
                mPreviousTextHash = HashUtil.crc32Digest(entity.content)
                editText.setTextChangeListener(object : TextChangeListener {
                    override fun onTextChange(view: View?, text: CharSequence?) {
                        val hash = HashUtil.crc32Digest(text.toString())
                        supportActionBar?.setTitle(if (hash != mPreviousTextHash) "$taskTitle*" else taskTitle)
                    }
                })
                editText.setText(entity.content)
                mOnMenuItemClickListener =
                    ActionMenuView.OnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.option_close -> {
                                onExit()
                                true
                            }

                            R.id.option_save -> {
                                val content = editText.getText().toString()
                                TaskManager.updateTask(entity.copy(
                                    content = content,
                                    lastModifiedTime = System.currentTimeMillis()
                                ))
                                mPreviousTextHash = HashUtil.crc32Digest(content)
                                supportActionBar?.setTitle(taskTitle)
                                true
                            }

                            else -> {
                                false
                            }
                        }
                    }
            } catch (e: Exception) {
                editText.setText(R.string.open_editor_failed)
                LogUtil.error(getString(R.string.open_editor_failed), e)
            }
        }
    }

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
        val entity = mEntity?:return
        val viewHolder = mViewHolder
        val temp: String? = viewHolder?.editText?.text?.toString()
        if (temp != null && HashUtil.crc32Digest(temp) != mPreviousTextHash) {
            AlertDialog.Builder(this).setCancelable(false)
                .setMessage(R.string.request_content_change).setPositiveButton(
                getString(R.string.save)
                ) { _, _ ->
                    try {
                        TaskManager.updateTask(entity.copy(
                            content = temp,
                            lastModifiedTime = System.currentTimeMillis()
                        ))
                        finish()
                    } catch (e: Exception) {
                        ToastUtil.toastLong(this,getString(R.string.save_failed))
                        LogUtil.error(getString(R.string.save_failed), e)
                    }
                }
                .setNegativeButton(R.string.do_not_save
                ) { _, _ -> finish() }.create().show()
        } else {
            finish()
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        val taskId=intent.getLongExtra("task_id",0)
        TaskManager.findTask(taskId)?.also {
            mEntity=it
        }
        mViewHolder=ViewHolder().also {
            it.bindViews()
        }
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onExit()
            }
        })
    }

}
