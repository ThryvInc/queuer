package com.rndapp.task_feed.adapters.items

import android.support.v7.widget.RecyclerView
import android.view.View
import com.rndapp.task_feed.R
import com.rndapp.task_feed.models.SprintProject
import com.rndapp.task_feed.views.SprintProjectViewHolder
import com.thryvinc.thux.adapters.LayoutIdRecyclerItemViewModel

class SprintProjectItem(val model: SprintProject, val listener: (RecyclerView.ViewHolder) -> Unit):
        LayoutIdRecyclerItemViewModel(R.layout.item_sprint_project) {
    override fun configureHolder(holder: RecyclerView.ViewHolder) {
        if (holder is SprintProjectViewHolder) {
            holder.reset()
            holder.setText(model.project?.name)
            holder.setRightText((model.remainingPoints ?: 0).toString())
            val tasks = model.unfinishedTasks()
            if (tasks != null) {
                (0..6).forEach {
                    if (tasks.size > it) holder.setNthText(it, "${tasks[it].points} ${tasks[it].name}")
                }
            }
        }
    }

    override fun viewHolderWithView(view: View): RecyclerView.ViewHolder {
        return SprintProjectViewHolder(view, listener)
    }
}