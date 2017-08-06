package com.rndapp.task_feed

import android.app.Application

import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.rndapp.task_feed.api.VolleyManager

/**
 * Created by eschrock on 2/4/14.
 */
class QueuerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        VolleyManager.init(this)
    }
}
