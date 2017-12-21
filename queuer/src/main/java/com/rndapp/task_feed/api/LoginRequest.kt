package com.rndapp.task_feed.api

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.rndapp.task_feed.models.SignInModel
import org.json.JSONObject

class LoginRequest: JsonObjectRequest {
    constructor(signInModel: SignInModel, listener: Response.Listener<JSONObject>,
                errorListener: Response.ErrorListener):
            super(Method.POST, QueuerServerConfiguration.BASE_URL + "session",
                    JSONObject(Gson().toJson(signInModel)),
                    Response.Listener<JSONObject> { response ->
                        listener.onResponse(response)
                    }, errorListener)
}