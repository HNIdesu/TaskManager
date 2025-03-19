package com.hnidesu.taskmanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_title_history")
data class TaskTitleEntity(
    @PrimaryKey
    val id: Long,
    @ColumnInfo
    val text: String,
    @ColumnInfo(name = "update_time")
    val updateTime: Long
)