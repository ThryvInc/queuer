package com.rndapp.task_feed.adapters

import android.graphics.Color
import android.util.Log
import com.rndapp.task_feed.R
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
        val color = when (project.color) {
            ProjectColor.BLUE.rgb -> R.color.blue
            ProjectColor.GOLDENROD.rgb -> R.color.goldenrod
            ProjectColor.GREEN.rgb -> R.color.green
            ProjectColor.RED.rgb -> R.color.red
            ProjectColor.PLUM.rgb -> R.color.plum
            ProjectColor.TURQUOISE.rgb -> R.color.turquoise
            ProjectColor.SMOG.rgb -> {
                R.color.smog
            }
            ProjectColor.ORANGE.rgb -> {
                R.color.orange
            }
            ProjectColor.YELLOW.rgb -> R.color.yellow
            else -> {
                R.color.smog
            }
        }
        holder.setText(text, position)

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
