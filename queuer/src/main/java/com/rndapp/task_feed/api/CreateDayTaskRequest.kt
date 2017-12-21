package com.rndapp.task_feed.api

import com.android.volley.Response
import com.rndapp.task_feed.models.Sprint
import org.json.JSONObject

class CreateDayTaskRequest: AuthedJsonObjectRequest {

    constructor(sprintId: Int, dayId: Int, taskId: Int, listener: Response.Listener<Sprint?>,
                errorListener: Response.ErrorListener):
            super(Method.POST, QueuerServerConfiguration.BASE_URL + "sprints/${sprintId}/days/${dayId}/day_tasks",
                    JSONObject("{\"day_task\":{\"task_id\":${taskId}}}"),
                    Response.Listener<JSONObject> { response ->
                        listener.onResponse(null)
                    }, errorListener)
}