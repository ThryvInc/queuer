package com.rndapp.task_feed.views

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import com.rndapp.task_feed.R
import com.rndapp.task_feed.listeners.OnProjectClickedListener
import com.rndapp.task_feed.models.Project

/**
 * Created by ell on 10/28/16.
 */

class ProjectViewHolder(itemView: View, private val listener: OnProjectClickedListener) : RecyclerView.ViewHolder(itemView) {
    private val tv: TextView
    private var project: Project? = null

    init {
        tv = itemView.findViewById(R.id.tv_project) as TextView

        itemView.setOnClickListener { this@ProjectViewHolder.listener.onProjectClicked(project!!) }
    }

    fun setProject(project: Project) {
        this.project = project
        tv.text = project.name + ": " + project.firstTaskText
        tv.setBackgroundColor(project.color)
    }
}
