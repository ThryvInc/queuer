package com.rndapp.task_feed.api

import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.rndapp.task_feed.managers.SessionManager
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by ell on 11/26/17.
 */
open class AuthedJsonObjectRequest : JsonObjectRequest {

    @Throws(AuthFailureError::class)
    override fun getHeaders(): Map<String, String> {
        val params = HashMap<String, String>()
        params.put("X-Qer-Authorization", SessionManager.getApiKey())
        params.put("Content-type", "application/json")
        params.put("Accept", "*/*")
        return params
    }

    constructor(url: String,
                json: JSONObject?,
                listener: Response.Listener<JSONObject>,
                errorListener: Response.ErrorListener): super(url, json, listener, errorListener) {}

    constructor(method: Int, url: String, json: JSONObject?,
                listener: Response.Listener<JSONObject>,
                errorListener: Response.ErrorListener): super(method, url, json, listener, errorListener) {}
}