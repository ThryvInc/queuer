package com.rndapp.task_feed.listeners

import com.rndapp.task_feed.models.DayTask

interface OnDayTaskClickedListener {
    fun onDayTaskClicked(dayTask: DayTask)
}