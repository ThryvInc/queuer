package com.rndapp.task_feed.managers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.android.volley.Response
import com.rndapp.task_feed.activities.LoginActivity
import com.rndapp.task_feed.api.LoginRequest
import com.rndapp.task_feed.api.SignUpRequest
import com.rndapp.task_feed.api.VolleyManager
import com.rndapp.task_feed.models.SignInModel
import org.json.JSONObject

class SessionManager {

    companion object {
        val API_KEY_PREFERENCE = "com.rndapp.queuer.api_key_pref"
        val USER_CREDENTIALS_PREF = "com.rndapp.queuer.user_creds"
        val USER_ID_PREF = "com.rndapp.queuer.user_id_pref"

        var preferences: SharedPreferences? = null

        fun saveApiKey(context: Context, apiKey: String) {
            val sp = context.getSharedPreferences(API_KEY_PREFERENCE, Activity.MODE_PRIVATE)
            val editPrefs = sp.edit()
            //store api key
            editPrefs.putString("api_key", apiKey)
            editPrefs.commit()
            SessionManager.preferences = sp
        }

        fun getApiKey(): String {
            val apiKey = preferences?.getString("api_key", "") ?: ""
            return apiKey
        }

        fun createAccount(username: String, password: String,
                          listener: Response.Listener<JSONObject>,
                          errorListener: Response.ErrorListener) {
            //get user details
            val signInModel = SignInModel()
            signInModel.username = username
            signInModel.password = password

            val request = SignUpRequest(signInModel, listener, errorListener)
            VolleyManager.queue?.add(request)
        }

        fun login(username: String, password: String,
                  listener: Response.Listener<JSONObject>,
                  errorListener: Response.ErrorListener) {
            val signInModel = SignInModel()
            signInModel.username = username
            signInModel.password = password

            val request = LoginRequest(signInModel, listener, errorListener)
            VolleyManager.queue?.add(request)
        }

        fun logout(activity: Activity) {
            saveApiKey(activity, "")
            activity.startActivity(Intent(activity, LoginActivity::class.java))
            activity.finish()
        }

        fun saveUserCredential(context: Context, credKey: String, credential: String) {
            val sp = context.getSharedPreferences(USER_CREDENTIALS_PREF, Activity.MODE_PRIVATE)
            val editPrefs = sp.edit()
            //store api key
            editPrefs.putString(credKey, credential)
            editPrefs.apply()
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
            editPrefs.apply()
        }

        fun getCredentialBoolean(context: Context, credKey: String, cred: Boolean): Boolean {
            return context.getSharedPreferences(USER_CREDENTIALS_PREF, Activity.MODE_PRIVATE)
                    .getBoolean(credKey, cred)
        }

        fun saveUserId(context: Context, userId: Int) {
            val sp = context.getSharedPreferences(USER_ID_PREF, Activity.MODE_PRIVATE)
            val editPrefs = sp.edit()
            //store api key
            editPrefs.putInt("user_id", userId)
            editPrefs.commit()
        }
    }
}