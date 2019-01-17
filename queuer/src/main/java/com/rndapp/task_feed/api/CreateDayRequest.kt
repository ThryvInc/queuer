package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rndapp.task_feed.models.Day
import com.rndapp.task_feed.models.Sprint
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ell on 11/26/17.
 */
class CreateDayRequest: AuthedJsonObjectRequest {

    constructor(sprintId: Int, date: Date, listener: Response.Listener<Day>,
                errorListener: Response.ErrorListener):
            super(Method.POST, QueuerServerConfiguration.BASE_URL + "sprints/" + sprintId + "/days",
                    JSONObject("{\"day\":{\"date\":\"${SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(date)}\"}}"),
                    Response.Listener<JSONObject> { response ->
                        GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                        val serverDay = Gson().fromJson<Day>(response.toString(), Day::class.java)
                        listener.onResponse(serverDay)
                    }, errorListener)
}
