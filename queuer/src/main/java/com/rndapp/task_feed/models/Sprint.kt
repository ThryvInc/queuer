package com.rndapp.task_feed.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class Sprint(val id: Int,
                  @SerializedName("start_date") var startDate: Date?,
                  @SerializedName("end_date") var endDate: Date?,
                  var name: String?,
                  @SerializedName("sprint_projects")var sprintProjects: ArrayList<SprintProject>,
                  var days: ArrayList<Day>,
                  var points: Int = 0,
                  @SerializedName("finished_points") var finishedPoints: Int = 0): Serializable, Comparable<Sprint> {

    override fun compareTo(other: Sprint): Int {
        return this.startDate?.compareTo(other.startDate) ?: 0
    }


    fun nameFromStartDate(): String? =
            "Sprint starting ${SimpleDateFormat("MMM dd, yy").format(startDate)}"
}