package com.hnidesu.taskmanager.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun allTasks(): Flow<List<TaskEntity>>
    @Query("DELETE FROM tasks")
    suspend fun clear()
    @Delete
    suspend fun deleteTask(taskEntity: TaskEntity)
    @Query("SELECT * FROM tasks WHERE create_time = :createTime LIMIT 1")
    suspend fun findTask(createTime: Long): TaskEntity?
    @Insert
    suspend fun insertTask(taskEntity: TaskEntity)
    @Insert
    suspend fun insertTasks(taskEntities: List<TaskEntity>)
    @RawQuery(observedEntities = [TaskEntity::class])
    fun getTasks(supportSQLiteQuery: SupportSQLiteQuery):
            Flow<List<TaskEntity>>
    @Update
    suspend fun updateTask(taskEntity: TaskEntity)
}
