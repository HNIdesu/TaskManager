package com.hnidesu.taskmanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity("tasks")
data class TaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "create_time")
    var createTime: Long,
    @ColumnInfo(name = "content")
    var content: String,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "is_finished")
    var isFinished: Int,
    @ColumnInfo(name = "deadline")
    var deadline: Long,
    @ColumnInfo(name = "last_modified_time")
    var lastModifiedTime: Long,
    @ColumnInfo(name = "is_encrypted")
    var isEncrypted: Int
) : Serializable