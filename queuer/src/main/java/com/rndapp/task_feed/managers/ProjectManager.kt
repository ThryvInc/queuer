package com.rndapp.task_feed.managers

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.rndapp.task_feed.api.ServerCommunicator
import com.rndapp.task_feed.models.Project
import org.json.JSONObject

/**
 * Created by ell on 8/5/17.
 */
class ProjectManager {

    companion object {

        fun uploadProjectToServer(context: Context, queue: RequestQueue, project: Project,
                                  listener: Response.Listener<JSONObject>,
                                  errorListener: Response.ErrorListener) {

            ServerCommunicator.uploadProjectToServer(context, project, queue,
                    listener,
                    errorListener)
        }

        fun updateProjectOnServer(context: Context, queue: RequestQueue, project: Project,
                                  listener: Response.Listener<JSONObject>,
                                  errorListener: Response.ErrorListener) {
            ServerCommunicator.updateProjectOnServer(context,
                    project,
                    queue,
                    listener,
                    errorListener
            )
        }
    }
}