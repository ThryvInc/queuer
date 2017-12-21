package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rndapp.task_feed.models.Project

class ProjectsRequest: AuthedJsonArrayRequest {
    constructor(listener: Response.Listener<ArrayList<Project>>,
                errorListener: Response.ErrorListener):
            super(QueuerServerConfiguration.BASE_URL + "projects", Response.Listener { response ->
                val listOfModels = object : TypeToken<ArrayList<Project>>() {}.type
                val serverModels= GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create().fromJson<ArrayList<Project>>(response.toString(), listOfModels)
                listener.onResponse(serverModels)
            }, errorListener)
}
