package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.GsonBuilder
import com.rndapp.task_feed.models.Day
import org.json.JSONObject

/**
 * Created by ell on 11/26/17.
 */
class DayRequest: AuthedJsonObjectRequest {
    constructor(sprintId: Int, day: Day, listener: Response.Listener<Day>,
                errorListener: Response.ErrorListener):
            super(Method.GET, QueuerServerConfiguration.BASE_URL + "sprints/${sprintId}/days/${day.id}", null, Response.Listener<JSONObject> { response ->
                val serverDay = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create().fromJson<Day>(response.toString(), Day::class.java)
                listener.onResponse(serverDay)
            }, errorListener)
}