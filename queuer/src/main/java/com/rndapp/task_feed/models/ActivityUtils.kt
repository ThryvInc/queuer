package com.rndapp.task_feed.models

import android.app.Activity
import android.content.Context
import android.content.Intent

import com.android.volley.RequestQueue
import com.android.volley.Response
import com.rndapp.task_feed.activities.LoginActivity
import com.rndapp.task_feed.api.ServerCommunicator
import org.json.JSONArray

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 7/14/13
 * Time: 10:56 AM
 */
object ActivityUtils {
    val USER_CREDENTIALS_PREF = "com.rndapp.queuer.user_creds"
    val USER_ID_PREF = "com.rndapp.queuer.user_id_pref"

    fun saveApiKey(context: Context, apiKey: String) {
        val sp = context.getSharedPreferences(ServerCommunicator.API_KEY_PREFERENCE, Activity.MODE_PRIVATE)
        val editPrefs = sp.edit()
        //store api key
        editPrefs.putString("api_key", apiKey)
        editPrefs.commit()
    }

    fun saveUserId(context: Context, userId: Int) {
        val sp = context.getSharedPreferences(USER_ID_PREF, Activity.MODE_PRIVATE)
        val editPrefs = sp.edit()
        //store api key
        editPrefs.putInt("user_id", userId)
        editPrefs.commit()
    }

    fun saveUserCredential(context: Context, credKey: String, credential: String) {
        val sp = context.getSharedPreferences(USER_CREDENTIALS_PREF, Activity.MODE_PRIVATE)
        val editPrefs = sp.edit()
        //store api key
        editPrefs.putString(credKey, credential)
        editPrefs.commit()
    }

    fun getUserCredential(context: Context, credKey: String, credential: String): String {
        return context.getSharedPreferences(USER_CREDENTIALS_PREF, Activity.MODE_PRIVATE)
                .getString(credKey, credential)
    }

    fun setCredentialBoolean(context: Context, credKey: String, cred: Boolean) {
        val sp = context.getSharedPreferences(USER_CREDENTIALS_PREF, Activity.MODE_PRIVATE)
        val editPrefs = sp.edit()
        //store api key
        editPrefs.putBoolean(credKey, cred)
        editPrefs.commit()
    }

    fun getCredentialBoolean(context: Context, credKey: String, cred: Boolean): Boolean {
        return context.getSharedPreferences(USER_CREDENTIALS_PREF, Activity.MODE_PRIVATE)
                .getBoolean(credKey, cred)
    }

    fun downloadProjectsFromServer(context: Context,
                                   queue: RequestQueue,
                                   listener: Response.Listener<JSONArray>,
                                   errorListener: Response.ErrorListener) {
        ServerCommunicator.downloadProjectsFromServer(context, queue, listener, errorListener)
    }

    fun logout(activity: Activity) {
        saveApiKey(activity, "")
        activity.startActivity(Intent(activity, LoginActivity::class.java))
        activity.finish()
    }
}
