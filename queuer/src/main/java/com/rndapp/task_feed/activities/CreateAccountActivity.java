package com.rndapp.task_feed.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.rndapp.task_feed.QueuerApplication;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.models.ActivityUtils;
import com.rndapp.task_feed.models.User;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 7/13/13
 * Time: 2:28 PM
 */
public class CreateAccountActivity extends ActionBarActivity implements View.OnClickListener{
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        findViewById(R.id.create_account_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        EditText userField = (EditText)findViewById(R.id.username_field);
        EditText passField = (EditText)findViewById(R.id.password_field);
        if (!userField.getText().toString().equals("") && !passField.getText().toString().equals("")){
            findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
            ServerCommunicator.createAccount(this, ((QueuerApplication) getApplication()).getRequestQueue(),
                    userField.getText().toString(), passField.getText().toString(),
                    new Response.Listener<JSONObject>() {
                        boolean errored;
                        String errorText;

                        @Override
                        public void onResponse(JSONObject jsob) {
                            try {
                                Log.d("Received from /sessions", jsob.toString());
                                if (jsob.has("api_key") && jsob.getString("api_key") != null) {
                                    String apiKey = jsob.getString("api_key");
                                    ActivityUtils.saveApiKey(CreateAccountActivity.this, apiKey);
                                    //create user using Gson
                                    user = new Gson().fromJson(jsob.toString(), User.class);
                                    ActivityUtils.saveUserId(CreateAccountActivity.this, user.getId());
                                } else if (jsob.has("errors")) {
                                    //error
                                    errored = true;
                                    errorText = jsob.getString("errors");
                                } else {
                                    //error
                                    errored = true;
                                    errorText = "Unknown Error";
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                //error
                                errored = true;
                                errorText = e.getLocalizedMessage();
                            }
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.progress_bar).setVisibility(View.GONE);
                                    //go to the next activity
                                    if (!errored && user != null) {
                                        CreateAccountActivity.this
                                                .startActivity(new Intent(CreateAccountActivity.this, FeedActivity.class));
                                        CreateAccountActivity.this.finish();
                                    } else {
                                        ((TextView) findViewById(R.id.update)).setText(errorText);
                                    }
                                }
                            });
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(final VolleyError volleyError) {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.progress_bar).setVisibility(View.GONE);
                                    ((TextView) findViewById(R.id.update)).setText(volleyError.getLocalizedMessage());
                                }
                            });
                        }
                    });
        }
    }
}
