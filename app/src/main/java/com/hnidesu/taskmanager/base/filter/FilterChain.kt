package com.hnidesu.taskmanager.base.filter

import java.util.LinkedList

class FilterChain<T>(vararg filters: Filter<T>) {
    private val mFilterChain: LinkedList<Filter<T>> = LinkedList()

    init {
        mFilterChain.addAll(filters)
    }

    fun match(t: T): Boolean {
        return mFilterChain.all {filter->
            filter.match(t)
        }

        return true
    }

    fun add(filter: Filter<T>) {
        mFilterChain.addLast(filter)
    }
}
