package com.rndapp.task_feed.adapters

import com.rndapp.task_feed.interfaces.RearrangementListener
import com.rndapp.task_feed.listeners.OnDayTaskClickedListener
import com.rndapp.task_feed.models.DayTask
import com.rndapp.task_feed.views.SimpleViewHolder

class DayTaskAdapter(var dayTasks: ArrayList<DayTask>, val listener: OnDayTaskClickedListener):
        SimpleItemAdapter<DayTask>(dayTasks as List<DayTask>), RearrangementListener {
    var shouldDisplayFinishedTasks = false

    var filteredDayTasks: List<DayTask> = ArrayList()
        get() = dayTasks.filter { !((it.task?.isFinished ?: true) && !shouldDisplayFinishedTasks) }

    override fun onBindViewHolder(holder: SimpleViewHolder?, position: Int) {
        val dayTask = filteredDayTasks[position]
        holder?.setText(dayTask.task?.name, position)
        if (dayTask.task?.project?.color != null) {
            holder?.itemView?.setBackgroundColor(dayTask.task.project?.color!!)
        }
    }

    override fun getItemCount(): Int {
        return filteredDayTasks.size
    }

    override fun onSimpleItemClicked(id: Int) {
        val dayTask = filteredDayTasks[id]
        listener.onDayTaskClicked(dayTask)
    }

    override fun swapElements(indexOne: Int, indexTwo: Int) {

        val temp1 = filteredDayTasks[indexOne]
        val temp2 = filteredDayTasks[indexTwo]

        val one = dayTasks.indexOf(temp1)
        val two = dayTasks.indexOf(temp2)

        dayTasks.removeAt(one)
        dayTasks.add(one, temp2)

        dayTasks.removeAt(two)
        dayTasks.add(two, temp1)
    }
}