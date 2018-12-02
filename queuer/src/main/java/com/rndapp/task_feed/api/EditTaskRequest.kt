package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rndapp.task_feed.models.Task
import org.json.JSONObject

class EditTaskRequest: AuthedJsonObjectRequest {

    constructor(task: Task, listener: Response.Listener<Task>,
                errorListener: Response.ErrorListener):
            super(Method.PUT, QueuerServerConfiguration.BASE_URL + "projects/" + task.project_id + "/tasks/" + task.id,
                    JSONObject("{\"task\":{\"points\":${task.points},\"name\":\"${task.name}\"}}"),
                    Response.Listener<JSONObject> { response ->
                        GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        val serverTask= Gson().fromJson<Task>(response.toString(), Task::class.java)
                        listener.onResponse(serverTask)
                    }, errorListener)
}