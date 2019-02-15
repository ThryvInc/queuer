package com.rndapp.task_feed.adapters

import android.support.v7.widget.RecyclerView
import com.rndapp.task_feed.R
import com.rndapp.task_feed.adapters.items.HighlightableSimpleViewModel
import com.rndapp.task_feed.adapters.items.SimpleViewModel
import com.rndapp.task_feed.adapters.items.SprintProjectItem
import com.rndapp.task_feed.listeners.OnProjectClickedListener
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.models.ProjectColor
import com.rndapp.task_feed.models.SprintProject
import com.thryvinc.thux.adapters.ModelRecyclerViewAdapter
import com.thryvinc.thux.adapters.RecyclerItemViewModelInterface
import com.thryvinc.thux.into
import com.thryvinc.thux.intoSecond
import java.util.ArrayList

class SprintProjectsAdapter(var sprintProjects: ArrayList<SprintProject>, val listener: (SprintProject) -> Unit):
        ModelRecyclerViewAdapter(projectsToItems(sprintProjects, listener)) { // }, RearrangementListener {
    companion object {
        fun projectsToItems(array: List<SprintProject>, listener: (SprintProject) -> Unit): List<RecyclerItemViewModelInterface> {
            return array.map {
                SprintProjectItem(it) { listener.invoke(array[it.adapterPosition]) }
            }
        }
    }

    fun updateArray(array: List<SprintProject>) {
        super.itemViewModels = projectsToItems(array, listener)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val sprintProject = sprintProjects[position]
        val color = ProjectColor.idFromProjectColor(sprintProject.project?.color ?: ProjectColor.GREEN.rgb)
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

fun projectToSimpleViewModel(sprintProject: SprintProject): SimpleViewModel {
    return SimpleViewModel(sprintProject.project?.name ?: "", (sprintProject.remainingPoints ?: 0).toString())
}

class HighlightableSprintProjectAdapter(var sprintProjects: ArrayList<SprintProject>, listener: (SprintProject, Boolean) -> Unit):
        HighlightableListAdapter<SprintProject>(sprintProjects, ::projectToHighlightableSimpleViewModel, listener)

fun projectToHighlightableSimpleViewModel(sprintProject: SprintProject): HighlightableSimpleViewModel {
    return HighlightableSimpleViewModel(sprintProject.project?.name ?: "", (sprintProject.remainingPoints ?: 0).toString())
}
