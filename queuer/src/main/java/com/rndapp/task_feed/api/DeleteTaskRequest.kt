package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.GsonBuilder
import com.rndapp.task_feed.models.Task
import org.json.JSONObject

class DeleteTaskRequest: AuthedJsonObjectRequest {
    constructor(projectId: Int, taskId: Int, listener: Response.Listener<Task>,
                errorListener: Response.ErrorListener) :
            super(Method.DELETE, QueuerServerConfiguration.BASE_URL + "projects/${projectId}/tasks/${taskId}", null, Response.Listener<JSONObject> { response ->
                val serverDay = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create().fromJson<Task>(response.toString(), Task::class.java)
                listener.onResponse(serverDay)
            }, errorListener)
}