package com.rndapp.task_feed.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.rndapp.task_feed.R
import com.rndapp.task_feed.listeners.OnSprintClickedListener
import com.rndapp.task_feed.models.Sprint
import com.rndapp.task_feed.views.SimpleViewHolder
import com.rndapp.task_feed.views.SprintViewHolder
import java.util.*

/**
 * Created by ell on 8/5/17.
 */
class SprintAdapter(private var sprints: List<Sprint>, private val listener: OnSprintClickedListener):
        SimpleItemAdapter<Sprint>(sprints) {
    override fun onBindViewHolder(holder: SimpleViewHolder?, position: Int) {
        val sprint = sprints[position]
        holder?.setText(sprint.name ?: sprint.nameFromStartDate(), position)
    }

    override fun onSimpleItemClicked(id: Int) {
        listener.onSprintClicked(sprints[id])
    }
}