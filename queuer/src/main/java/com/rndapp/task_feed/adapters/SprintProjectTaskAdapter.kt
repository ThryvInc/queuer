package com.rndapp.task_feed.adapters

import com.rndapp.task_feed.adapters.items.SimpleViewModel
import com.rndapp.task_feed.models.SprintProjectTask

class SprintProjectTaskAdapter(var sprintProjectTasks: ArrayList<SprintProjectTask>, listener: (SprintProjectTask) -> Unit):
        SimpleListAdapter<SprintProjectTask>(sprintProjectTasks, ::taskToSimpleViewModel, listener)

fun taskToSimpleViewModel(sprintProjectTask: SprintProjectTask): SimpleViewModel {
    return SimpleViewModel(sprintProjectTask.task?.name ?: "", sprintProjectTask.task?.points.toString())
}