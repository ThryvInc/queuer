package com.rndapp.task_feed.adapters

import com.rndapp.task_feed.listeners.OnTaskClickedListener
import com.rndapp.task_feed.models.Task
import com.rndapp.task_feed.views.SimpleViewHolder
import java.util.*

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/23/13
 * Time: 9:53 AM

 */
class TaskAdapter(var tasks: ArrayList<Task>, val listener: OnTaskClickedListener): SimpleItemAdapter<Task>(tasks as List<Task>) {

    var filteredTasks: List<Task> = ArrayList()
        get() = tasks.filter { !it.isFinished }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val task = filteredTasks[position]
        holder.setText(task.name, position)
    }

    override fun getItemCount(): Int {
        return filteredTasks.size
    }

    override fun onSimpleItemClicked(id: Int) {
        listener.onTaskClicked(tasks[id])
    }
}
