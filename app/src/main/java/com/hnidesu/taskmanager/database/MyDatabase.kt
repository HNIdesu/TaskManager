package com.hnidesu.taskmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 2)
abstract class MyDatabase : RoomDatabase() {
    abstract val taskDao: TaskDao
}
