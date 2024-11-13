package com.hnidesu.taskmanager.collection

import com.hnidesu.taskmanager.base.SortType
import com.hnidesu.taskmanager.database.TaskEntity

class SortedTaskList {
    private var mItemList = mutableListOf<TaskEntity>()
    var sortType = SortType.CreationAsc
        set(value) {
            field = value
            sort(sortType)
        }

    private val mComparatorMap = mapOf<SortType,Comparator<TaskEntity>>(
        SortType.CreationAsc to Comparator { o1, o2 -> o1.createTime.compareTo(o2.createTime) },
        SortType.CreationDesc to Comparator { o1, o2 -> o2.createTime.compareTo(o1.createTime) },
        SortType.DeadlineAsc to Comparator { o1, o2 -> o1.deadline.compareTo(o2.deadline) },
        SortType.DeadlineDesc to Comparator { o1, o2 -> o2.deadline.compareTo(o1.deadline) },
        SortType.ModifiedAsc to Comparator { o1, o2 -> o1.lastModifiedTime.compareTo(o2.lastModifiedTime) },
        SortType.ModifiedDesc to Comparator { o1, o2 -> o2.lastModifiedTime.compareTo(o1.lastModifiedTime) }
    )

    fun update(index: Int): Int {
        val item = mItemList[index]
        removeAt(index)
        return add(item)
    }

    operator fun get(index: Int): TaskEntity {
        return mItemList[index]
    }

    fun clear() {
        mItemList.clear()
    }

    val size: Int
        get() = mItemList.size

    fun add(taskItem: TaskEntity): Int {
        val comparator = mComparatorMap[sortType]!!
        val size = mItemList.size
        var putIndex = -1
        for (i in 0 until size) {
            if (comparator.compare(taskItem, mItemList[i]) < 0) {
                putIndex = i
                break
            }
            if (i == size - 1) {
                putIndex = size
            }
        }
        if (putIndex == -1) {
            mItemList.add(taskItem)
        } else {
            mItemList.add(putIndex, taskItem)
        }
        return putIndex
    }

    fun removeAt(index: Int): Int {
        mItemList.removeAt(index)
        return index
    }

    private fun sort(sortType: SortType) {
        when (sortType) {
            SortType.CreationAsc ->
                mItemList.sortBy { it.createTime }
            SortType.CreationDesc ->
                mItemList.sortByDescending { it.createTime }
            SortType.DeadlineAsc ->
                mItemList.sortBy { it.deadline }
            SortType.DeadlineDesc ->
                mItemList.sortByDescending { it.deadline }
            SortType.ModifiedAsc ->
                mItemList.sortBy { it.lastModifiedTime }
            SortType.ModifiedDesc ->
                mItemList.sortByDescending { it.lastModifiedTime }
        }
    }

    fun replace(list: MutableList<TaskEntity>) {
        mItemList = list
        sort(sortType)
    }

}
