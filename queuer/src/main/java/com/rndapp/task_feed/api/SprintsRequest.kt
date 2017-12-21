package com.rndapp.task_feed.api

import com.android.volley.Response
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.rndapp.task_feed.models.Sprint

/**
 * Created by ell on 11/26/17.
 */
class SprintsRequest: AuthedJsonArrayRequest {
    constructor(listener: Response.Listener<List<Sprint>>,
                errorListener: Response.ErrorListener):
            super(QueuerServerConfiguration.BASE_URL + "sprints", Response.Listener { response ->
                val listOfSprints = object : TypeToken<List<Sprint>>() {}.type
                val serverSprints= GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create().fromJson<List<Sprint>>(response.toString(), listOfSprints)
                listener.onResponse(serverSprints)
            }, errorListener)
}