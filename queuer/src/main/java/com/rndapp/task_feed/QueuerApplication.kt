package com.rndapp.task_feed

import android.app.Activity
import android.app.Application
import com.rndapp.task_feed.api.VolleyManager
import com.rndapp.task_feed.managers.SessionManager

/**
 * Created by eschrock on 2/4/14.
 */
class QueuerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        VolleyManager.init(this)
        SessionManager.preferences = this.getSharedPreferences(SessionManager.API_KEY_PREFERENCE, Activity.MODE_PRIVATE)
    }
}
