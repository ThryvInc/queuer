package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rndapp.task_feed.models.Day
import org.json.JSONArray

class DaysRequest: AuthedJsonArrayRequest {
    constructor(sprintId: Int, listener: Response.Listener<ArrayList<Day>>,
                errorListener: Response.ErrorListener):
            super(QueuerServerConfiguration.BASE_URL + "sprints/${sprintId}/days", Response.Listener<JSONArray> { response ->
                val listOfDays = object : TypeToken<ArrayList<Day>>() {}.type
                val serverDays = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create().fromJson<ArrayList<Day>>(response.toString(), listOfDays)
                listener.onResponse(serverDays)
            }, errorListener)
}