package com.rndapp.task_feed.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView

import com.android.volley.Response
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.rndapp.task_feed.QueuerApplication
import com.rndapp.task_feed.R
import com.rndapp.task_feed.api.ServerCommunicator
import com.rndapp.task_feed.api.VolleyManager
import com.rndapp.task_feed.models.ActivityUtils
import com.rndapp.task_feed.models.User
import org.json.JSONObject

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 7/13/13
 * Time: 2:12 PM
 */
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private var user: User? = null

    internal var userField: EditText? = null
    internal var passField: EditText? = null
    internal var remember: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sp = getSharedPreferences(ServerCommunicator.API_KEY_PREFERENCE, Activity.MODE_PRIVATE)
        val apiKey = sp.getString("api_key", "")
        if (apiKey != "") {
            startActivity(Intent(this, FeedActivity::class.java))
            finish()
        }

        userField = findViewById(R.id.username_field) as EditText
        passField = findViewById(R.id.password_field) as EditText
        remember = findViewById(R.id.remember_me_checkbox) as CheckBox

        val savedUsername = ActivityUtils.getUserCredential(this, USERNAME_KEY, "-1")
        val savedPassword = ActivityUtils.getUserCredential(this, PASSWORED_KEY, "-1")
        val saveCreds = ActivityUtils.getCredentialBoolean(this, REMEMBER_KEY, false)

        if (saveCreds) {
            userField!!.setText(savedUsername)
            passField!!.setText(savedPassword)
        }

        remember!!.isChecked = saveCreds

        findViewById(R.id.login_button).setOnClickListener(this)
        findViewById(R.id.create_account_button).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.login_button -> if (userField!!.text.toString() != "" && passField!!.text.toString() != "") {
                findViewById(R.id.progress_bar).visibility = View.VISIBLE
                ServerCommunicator.login(this, VolleyManager.queue!!,
                        userField!!.text.toString(), passField!!.text.toString(),
                        object : Response.Listener<JSONObject> {
                            internal var errored: Boolean = false
                            internal var errorText: String? = null

                            override fun onResponse(jsob: JSONObject) {
                                try {
                                    Log.d("Received from /sessions", jsob.toString())
                                    if (jsob.has("api_key") && jsob.getString("api_key") != null) {
                                        val apiKey = jsob.getString("api_key")
                                        ActivityUtils.saveApiKey(this@LoginActivity, apiKey)
                                        //create user using Gson
                                        user = Gson().fromJson<User>(jsob.toString(), User::class.java!!)
                                        ActivityUtils.saveUserId(this@LoginActivity, user!!.id)
                                    } else if (jsob.has("errors")) {
                                        //error
                                        errored = true
                                        errorText = jsob.getString("errors")
                                    } else {
                                        //error
                                        errored = true
                                        errorText = "Unknown Error"
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    //error
                                    errored = true
                                    errorText = e.localizedMessage
                                }

                                runOnUiThread {
                                    findViewById(R.id.progress_bar).visibility = View.GONE
                                    //go to the next activity
                                    if (!errored && user != null) {
                                        this@LoginActivity
                                                .startActivity(Intent(this@LoginActivity, FeedActivity::class.java))
                                        this@LoginActivity.finish()
                                    } else {
                                        (findViewById(R.id.update) as TextView).text = errorText
                                    }
                                }
                            }
                        }, Response.ErrorListener { volleyError ->
                    runOnUiThread {
                        findViewById(R.id.progress_bar).visibility = View.GONE
                        (findViewById(R.id.update) as TextView).text = volleyError.localizedMessage
                    }
                })

                if (remember!!.isChecked) {
                    ActivityUtils.setCredentialBoolean(this, REMEMBER_KEY, true)
                    ActivityUtils.saveUserCredential(this, USERNAME_KEY, userField!!.text.toString())
                    ActivityUtils.saveUserCredential(this, PASSWORED_KEY, passField!!.text.toString())
                }
            }
            R.id.create_account_button -> startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }

    companion object {
        private val USERNAME_KEY = "username"
        private val PASSWORED_KEY = "password"
        private val REMEMBER_KEY = "remember"
    }

}
