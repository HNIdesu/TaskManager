package com.hnidesu.taskmanager.manager

import android.content.Context
import com.hnidesu.taskmanager.base.filter.FilterChain
import com.hnidesu.taskmanager.database.MyDatabase
import com.hnidesu.taskmanager.database.TaskEntity
import com.hnidesu.taskmanager.eventbus.TaskListChangeEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus

object TaskManager {
    private var isInitialized = false

    @Throws(IllegalStateException::class)
    fun assertInitialized() {
        if (!isInitialized)
            throw IllegalStateException("TaskManager is not initialized")
    }


    private lateinit var mDatabase: MyDatabase
    private lateinit var mInnerTaskCollection: MutableMap<Long, TaskEntity>

    fun init(context: Context) {
        mDatabase = DatabaseManager.getMyDatabase(context)
        mInnerTaskCollection = runBlocking {
            val map = mutableMapOf<Long, TaskEntity>()
            mDatabase.taskDao.allTasks().forEach {
                map[it.createTime] = it
            }
            map
        }
        isInitialized = true
    }

    @Throws(IllegalStateException::class)
    fun getTasks(filterChain: FilterChain<TaskEntity>? = null): List<TaskEntity> {
        assertInitialized()
        return mInnerTaskCollection.values.filter {
            filterChain?.match(it) ?: true
        }
    }

    @Throws(IllegalStateException::class)
    fun findTask(createTime: Long): TaskEntity? {
        assertInitialized()
        return mInnerTaskCollection.get(createTime)
    }

    @Throws(IllegalStateException::class)
    fun addTask(entity: TaskEntity) {
        assertInitialized()
        mInnerTaskCollection[entity.createTime] = entity
        CoroutineScope(Dispatchers.IO).launch {
            mDatabase.taskDao.insertTask(entity)
        }
        EventBus.getDefault().post(TaskListChangeEvent())
    }

    @Throws(IllegalStateException::class)
    fun updateTask(entity: TaskEntity) {
        assertInitialized()
        CoroutineScope(Dispatchers.IO).launch {
            mDatabase.taskDao.updateTask(entity)
        }
        mInnerTaskCollection.put(entity.createTime, entity)
        EventBus.getDefault().post(TaskListChangeEvent())
    }

    @Throws(IllegalStateException::class)
    fun addTasks(tasks: Iterable<TaskEntity>) {
        assertInitialized()
        CoroutineScope(Dispatchers.IO).launch {
            tasks.forEach {
                mDatabase.taskDao.insertTask(it)
            }
        }
        tasks.forEach {
            mInnerTaskCollection[it.createTime] = it
        }
        EventBus.getDefault().post(TaskListChangeEvent())
    }

    @Throws(IllegalStateException::class)
    fun deleteTask(entity: TaskEntity) {
        assertInitialized()
        CoroutineScope(Dispatchers.IO).launch {
            mDatabase.taskDao.deleteTask(entity)
        }
        mInnerTaskCollection.remove(entity.createTime)
        EventBus.getDefault().post(TaskListChangeEvent())
    }

    @Throws(IllegalStateException::class)
    fun clear() {
        assertInitialized()
        CoroutineScope(Dispatchers.IO).launch {
            mDatabase.taskDao.clear()
        }
        mInnerTaskCollection.clear()
        EventBus.getDefault().post(TaskListChangeEvent())
    }

}
