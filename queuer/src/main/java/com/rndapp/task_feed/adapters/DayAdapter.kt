package com.rndapp.task_feed.adapters

import com.rndapp.task_feed.listeners.OnDayClickedListener
import com.rndapp.task_feed.models.Day
import com.rndapp.task_feed.views.SimpleViewHolder

/**
 * Created by ell on 8/5/17.
 */

class DayAdapter(var days: List<Day>, val listener: OnDayClickedListener?): SimpleItemAdapter<Day>(days) {
    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        val day = days[position]
        holder.setText(day.nameFromDate(), position)
        holder.setRightText(day.points.toString())
    }

    override fun onSimpleItemClicked(id: Int) {
        listener?.onDayClicked(days[id])
    }
}
