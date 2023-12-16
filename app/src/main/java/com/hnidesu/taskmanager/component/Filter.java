package com.hnidesu.taskmanager.component;

public interface Filter<T> {
    public boolean match(T item);
}
