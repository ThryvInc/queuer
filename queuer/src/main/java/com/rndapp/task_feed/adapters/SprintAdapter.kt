package com.rndapp.task_feed.adapters

import com.rndapp.task_feed.adapters.items.SimpleViewModel
import com.rndapp.task_feed.listeners.OnSprintClickedListener
import com.rndapp.task_feed.models.Sprint

/**
 * Created by ell on 8/5/17.
 */
class SprintAdapter(sprints: List<Sprint>, listener: OnSprintClickedListener):
        SimpleListAdapter<Sprint>(sprints, ::sprintToSimpleViewModel, listener::onSprintClicked)

fun sprintToSimpleViewModel(sprint: Sprint): SimpleViewModel {
    return SimpleViewModel(sprint.nameFromStartDate() ?: "", sprint.points.toString())
}
