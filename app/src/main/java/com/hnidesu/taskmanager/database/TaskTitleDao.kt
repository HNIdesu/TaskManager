package com.hnidesu.taskmanager.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskTitleDao {
    @Query("SELECT * FROM task_title_history ORDER BY update_time DESC")
    fun getAll(): Flow<List<TaskTitleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entity: TaskTitleEntity)

    @Delete
    suspend fun delete(entity: TaskTitleEntity)

    @Query("DELETE FROM task_title_history")
    suspend fun deleteAll()
}