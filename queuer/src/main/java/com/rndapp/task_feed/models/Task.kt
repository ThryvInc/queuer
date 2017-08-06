package com.rndapp.task_feed.models

import android.content.Context

import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.rndapp.task_feed.api.ServerCommunicator
import org.json.JSONObject

import java.io.Serializable
import java.util.Date

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 1:32 PM

 */
class Task : Serializable, Comparable<Task> {
    var id: Int = 0
    var localId: Int = 0
    var project_id: Int = 0
    var name: String? = null
    var isFinished = false
    var points = 1
    var order: Int = 0
    var created_at: Date? = null
    var updated_at: Date? = null

    override fun compareTo(task: Task): Int {
        return this.order - task.order
    }

    fun isUpToDateWithServerTask(serverTask: Task): Boolean {
        return this.updated_at == serverTask.updated_at
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val task = o as Task?

        if (project_id != task!!.project_id) return false
        if (id != task.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + localId
        result = 31 * result + project_id
        result = 31 * result + name!!.hashCode()
        return result
    }

    override fun toString(): String {
        return name ?: ""
    }

    companion object {

        fun uploadTaskToServer(context: Context,
                               queue: RequestQueue,
                               task: Task,
                               listener: Response.Listener<JSONObject>,
                               errorListener: Response.ErrorListener) {
            ServerCommunicator.uploadTaskToServer(context, task, queue, listener, errorListener)
        }

        fun markAsFinished(context: Context, queue: RequestQueue, task: Task): Task {
            task.isFinished = true
            updateTaskOnServer(context, queue, task)
            return task
        }

        fun updateTaskOnServer(context: Context, queue: RequestQueue, task: Task): Task {
            ServerCommunicator.updateTask(context,
                    queue,
                    task,
                    { o: JSONObject ->
                    } as Response.Listener<JSONObject>,
                    Response.ErrorListener { })
            return task
        }
    }
}
