package com.rndapp.task_feed.interfaces;

import android.content.Context;

import com.rndapp.task_feed.models.Task;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 7/16/13
 * Time: 3:29 PM
 */
public interface TaskDisplayer {
    public void setupForAsync();
    public void taskUpdated(Task task);
    public void taskCreated(Task task);
    public Context getContext();
    public void taskChangedOrder(Task task);
}
