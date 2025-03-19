package com.hnidesu.taskmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class,TaskTitleEntity::class], version = 3)
abstract class MyDatabase : RoomDatabase() {
    abstract val taskDao: TaskDao
    abstract val taskTitleDao: TaskTitleDao
}
