package com.hnidesu.taskmanager.base

import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import com.hnidesu.taskmanager.base.filter.FilterChain
import com.hnidesu.taskmanager.database.TaskEntity
import com.hnidesu.taskmanager.manager.DatabaseManager
import kotlinx.coroutines.runBlocking

class DatabaseTaskSource(context:Context) {
    private val mTaskDao = DatabaseManager.getMyDatabase(context).taskDao
    fun clear() {
        runBlocking {
            mTaskDao.clear()
        }
    }

    fun updateTask(newTaskItem: TaskEntity) {
        runBlocking {
            mTaskDao.updateTask(newTaskItem)
        }
    }

    fun findTask(createTime: Long): TaskEntity? {
        return runBlocking {
            mTaskDao.findTask(createTime)
        }
    }

    fun deleteTask(taskItem: TaskEntity) {
        runBlocking {
            mTaskDao.deleteTask(taskItem)
        }
    }

    fun importData(taskItems: Sequence<TaskEntity>) {
        runBlocking {
            for (item in taskItems) {
                mTaskDao.insertTask(item)
            }
        }
    }

    fun addTask(taskItem: TaskEntity) {
        runBlocking {
            mTaskDao.insertTask(taskItem)
        }
    }

    fun getTasks(filterChain: FilterChain<TaskEntity>?): Sequence<TaskEntity> {
        return runBlocking {
            mTaskDao.rawQuery(SimpleSQLiteQuery("SELECT * FROM tasks")).asSequence().filter { obj ->
                filterChain?.match(obj)?:true
            }
        }
    }


}
