package com.hnidesu.taskmanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("tasks")
data class TaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "create_time")
    val createTime: Long,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "is_finished")
    val isFinished: Int,
    @ColumnInfo(name = "deadline")
    val deadline: Long,
    @ColumnInfo(name = "last_modified_time")
    val lastModifiedTime: Long,
    @ColumnInfo(name = "is_encrypted")
    val isEncrypted: Int
)