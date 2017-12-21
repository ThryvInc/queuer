package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rndapp.task_feed.models.Project
import org.json.JSONObject

class EditProjectRequest: AuthedJsonObjectRequest {

    constructor(project: Project, listener: Response.Listener<Project>,
                errorListener: Response.ErrorListener):
            super(Method.PUT, QueuerServerConfiguration.BASE_URL + "projects",
                    JSONObject("{\"project\":${Gson().toJson(Project(name = project.name, color = project.color))}}"),
                    Response.Listener<JSONObject> { response ->
                        GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        val serverModel= Gson().fromJson<Project>(response.toString(), Project::class.java)
                        listener.onResponse(serverModel)
                    }, errorListener)
}