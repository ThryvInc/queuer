package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rndapp.task_feed.models.Sprint
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ell on 11/26/17.
 */
class CreateSprintRequest: AuthedJsonObjectRequest {

    constructor(startDate: Date, listener: Response.Listener<Sprint>,
                errorListener: Response.ErrorListener):
            super(Method.POST, QueuerServerConfiguration.BASE_URL + "sprints",
                    JSONObject("{\"sprint\":{\"start_date\":\"${SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(startDate)}\"}}"),
                    Response.Listener<JSONObject> { response ->
                        GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val serverSprint= Gson().fromJson<Sprint>(response.toString(), Sprint::class.java)
                listener.onResponse(serverSprint)
            }, errorListener)
}