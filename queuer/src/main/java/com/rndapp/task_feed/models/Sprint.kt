package com.rndapp.task_feed.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ell on 8/5/17.
 */
data class Sprint(val id: Int,
                  @SerializedName("start_date") var startDate: Date?,
                  @SerializedName("end_date") var endDate: Date?,
                  var name: String?,
                  var projects: ArrayList<Project>,
                  var days: ArrayList<Day>): Serializable, Comparable<Sprint> {

    override fun compareTo(other: Sprint): Int {
        return this.startDate?.compareTo(other.startDate) ?: 0
    }


    fun nameFromStartDate(): String? =
            "Sprint starting ${SimpleDateFormat("MMM dd, yy").format(startDate)}"
}