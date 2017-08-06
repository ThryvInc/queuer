package com.rndapp.task_feed.listeners

import com.rndapp.task_feed.models.Task

/**
 * Created by ell on 10/28/16.
 */

interface OnTaskClickedListener {
    fun onTaskClicked(task: Task)
}
