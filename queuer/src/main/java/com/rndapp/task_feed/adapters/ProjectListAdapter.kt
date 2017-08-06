package com.rndapp.task_feed.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rndapp.task_feed.R
import com.rndapp.task_feed.interfaces.RearrangementListener
import com.rndapp.task_feed.listeners.OnProjectClickedListener
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.views.ProjectViewHolder

import java.util.ArrayList

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/23/13
 * Time: 9:53 AM

 */
class ProjectListAdapter(projects: ArrayList<Project>?, private val listener: OnProjectClickedListener) : RecyclerView.Adapter<ProjectViewHolder>(), RearrangementListener {
    private var projects: ArrayList<Project>? = null

    init {
        if (projects != null){
            this.projects = projects.clone() as ArrayList<Project>
        }

        removeEmptyProjects()
    }

    fun setProjects(projects: ArrayList<Project>) {
        this.projects = projects.clone() as ArrayList<Project>
        removeEmptyProjects()
        notifyDataSetChanged()
    }

    fun removeEmptyProjects() {
        for (project in projects!!) {
            if (project.isEmpty || project.isHidden) {
                projects!!.remove(project)
                removeEmptyProjects()
                break
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.project_list_item, parent, false)
        return ProjectViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.setProject(projects!![position])
    }

    override fun getItemCount(): Int {
        return projects!!.size
    }

    override fun swapElements(indexOne: Int, indexTwo: Int) {
        val temp1 = projects!![indexOne]
        val temp2 = projects!![indexTwo]

        projects!!.removeAt(indexOne)
        projects!!.add(indexOne, temp2)

        projects!!.removeAt(indexTwo)
        projects!!.add(indexTwo, temp1)
    }
}
