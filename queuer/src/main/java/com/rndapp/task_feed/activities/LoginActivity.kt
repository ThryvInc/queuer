package com.rndapp.task_feed.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Response
import com.google.gson.Gson
import com.rndapp.task_feed.R
import com.rndapp.task_feed.api.LoginRequest
import com.rndapp.task_feed.api.VolleyManager
import com.rndapp.task_feed.managers.SessionManager
import com.rndapp.task_feed.models.SignInModel
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

    internal var errored: Boolean = false
    internal var errorText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sp = getSharedPreferences(SessionManager.API_KEY_PREFERENCE, Activity.MODE_PRIVATE)
        SessionManager.preferences = sp

        val apiKey = SessionManager.getApiKey()
        if (!TextUtils.isEmpty(apiKey)) {
            startActivity(Intent(this, SprintsActivity::class.java))
            finish()
        }

        userField = findViewById(R.id.username_field) as EditText
        passField = findViewById(R.id.password_field) as EditText
        remember = findViewById(R.id.remember_me_checkbox) as CheckBox

        val savedUsername = SessionManager.getUserCredential(this, USERNAME_KEY, "-1")
        val savedPassword = SessionManager.getUserCredential(this, PASSWORED_KEY, "-1")
        val saveCreds = SessionManager.getCredentialBoolean(this, REMEMBER_KEY, false)

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
                val signInModel = SignInModel()
                signInModel.username = userField!!.text.toString()
                signInModel.password = passField!!.text.toString()
                val request = LoginRequest(signInModel,
                        object : Response.Listener<JSONObject> {

                            override fun onResponse(jsob: JSONObject) {
                                handleApiKey(jsob)
                                handleLogin(jsob)
                            }
                        }, Response.ErrorListener { volleyError ->
                    runOnUiThread {
                        findViewById(R.id.progress_bar).visibility = View.GONE
                        (findViewById(R.id.update) as TextView).text = volleyError.localizedMessage
                    }
                })
                VolleyManager.queue?.add(request)

                if (remember!!.isChecked) {
                    SessionManager.setCredentialBoolean(this, REMEMBER_KEY, true)
                    SessionManager.saveUserCredential(this, USERNAME_KEY, userField!!.text.toString())
                    SessionManager.saveUserCredential(this, PASSWORED_KEY, passField!!.text.toString())
                }
            }
            R.id.create_account_button -> startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }

    fun handleApiKey(jsob: JSONObject) {
        try {
            if (jsob.has("api_key") && jsob.getString("api_key") != null) {
                val apiKey = jsob.getString("api_key")
                SessionManager.saveApiKey(this@LoginActivity, apiKey)
                //create user using Gson
                user = Gson().fromJson<User>(jsob.toString(), User::class.java!!)
                SessionManager.saveUserId(this@LoginActivity, user!!.id)
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
    }

    fun handleLogin(jsob: JSONObject) {
        runOnUiThread {
            findViewById(R.id.progress_bar).visibility = View.GONE
            //go to the next activity
            if (!errored && user != null) {
                this@LoginActivity
                        .startActivity(Intent(this@LoginActivity, SprintsActivity::class.java))
                this@LoginActivity.finish()
            } else {
                (findViewById(R.id.update) as TextView).text = errorText
            }
        }
    }

    companion object {
        private val USERNAME_KEY = "username"
        private val PASSWORED_KEY = "password"
        private val REMEMBER_KEY = "remember"
    }

}
