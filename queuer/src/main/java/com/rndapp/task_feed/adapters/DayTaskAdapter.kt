package com.rndapp.task_feed.adapters

import android.support.v7.widget.RecyclerView
import com.rndapp.task_feed.adapters.items.SimpleViewModel
import com.rndapp.task_feed.listeners.OnDayTaskClickedListener
import com.rndapp.task_feed.models.DayTask
import com.rndapp.task_feed.models.ProjectColor
import com.thryvinc.thux.into

class DayTaskAdapter(var dayTasks: ArrayList<DayTask>, listener: OnDayTaskClickedListener):
        SimpleListAdapter<DayTask>(dayTasks, ::dayTaskToSimpleViewModel, listener::onDayTaskClicked) { //, RearrangementListener {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val dayTask = dayTasks[position]
        if (dayTask.task?.project?.color != null) {
            val color = ProjectColor.idFromProjectColor(dayTask.task.project!!.color)
            holder.itemView.setBackgroundColor(holder.itemView.context.resources.getColor(color))
        }
    }

//    override fun swapElements(indexOne: Int, indexTwo: Int) {
//
//        val temp1 = filteredDayTasks[indexOne]
//        val temp2 = filteredDayTasks[indexTwo]
//
//        val one = dayTasks.indexOf(temp1)
//        val two = dayTasks.indexOf(temp2)
//
//        dayTasks.removeAt(one)
//        dayTasks.add(one, temp2)
//
//        dayTasks.removeAt(two)
//        dayTasks.add(two, temp1)
//    }
}

fun dayTaskToSimpleViewModel(dayTask: DayTask): SimpleViewModel {
    return dayTask.task!! into ::taskToSimpleViewModel //SimpleViewModel(dayTask.task?.name ?: "", (dayTask.task?.points ?: 0).toString())
}
