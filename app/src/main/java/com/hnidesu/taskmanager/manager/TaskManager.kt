package com.hnidesu.taskmanager.manager

import android.content.Context
import com.hnidesu.taskmanager.base.filter.FilterChain
import com.hnidesu.taskmanager.database.MyDatabase
import com.hnidesu.taskmanager.database.TaskEntity
import com.hnidesu.taskmanager.eventbus.TaskListChangeEvent
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

object TaskManager {
    @Throws(IllegalStateException::class)
    fun assertInitialized() {
        if (!mIsInitialized)
            throw IllegalStateException("TaskManager is not initialized")
    }

    private var mIsInitialized = false
    private val mSingleThreadContext = newSingleThreadContext("TaskManagerContext")
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
        mIsInitialized = true
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
        return mInnerTaskCollection[createTime]
    }

    @Throws(IllegalStateException::class)
    suspend fun addTask(entity: TaskEntity) {
        assertInitialized()
        mDatabase.taskDao.insertTask(entity)
        withContext(mSingleThreadContext) {
            mInnerTaskCollection[entity.createTime] = entity
        }
        EventBus.getDefault().post(TaskListChangeEvent())
    }

    @Throws(IllegalStateException::class)
    suspend fun updateTask(entity: TaskEntity) {
        assertInitialized()
        mDatabase.taskDao.updateTask(entity)
        withContext(mSingleThreadContext) {
            mInnerTaskCollection[entity.createTime] = entity
        }
        EventBus.getDefault().post(TaskListChangeEvent())
    }

    @Throws(IllegalStateException::class)
    suspend fun addTasks(tasks: Iterable<TaskEntity>) {
        assertInitialized()
        tasks.forEach {
            mDatabase.taskDao.insertTask(it)
        }
        withContext(mSingleThreadContext) {
            tasks.forEach {
                mInnerTaskCollection[it.createTime] = it
            }
        }
        EventBus.getDefault().post(TaskListChangeEvent())
    }

    @Throws(IllegalStateException::class)
    suspend fun deleteTask(entity: TaskEntity) {
        assertInitialized()
        mDatabase.taskDao.deleteTask(entity)
        withContext(mSingleThreadContext) {
            mInnerTaskCollection.remove(entity.createTime)
        }
        EventBus.getDefault().post(TaskListChangeEvent())
    }

    @Throws(IllegalStateException::class)
    suspend fun clear() {
        assertInitialized()
        mDatabase.taskDao.clear()
        withContext(mSingleThreadContext) {
            mInnerTaskCollection.clear()
        }
        EventBus.getDefault().post(TaskListChangeEvent())
    }

}
