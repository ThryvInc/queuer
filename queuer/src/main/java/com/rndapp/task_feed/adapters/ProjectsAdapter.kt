package com.rndapp.task_feed.adapters

import android.support.v7.widget.RecyclerView
import com.rndapp.task_feed.adapters.items.HighlightableSimpleViewModel
import com.rndapp.task_feed.adapters.items.SimpleViewModel
import com.rndapp.task_feed.listeners.OnProjectClickedListener
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.models.ProjectColor
import java.util.*

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/23/13
 * Time: 9:53 AM

 */
class ProjectsAdapter(var projects: ArrayList<Project>, listener: OnProjectClickedListener):
        SimpleListAdapter<Project>(projects, ::projectToSimpleViewModel, listener::onProjectClicked) { // }, RearrangementListener {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val project = projects[position]
        val color = ProjectColor.idFromProjectColor(project.color)
        holder.itemView.setBackgroundColor(holder.itemView.context.resources.getColor(color))
    }

//    override fun swapElements(indexOne: Int, indexTwo: Int) {
//        val temp1 = projects[indexOne]
//        val temp2 = projects[indexTwo]
//
//        projects.removeAt(indexOne)
//        projects.add(indexOne, temp2)
//
//        projects.removeAt(indexTwo)
//        projects.add(indexTwo, temp1)
//    }
}

fun projectToSimpleViewModel(project: Project): SimpleViewModel {
    return SimpleViewModel(project.name ?: "", (project.remainingPoints ?: 0).toString())
}

class HighlightableProjectAdapter(var projects: ArrayList<Project>, listener: (Project, Boolean) -> Unit):
        HighlightableListAdapter<Project>(projects, ::projectToHighlightableSimpleViewModel, listener)

fun projectToHighlightableSimpleViewModel(project: Project): HighlightableSimpleViewModel {
    return HighlightableSimpleViewModel(project.name ?: "", (project.remainingPoints ?: 0).toString())
}
