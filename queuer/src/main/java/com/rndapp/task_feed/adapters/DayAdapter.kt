package com.rndapp.task_feed.adapters

import com.rndapp.task_feed.adapters.items.SimpleViewModel
import com.rndapp.task_feed.listeners.OnDayClickedListener
import com.rndapp.task_feed.models.Day

/**
 * Created by ell on 8/5/17.
 */

class DayAdapter(days: List<Day>, listener: OnDayClickedListener):
        SimpleListAdapter<Day>(days, ::dayToSimpleViewModel, listener::onDayClicked)

fun dayToSimpleViewModel(day: Day): SimpleViewModel {
    return SimpleViewModel(day.nameFromDate() ?: "", day.points.toString())
}
