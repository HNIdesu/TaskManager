package com.hnidesu.taskmanager.manager

import android.content.Context
import com.hnidesu.taskmanager.base.DatabaseTaskSource
import com.hnidesu.taskmanager.base.filter.FilterChain
import com.hnidesu.taskmanager.database.TaskEntity

object TaskManager{
    fun getTasks(context:Context,filterChain: FilterChain<TaskEntity>?): Sequence<TaskEntity> {
        return DatabaseTaskSource(context).getTasks(filterChain)
    }

    fun setTaskFinish(context:Context, entity: TaskEntity, finish: Boolean): Boolean {
        val fin=if(finish)1 else 0
        return if (entity.isFinished != fin) {
            entity.isFinished = fin
            DatabaseTaskSource(context).updateTask(entity)
            true
        }else false
    }

    fun findTask(context:Context,id:Long): TaskEntity? {
        return DatabaseTaskSource(context).findTask(id)
    }

    fun addTask(context:Context,item: TaskEntity) {
        DatabaseTaskSource(context).addTask(item)
    }

    fun updateTask(context:Context,item: TaskEntity) {
        DatabaseTaskSource(context).updateTask(item)
    }

    fun deleteTask(context:Context,task: TaskEntity) {
        DatabaseTaskSource(context).deleteTask(task)
    }

    fun clear(context:Context) {
        DatabaseTaskSource(context).clear()
    }

}
