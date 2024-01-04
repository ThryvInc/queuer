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
                         @SerializedName("sprint_project_tasks") var sprintProjectTasks: ArrayList<SprintProjectTask>? = null): Serializable {

    fun unfinishedSprintTasks() = sprintProjectTasks?.filter { !(it.task?.isFinished ?: false) }?.sortedBy { it.task?.created_at }
    fun finishedSprintTasks() = sprintProjectTasks?.filter { it.task?.isFinished ?: true }?.sortedBy { it.task?.created_at }

    fun unfinishedTasks() = unfinishedSprintTasks()?.filter { it.task != null }?.map { it.task!! }
    fun finishedTasks() = finishedSprintTasks()?.filter { it.task != null }?.map { it.task!! }
}
