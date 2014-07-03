package com.rndapp.task_feed.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
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
 * Time: 2:12 PM
 */
public class LoginActivity extends Activity implements View.OnClickListener{
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORED_KEY = "password";
    private static final String REMEMBER_KEY = "remember";

    private User user;

    EditText userField;
    EditText passField;
    CheckBox remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sp = getSharedPreferences(ServerCommunicator.API_KEY_PREFERENCE, Activity.MODE_PRIVATE);
        String apiKey = sp.getString("api_key", "");
        if (!apiKey.equals("")){
            startActivity(new Intent(this, FeedActivity.class));
            finish();
        }

        userField = (EditText)findViewById(R.id.username_field);
        passField = (EditText)findViewById(R.id.password_field);
        remember = (CheckBox)findViewById(R.id.remember_me_checkbox);

        String savedUsername = ActivityUtils.getUserCredential(this, USERNAME_KEY, "-1");
        String savedPassword = ActivityUtils.getUserCredential(this, PASSWORED_KEY, "-1");
        boolean saveCreds = ActivityUtils.getCredentialBoolean(this, REMEMBER_KEY, false);

        if (saveCreds){
            userField.setText(savedUsername);
            passField.setText(savedPassword);
        }

        remember.setChecked(saveCreds);

        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.create_account_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_button:
                if (!userField.getText().toString().equals("") && !passField.getText().toString().equals("")){
                    findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                    ServerCommunicator.login(this, ((QueuerApplication) getApplication()).getRequestQueue(),
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
                                            ActivityUtils.saveApiKey(LoginActivity.this, apiKey);
                                            //create user using Gson
                                            user = new Gson().fromJson(jsob.toString(), User.class);
                                            ActivityUtils.saveUserId(LoginActivity.this, user.getId());
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
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            findViewById(R.id.progress_bar).setVisibility(View.GONE);
                                            //go to the next activity
                                            if (!errored && user != null) {
                                                LoginActivity.this
                                                        .startActivity(new Intent(LoginActivity.this, FeedActivity.class));
                                                LoginActivity.this.finish();
                                            } else {
                                                ((TextView) findViewById(R.id.update)).setText(errorText);
                                            }
                                        }
                                    });
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(final VolleyError volleyError) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            findViewById(R.id.progress_bar).setVisibility(View.GONE);
                                            ((TextView) findViewById(R.id.update)).setText(volleyError.getLocalizedMessage());
                                        }
                                    });

                                }
                            });

                    if (remember.isChecked()){
                        ActivityUtils.setCredentialBoolean(this, REMEMBER_KEY, true);
                        ActivityUtils.saveUserCredential(this, USERNAME_KEY, userField.getText().toString());
                        ActivityUtils.saveUserCredential(this, PASSWORED_KEY, passField.getText().toString());
                    }
                }
                break;
            case R.id.create_account_button:
                startActivity(new Intent(this, CreateAccountActivity.class));
                break;
        }
    }

}
