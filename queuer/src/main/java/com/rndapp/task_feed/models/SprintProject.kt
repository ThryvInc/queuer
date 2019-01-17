package com.rndapp.task_feed.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

/**
 * Created by ell on 8/5/17.
 */
data class SprintProject(val id: Int? = null,
                         @SerializedName("created_at") var createdAt: Date? = null,
                         @SerializedName("project_id") val projectId: Int,
                         @SerializedName("sprint_id") var sprintId: Int? = null,
                         val project: Project? = null,
                         val points: Int? = null,
                         @SerializedName("remaining_points") var remainingPoints: Int? = null,
                         @SerializedName("sprint_project_tasks") var sprintProjectTasks: ArrayList<SprintProjectTask>? = null,
                         var tasks: ArrayList<Task>? = null): Serializable {
    fun unfinishedTasks() = tasks?.filter { !it.isFinished }?.sortedBy { it.created_at }

    fun finishedTasks() = tasks?.filter { it.isFinished }?.sortedBy { it.created_at }
}