package com.hnidesu.taskmanager.base.filter

interface Filter<T> {
    fun match(t: T): Boolean
}
