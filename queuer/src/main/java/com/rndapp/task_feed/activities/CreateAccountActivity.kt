package com.rndapp.task_feed.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Response
import com.google.gson.Gson
import com.rndapp.task_feed.R
import com.rndapp.task_feed.managers.SessionManager
import com.rndapp.task_feed.models.User
import org.json.JSONObject

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 7/13/13
 * Time: 2:28 PM
 */
class CreateAccountActivity : AppCompatActivity(), View.OnClickListener {
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        findViewById(R.id.create_account_button).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val userField = findViewById(R.id.username_field) as EditText
        val passField = findViewById(R.id.password_field) as EditText
        if (userField.text.toString() != "" && passField.text.toString() != "") {
            findViewById(R.id.progress_bar).visibility = View.VISIBLE
            SessionManager.createAccount(userField.text.toString(), passField.text.toString(),
                    object : Response.Listener<JSONObject> {
                        internal var errored: Boolean = false
                        internal var errorText: String = ""

                        override fun onResponse(jsob: JSONObject) {
                            try {
                                if (jsob.has("api_key") && jsob.getString("api_key") != null) {
                                    val apiKey = jsob.getString("api_key")
                                    SessionManager.saveApiKey(this@CreateAccountActivity, apiKey)
                                    //create user using Gson
                                    user = Gson().fromJson<User>(jsob.toString(), User::class.java!!)
                                    SessionManager.saveUserId(this@CreateAccountActivity, user!!.id)
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

                            Handler().post {
                                findViewById(R.id.progress_bar).visibility = View.GONE
                                //go to the next activity
                                if (!errored && user != null) {
                                    this@CreateAccountActivity
                                            .startActivity(Intent(this@CreateAccountActivity, FeedActivity::class.java))
                                    this@CreateAccountActivity.finish()
                                } else {
                                    (findViewById(R.id.update) as TextView).text = errorText
                                }
                            }
                        }
                    }, Response.ErrorListener { volleyError ->
                Handler().post {
                    findViewById(R.id.progress_bar).visibility = View.GONE
                    (findViewById(R.id.update) as TextView).text = volleyError.localizedMessage
                }
            })
        }
    }
}
