package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.GsonBuilder
import com.rndapp.task_feed.models.Project
import org.json.JSONObject

class ProjectRequest: AuthedJsonObjectRequest {
    constructor(projectId: Int, listener: Response.Listener<Project>,
                errorListener: Response.ErrorListener):
            super(Method.GET, QueuerServerConfiguration.BASE_URL + "projects/${projectId}", null, Response.Listener<JSONObject> { response ->
                val serverProject = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create().fromJson<Project>(response.toString(), Project::class.java)
                listener.onResponse(serverProject)
            }, errorListener)
}