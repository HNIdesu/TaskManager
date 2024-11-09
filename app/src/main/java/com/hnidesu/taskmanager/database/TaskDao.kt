package com.hnidesu.taskmanager.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    suspend fun allTasks(): List<TaskEntity>
    @Query("DELETE FROM tasks")
    suspend fun clear()
    @Delete
    suspend fun deleteTask(taskEntity: TaskEntity)
    @Query("SELECT * FROM tasks WHERE create_time = :createTime LIMIT 1")
    suspend fun findTask(createTime: Long): TaskEntity?
    @Insert
    suspend fun insertTask(taskEntity: TaskEntity)
    @RawQuery
    suspend fun rawQuery(supportSQLiteQuery: SupportSQLiteQuery): List<TaskEntity>
    @Update
    suspend fun updateTask(taskEntity: TaskEntity)
}
