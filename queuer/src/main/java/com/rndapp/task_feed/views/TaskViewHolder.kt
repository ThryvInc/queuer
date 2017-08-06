package com.rndapp.task_feed.views

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.rndapp.task_feed.R
import com.rndapp.task_feed.listeners.OnTaskClickedListener
import com.rndapp.task_feed.models.Task

/**
 * Created by ell on 10/28/16.
 */

class TaskViewHolder(itemView: View, private val listener: OnTaskClickedListener) : RecyclerView.ViewHolder(itemView) {
    private val tv: TextView
    private var task: Task? = null

    init {
        tv = itemView.findViewById(R.id.tv_task) as TextView

        itemView.setOnClickListener { this@TaskViewHolder.listener.onTaskClicked(task!!) }
    }

    fun setTask(task: Task) {
        this.task = task
        tv.text = task.name
    }
}
