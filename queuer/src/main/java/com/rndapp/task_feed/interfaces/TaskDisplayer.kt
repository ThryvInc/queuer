package com.rndapp.task_feed.interfaces

import android.content.Context

import com.rndapp.task_feed.models.Task

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 7/16/13
 * Time: 3:29 PM
 */
interface TaskDisplayer {
    fun getContext(): Context?
    fun setupForAsync()
    fun taskUpdated(task: Task)
    fun taskCreated(task: Task)
    fun taskChangedOrder(task: Task)
}
