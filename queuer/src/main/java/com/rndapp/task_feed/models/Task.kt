package com.rndapp.task_feed.models

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.io.Serializable
import java.util.*

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 1:32 PM

 */
class Task : Serializable {
    var id: Int = 0
    var localId: Int = 0
    var project_id: Int = 0
    var project: Project? = null
    var name: String? = null
    @SerializedName("finished")
    var isFinished = false
    var points = 1
    var created_at: Date? = null
    var updated_at: Date? = null

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val task = o as Task?

        if (project_id != task!!.project_id) return false
        if (id != task.id) return false

        return true
    }

    override fun toString(): String {
        return name ?: ""
    }
}
