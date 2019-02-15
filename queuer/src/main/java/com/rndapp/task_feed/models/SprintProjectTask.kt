package com.rndapp.task_feed.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SprintProjectTask(val id: Int? = null,
                             @SerializedName("sprint_project_id") var sprintProjectId: Int? = null,
                             @SerializedName("task_id") val taskId: Int? = null,
                             var task: Task? = null): Serializable