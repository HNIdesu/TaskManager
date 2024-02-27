package com.hnidesu.taskmanager.component;

public interface Filter<T> {
    boolean match(T item);
}
