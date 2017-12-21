package com.rndapp.task_feed.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ell on 8/5/17.
 */
data class Day(val id: Int, val date: Date?, @SerializedName("day_tasks") var dayTasks: ArrayList<DayTask>?): Serializable, Comparable<Day> {

    override fun compareTo(other: Day): Int {
        return this.date?.compareTo(other.date) ?: 0
    }

    fun nameFromDate(): String? = "${SimpleDateFormat("MMM dd, yy").format(date)}"
}