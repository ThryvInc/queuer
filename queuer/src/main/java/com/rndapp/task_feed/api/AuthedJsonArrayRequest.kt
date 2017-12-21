package com.rndapp.task_feed.api

import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.rndapp.task_feed.managers.SessionManager
import org.json.JSONArray
import java.util.HashMap

/**
 * Created by ell on 11/26/17.
 */
open class AuthedJsonArrayRequest: JsonArrayRequest {

    @Throws(AuthFailureError::class)
    override fun getHeaders(): Map<String, String> {
        val params = HashMap<String, String>()
        params.put("X-Qer-Authorization", SessionManager.getApiKey())
        params.put("Content-type", "application/json")
        params.put("Accept", "*/*")
        return params
    }

    constructor(url: String,
                listener: Response.Listener<JSONArray>,
                errorListener: Response.ErrorListener): super(url, listener, errorListener) {

    }

    constructor(method: Int, url: String, jsonRequest: JSONArray,
                listener: Response.Listener<JSONArray>,
                errorListener: Response.ErrorListener): super(method, url, jsonRequest, listener, errorListener) {

    }
}