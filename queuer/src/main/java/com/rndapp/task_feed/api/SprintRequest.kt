package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.GsonBuilder
import com.rndapp.task_feed.models.Sprint
import org.json.JSONObject

/**
 * Created by ell on 11/26/17.
 */
class SprintRequest: AuthedJsonObjectRequest {
    constructor(sprint: Sprint, listener: Response.Listener<Sprint>,
                errorListener: Response.ErrorListener):
            super(Method.GET, QueuerServerConfiguration.BASE_URL + "sprints/${sprint.id}", null, Response.Listener<JSONObject> { response ->
                val serverSprints= GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create().fromJson<Sprint>(response.toString(), Sprint::class.java)
                listener.onResponse(serverSprints)
            }, errorListener)
}