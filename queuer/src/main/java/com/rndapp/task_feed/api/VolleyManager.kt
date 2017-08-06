package com.rndapp.task_feed.api

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

/**
 * Created by ell on 8/5/17.
 */
class VolleyManager {
    companion object {
        var queue: RequestQueue? = null

        fun init(context: Context) {
            if (queue == null) queue = Volley.newRequestQueue(context)
        }
    }
}