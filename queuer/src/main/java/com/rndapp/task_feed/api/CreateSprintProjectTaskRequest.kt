package com.rndapp.task_feed.api

import com.android.volley.Response
import com.rndapp.task_feed.models.SprintProjectTask
import org.json.JSONObject

class CreateSprintProjectTaskRequest: AuthedJsonObjectRequest {
    constructor(sprintProjectId: Int, taskId: Int, listener: Response.Listener<SprintProjectTask?>,
                errorListener: Response.ErrorListener):
            super(Method.POST, QueuerServerConfiguration.BASE_URL + "sprint_projects/${sprintProjectId}/sprint_project_tasks",
                    JSONObject("{\"sprint_project_task\":{\"task_id\":$taskId}}"),
                    Response.Listener<JSONObject> { response ->
                        listener.onResponse(null)
                    }, errorListener)
}
