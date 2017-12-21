package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.Gson
import com.rndapp.task_feed.models.Sprint
import com.rndapp.task_feed.models.SprintProject
import org.json.JSONObject

/**
 * Created by ell on 11/26/17.
 */
class CreateSprintProjectRequest: AuthedJsonObjectRequest {

    constructor(sprintId: Int, sprintProject: SprintProject, listener: Response.Listener<Sprint?>,
                errorListener: Response.ErrorListener):
            super(Method.POST, QueuerServerConfiguration.BASE_URL + "sprints/${sprintId}/sprint_projects",
                    JSONObject("{\"sprint_project\":${Gson().toJson(sprintProject)}}"),
                    Response.Listener<JSONObject> { response ->
                        listener.onResponse(null)
                    }, errorListener)
}