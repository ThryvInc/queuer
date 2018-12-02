package com.rndapp.task_feed.api

import com.android.volley.Response
import com.rndapp.task_feed.models.DayTask
import org.json.JSONObject

class DeleteDayTaskRequest : AuthedJsonObjectRequest {

    constructor(sprintId: Int, dayId: Int, dayTaskId: Int, listener: Response.Listener<DayTask?>?,
                errorListener: Response.ErrorListener):
            super(Method.DELETE, QueuerServerConfiguration.BASE_URL + "sprints/${sprintId}/days/${dayId}/day_tasks/${dayTaskId}",
                    null, Response.Listener<JSONObject> { response ->
                        listener?.onResponse(null)
                    }, errorListener)
}