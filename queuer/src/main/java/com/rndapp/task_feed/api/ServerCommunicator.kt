package com.rndapp.task_feed.api

import java.util.HashMap

import org.json.JSONException
import org.json.JSONObject

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.rndapp.task_feed.models.ActivityUtils
import com.rndapp.task_feed.models.NewUserModel
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.models.SignInModel
import com.rndapp.task_feed.models.Task
import org.json.JSONArray

class ServerCommunicator {
    private val context: Context? = null

    companion object {
        val USER_CREDENTIALS_PREF = "com.rndapp.queuer.user_creds"
        val USER_ID_PREF = "com.rndapp.queuer.user_id_pref"

        val BASE_URL = "https://queuer-rndapp.rhcloud.com/api/v1/" //192.168.11.152:3000
        val API_KEY_PREFERENCE = "com.rndapp.queuer.api_key_pref"
        val API_KEY_HEADER = "X-Qer-Authorization"
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(1000, 0, 1.0.toFloat())

        fun createAccount(context: Context,
                          queue: RequestQueue,
                          username: String, password: String,
                          listener: Response.Listener<JSONObject>,
                          errorListener: Response.ErrorListener) {
            //get user details
            val signInModel = SignInModel()
            signInModel.username = username
            signInModel.password = password

            var newUser: JSONObject? =
                    null
            try {
                newUser = JSONObject(Gson().toJson(NewUserModel(signInModel), NewUserModel::class.java!!))
                postToEndpointUnauthed("users", newUser, queue, listener, errorListener)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        fun login(context: Context,
                  queue: RequestQueue,
                  username: String, password: String,
                  listener: Response.Listener<JSONObject>,
                  errorListener: Response.ErrorListener) {
            //get user details
            val signInModel = SignInModel()
            signInModel.username = username
            signInModel.password = password

            var signInJson: JSONObject? = null
            try {
                signInJson = JSONObject(Gson().toJson(signInModel))
                postToEndpointUnauthed("session", signInJson, queue, listener, errorListener)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun downloadProjectsFromServer(context: Context,
                                       queue: RequestQueue,
                                       listener: Response.Listener<JSONArray>,
                                       errorListener: Response.ErrorListener) {

            val sp = context.getSharedPreferences(USER_ID_PREF, Activity.MODE_PRIVATE)
            getEndpointAuthed("users/" + sp.getInt("user_id", 0) + "/projects",
                    context,
                    queue,
                    listener,
                    errorListener)
        }

        fun uploadProjectToServer(context: Context, project: Project,
                                  queue: RequestQueue,
                                  listener: Response.Listener<JSONObject>,
                                  errorListener: Response.ErrorListener) {
            val hash = HashMap<String, Any>()
            hash.put("project", project)
            val newProject = Project()
            newProject.color = project.color
            newProject.name = project.name
            try {
                val jsonObject = JSONObject(Gson().toJson(hash))
                val sp = context.getSharedPreferences(ActivityUtils.USER_ID_PREF, Activity.MODE_PRIVATE)
                postToEndpointAuthed("users/" + sp.getInt("user_id", 0) + "/projects", jsonObject,
                        context, queue, listener, errorListener)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun updateProjectOnServer(context: Context, project: Project,
                                  queue: RequestQueue,
                                  listener: Response.Listener<JSONObject>,
                                  errorListener: Response.ErrorListener) {
            val hash = HashMap<String, Any>()
            hash.put("project", project)
            try {
                val jsonObject = JSONObject(Gson().toJson(hash))
                val sp = context.getSharedPreferences(ActivityUtils.USER_ID_PREF, Activity.MODE_PRIVATE)
                putToEndpointAuthed("users/" + sp.getInt("user_id", 0) + "/projects/" + project.id,
                        jsonObject,
                        context,
                        queue,
                        listener,
                        errorListener)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun uploadTaskToServer(context: Context,
                               task: Task,
                               queue: RequestQueue,
                               listener: Response.Listener<JSONObject>,
                               errorListener: Response.ErrorListener) {
            val hash = HashMap<String, Any>()
            hash.put("task", task)
            try {
                val jsonObject = JSONObject(Gson().toJson(hash))
                val sp = context.getSharedPreferences(ActivityUtils.USER_ID_PREF, Activity.MODE_PRIVATE)
                postToEndpointAuthed("users/" + sp.getInt("user_id", 0) + "/projects/" + task.project_id + "/tasks",
                        jsonObject,
                        context,
                        queue,
                        listener,
                        errorListener)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun updateTask(context: Context,
                       queue: RequestQueue,
                       task: Task,
                       listener: Response.Listener<JSONObject>,
                       errorListener: Response.ErrorListener) {
            val hash = HashMap<String, Any>()
            hash.put("task", task)
            try {
                val jsonObject = JSONObject(Gson().toJson(hash))

                val sp = context.getSharedPreferences(ActivityUtils.USER_ID_PREF, Activity.MODE_PRIVATE)
                putToEndpointAuthed("users/" + sp.getInt("user_id", 0) + "/projects/" + task.project_id + "/tasks/" + task.id,
                        jsonObject, context, queue, listener, errorListener)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        private fun postToEndpointAuthed(endpoint: String,
                                         postData: JSONObject,
                                         context: Context,
                                         queue: RequestQueue,
                                         listener: Response.Listener<JSONObject>,
                                         errorListener: Response.ErrorListener) {

            val sp = context.getSharedPreferences(API_KEY_PREFERENCE, Activity.MODE_PRIVATE)
            val request = object : JsonObjectRequest(Request.Method.POST,
                    BASE_URL + endpoint,
                    postData,
                    listener,
                    errorListener) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params.put(API_KEY_HEADER, sp.getString("api_key", ""))
                    params.put("Content-type", "application/json; charset=utf-8")
                    return params
                }
            }
            request.retryPolicy = DefaultRetryPolicy(30 * 1000, 0, 0f)
            queue.add(request)
        }

        private fun putToEndpointAuthed(endpoint: String,
                                        postData: JSONObject,
                                        context: Context,
                                        queue: RequestQueue,
                                        listener: Response.Listener<JSONObject>,
                                        errorListener: Response.ErrorListener) {

            val sp = context.getSharedPreferences(API_KEY_PREFERENCE, Activity.MODE_PRIVATE)
            val request = object : JsonObjectRequest(Request.Method.PUT,
                    BASE_URL + endpoint,
                    postData,
                    listener,
                    errorListener) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params.put(API_KEY_HEADER, sp.getString("api_key", ""))
                    params.put("Content-type", "application/json; charset=utf-8")
                    return params
                }
            }
            request.retryPolicy = DefaultRetryPolicy(30 * 1000, 0, 0f)
            queue.add(request)
        }

        private fun postToEndpointUnauthed(endpoint: String,
                                           postData: JSONObject,
                                           queue: RequestQueue,
                                           listener: Response.Listener<JSONObject>,
                                           errorListener: Response.ErrorListener) {
            Log.d("postData", postData.toString())
            val request = object : JsonObjectRequest(Request.Method.POST,
                    BASE_URL + endpoint,
                    postData,
                    listener,
                    errorListener) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params.put("Content-type", "application/json; charset=utf-8")
                    return params
                }
            }
            request.retryPolicy = DefaultRetryPolicy(30 * 1000, 0, 0f)
            queue.add(request)
        }

        private fun getEndpointUnauthed(endpoint: String,
                                        queue: RequestQueue,
                                        listener: Response.Listener<JSONObject>,
                                        errorListener: Response.ErrorListener) {
            val request = object : JsonObjectRequest(Request.Method.GET,
                    BASE_URL + endpoint, null,
                    listener,
                    errorListener) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params.put("Content-type", "application/json; charset=utf-8")
                    return params
                }
            }
            request.retryPolicy = DefaultRetryPolicy(30 * 1000, 0, 0f)
            queue.add(request)
        }

        private fun getEndpointAuthed(endpoint: String,
                                      context: Context,
                                      queue: RequestQueue,
                                      listener: Response.Listener<JSONArray>,
                                      errorListener: Response.ErrorListener) {

            val sp = context.getSharedPreferences(API_KEY_PREFERENCE, Activity.MODE_PRIVATE)
            val apiKey = sp.getString("api_key", "")
            val request = object : JsonArrayRequest(BASE_URL + endpoint,
                    listener,
                    errorListener) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params.put(API_KEY_HEADER, apiKey)
                    params.put("Content-type", "application/json; charset=utf-8")
                    return params
                }
            }
            request.retryPolicy = DefaultRetryPolicy(30 * 1000, 0, 0f)
            queue.add(request)
        }
    }

}
