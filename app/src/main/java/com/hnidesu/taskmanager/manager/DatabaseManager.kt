package com.hnidesu.taskmanager.manager

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hnidesu.taskmanager.database.MyDatabase

object DatabaseManager{
    lateinit var myDatabase: MyDatabase
    fun init(context: Context){
        myDatabase = Room.databaseBuilder(context, MyDatabase::class.java, "database.db")
            .addMigrations(
            object : Migration(1,2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("CREATE TABLE tasks_temp (\n    create_time INTEGER PRIMARY KEY NOT NULL,\n    last_modified_time INTEGER NOT NULL,\n    is_encrypted INTEGER NOT NULL,\n    title TEXT NOT NULL,\n    deadline INTEGER NOT NULL,\n    content TEXT NOT NULL,\n    is_finished INTEGER NOT NULL\n)")
                    db.execSQL("INSERT INTO tasks_temp(last_modified_time, create_time, is_encrypted, title, deadline, content, is_finished) SELECT last_modified_time, create_time, is_encrypted, title, deadline, content, is_finished FROM tasks")
                    db.execSQL("DROP TABLE tasks")
                    db.execSQL("ALTER TABLE tasks_temp RENAME TO tasks")
                    db.execSQL("CREATE TABLE IF NOT EXISTS periodic_tasks (\n    last_finished_time INTEGER NOT NULL,\n    start_time INTEGER NOT NULL,\n    period INTEGER NOT NULL,\n    last_modified_time INTEGER NOT NULL,\n    create_time INTEGER PRIMARY KEY NOT NULL,\n    is_encrypted INTEGER NOT NULL,\n    end_time INTEGER NOT NULL,\n    title TEXT NOT NULL,\n    content TEXT NOT NULL\n)")
                }
            }).addMigrations(
            object : Migration(2,3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("DROP TABLE IF EXISTS periodic_tasks")
                    db.execSQL("CREATE TABLE IF NOT EXISTS task_title_history(id INTEGER PRIMARY KEY NOT NULL, text TEXT NOT NULL,update_time INTEGER NOT NULL)")
                }
            }).build()
    }
}
