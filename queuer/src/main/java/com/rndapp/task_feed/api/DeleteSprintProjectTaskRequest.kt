package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.GsonBuilder
import com.rndapp.task_feed.models.SprintProject
import com.rndapp.task_feed.models.SprintProjectTask
import org.json.JSONObject

class DeleteSprintProjectTaskRequest: AuthedJsonObjectRequest {
    constructor(sprintProjectId: Int, sprintProjectTaskId: Int, listener: Response.Listener<SprintProjectTask>,
                errorListener: Response.ErrorListener) :
            super(Method.DELETE, QueuerServerConfiguration.BASE_URL + "sprint_projects/${sprintProjectId}/sprint_project_tasks/${sprintProjectTaskId}", null, Response.Listener<JSONObject> { response ->
                val serverDay = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create().fromJson<SprintProjectTask>(response.toString(), SprintProjectTask::class.java)
                listener.onResponse(serverDay)
            }, errorListener)
}