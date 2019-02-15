package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.GsonBuilder
import com.rndapp.task_feed.models.SprintProject
import org.json.JSONObject

class SprintProjectRequest: AuthedJsonObjectRequest {
    constructor(sprintProjectId: Int, listener: Response.Listener<SprintProject>,
                errorListener: Response.ErrorListener) :
            super(Method.GET, QueuerServerConfiguration.BASE_URL + "sprint_projects/${sprintProjectId}", null, Response.Listener<JSONObject> { response ->
                val serverDay = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create().fromJson<SprintProject>(response.toString(), SprintProject::class.java)
                listener.onResponse(serverDay)
            }, errorListener)
}
