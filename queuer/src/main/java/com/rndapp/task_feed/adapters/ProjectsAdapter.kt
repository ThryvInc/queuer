package com.rndapp.task_feed.adapters

import com.rndapp.task_feed.interfaces.RearrangementListener
import com.rndapp.task_feed.listeners.OnProjectClickedListener
import com.rndapp.task_feed.listeners.OnSimpleItemClickedListener
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.models.ProjectColor
import com.rndapp.task_feed.views.SimpleViewHolder
import java.util.*

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/23/13
 * Time: 9:53 AM

 */
class ProjectsAdapter(var projects: ArrayList<Project>, private val listener: OnProjectClickedListener?):
        SimpleItemAdapter<Project>(projects as List<Project>), RearrangementListener, OnSimpleItemClickedListener {

    override fun getItemCount(): Int {
        return projects.size
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val project = projects[position]
        val text = project.name
        val color = ProjectColor.idFromProjectColor(project.color)
        holder.setText(text, position)
        if (project.remainingPoints != null) {
            holder.setRightText(project.remainingPoints.toString())
        }

        holder.itemView.setBackgroundColor(holder.itemView.context.resources.getColor(color))
    }

    override fun onSimpleItemClicked(id: Int) {
        listener?.onProjectClicked(projects[id])
    }

    override fun swapElements(indexOne: Int, indexTwo: Int) {
        val temp1 = projects[indexOne]
        val temp2 = projects[indexTwo]

        projects.removeAt(indexOne)
        projects.add(indexOne, temp2)

        projects.removeAt(indexTwo)
        projects.add(indexTwo, temp1)
    }
}
