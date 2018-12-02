package com.rndapp.task_feed.adapters

import com.rndapp.task_feed.listeners.OnSprintClickedListener
import com.rndapp.task_feed.models.Sprint
import com.rndapp.task_feed.views.SimpleViewHolder

/**
 * Created by ell on 8/5/17.
 */
class SprintAdapter(private var sprints: List<Sprint>, private val listener: OnSprintClickedListener):
        SimpleItemAdapter<Sprint>(sprints) {
    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val sprint = sprints[position]
        holder.setText(sprint.name ?: sprint.nameFromStartDate(), position)
    }

    override fun onSimpleItemClicked(id: Int) {
        listener.onSprintClicked(sprints[id])
    }
}