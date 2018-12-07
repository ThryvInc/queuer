package com.rndapp.task_feed.adapters

import com.rndapp.task_feed.adapters.items.HighlightableSimpleViewModel
import com.rndapp.task_feed.adapters.items.SimpleViewModel
import com.rndapp.task_feed.listeners.OnTaskClickedListener
import com.rndapp.task_feed.models.Task
import java.util.*

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/23/13
 * Time: 9:53 AM
 */

class TaskAdapter(var tasks: ArrayList<Task>, listener: OnTaskClickedListener):
        SimpleListAdapter<Task>(tasks, ::taskToSimpleViewModel, listener::onTaskClicked)

fun taskToSimpleViewModel(task: Task): SimpleViewModel {
    return SimpleViewModel(task.name ?: "", task.points.toString())
}

class HighlightableTaskAdapter(var tasks: ArrayList<Task>, listener: (Task, Boolean) -> Unit):
        HighlightableListAdapter<Task>(tasks, ::taskToHighlightableSimpleViewModel, listener)

fun taskToHighlightableSimpleViewModel(task: Task): HighlightableSimpleViewModel {
    return HighlightableSimpleViewModel(task.name ?: "", task.points.toString())
}