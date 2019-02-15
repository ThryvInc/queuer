package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.GsonBuilder
import com.rndapp.task_feed.models.Task
import org.json.JSONObject

class ToggleFinishedTaskRequest : AuthedJsonObjectRequest {

    constructor(task: Task, listener: Response.Listener<Task?>,
                errorListener: Response.ErrorListener):
            super(Method.PUT, QueuerServerConfiguration.BASE_URL + "projects/${task.projectId}/tasks/${task.id}",
                    JSONObject("{\"task\":{\"finished\":${task.isFinished}}}"),
                    Response.Listener<JSONObject> { response ->
                        GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//                        val serverTask = Gson().fromJson<Task>(response.toString(), Task::class.java)
                        listener.onResponse(null)
                    }, errorListener)
}