package com.hnidesu.taskmanager.component;

import java.util.List;

public abstract class Observable {
    public abstract List<Observer> getObserverList();

    public void notifyObservers(){
        for (Observer o: getObserverList()) {
            o.update(this);
        }
        return;
    }
}
