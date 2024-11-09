package com.hnidesu.taskmanager.base

import com.hnidesu.taskmanager.database.TaskEntity

class TaskCollection {
    private var mInnerList: MutableList<TaskEntity> = mutableListOf()
    var sortType = SortType.CreationAsc
        set(value) {
            field = value
            sort(sortType)
        }

    private val mComparatorMap = mapOf(
        0 to object : IComparator {
        override fun canBePutAfter(before: TaskEntity, after: TaskEntity): Boolean {
            return before.createTime < after.createTime
        }
    },1 to object : IComparator {
        override fun canBePutAfter(before: TaskEntity, after: TaskEntity): Boolean {
            return before.createTime > after.createTime
        }
    },2 to object : IComparator {
        override fun canBePutAfter(before: TaskEntity, after: TaskEntity): Boolean {
            return before.deadline < after.deadline
        }
    },3 to object : IComparator {
        override fun canBePutAfter(before: TaskEntity, after: TaskEntity): Boolean {
            return before.deadline > after.deadline
        }
    },4 to object : IComparator {
        override fun canBePutAfter(before: TaskEntity, after: TaskEntity): Boolean {
            return before.lastModifiedTime < after.lastModifiedTime
        }
    },5 to object : IComparator {
        override fun canBePutAfter(before: TaskEntity, after: TaskEntity): Boolean {
            return before.lastModifiedTime > after.lastModifiedTime
        }
    })

    interface IComparator {
        fun canBePutAfter(before: TaskEntity, after: TaskEntity): Boolean
    }

    fun update(index: Int): Int {
        val item = mInnerList[index]
        removeAt(index)
        return add(item)
    }

    operator fun get(index: Int): TaskEntity {
        return mInnerList[index]
    }

    fun clear() {
        mInnerList.clear()
    }

    val size: Int
        get() = mInnerList.size

    fun add(taskItem: TaskEntity): Int {
        val iComparator = mComparatorMap[sortType.ordinal]!!
        val size = mInnerList.size
        var putIndex = -1
        return if (size == 0) {
            mInnerList.add(taskItem)
            0
        } else if (!iComparator.canBePutAfter(mInnerList[0], taskItem)) {
            mInnerList.add(0, taskItem)
            0
        } else {
            for (i in 0 until size) {
                if (iComparator.canBePutAfter(
                        mInnerList[i],
                        taskItem
                    ) && (i == size - 1 || !iComparator.canBePutAfter(
                        mInnerList[i + 1], taskItem
                    ))
                ) {
                    putIndex = i + 1
                    mInnerList.add(putIndex, taskItem)
                }
            }
            putIndex
        }
    }

    fun removeAt(index: Int): Int {
        mInnerList.removeAt(index)
        return index
    }

    private fun sort(sortType: SortType) {
        when (sortType) {
            SortType.CreationAsc->
                mInnerList.sortBy { it.createTime }
            SortType.CreationDesc->
                mInnerList.sortByDescending { it.createTime }
            SortType.DeadlineAsc->
                mInnerList.sortBy { it.deadline }
            SortType.DeadlineDesc->
                mInnerList.sortByDescending { it.deadline }
            SortType.ModifiedAsc->
                mInnerList.sortBy { it.lastModifiedTime }
            SortType.ModifiedDesc->
                mInnerList.sortByDescending { it.lastModifiedTime }
        }
    }

    fun replace(list: MutableList<TaskEntity>) {
        mInnerList = list
        sort(sortType)
    }

}
