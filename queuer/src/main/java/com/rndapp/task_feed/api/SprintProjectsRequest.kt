package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rndapp.task_feed.models.SprintProject
import org.json.JSONArray
import org.json.JSONObject

class SprintProjectsRequest: AuthedJsonArrayRequest {
    constructor(sprintId: Int, listener: Response.Listener<List<SprintProject>>,
                errorListener: Response.ErrorListener) :
            super(QueuerServerConfiguration.BASE_URL + "sprints/${sprintId}/sprint_projects", Response.Listener<JSONArray> { response ->
                val listOfObjects = object : TypeToken<ArrayList<SprintProject>>() {}.type
                val serverObject = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create().fromJson<List<SprintProject>>(response.toString(), listOfObjects)
                listener.onResponse(serverObject)
            }, errorListener)
}