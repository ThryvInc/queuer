package com.rndapp.task_feed.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.gson.Gson;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.models.ActivityUtils;
import com.rndapp.task_feed.models.NewUserModel;
import com.rndapp.task_feed.models.SignInModel;
import com.rndapp.task_feed.models.User;
import org.json.JSONException;
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
            new CreateAccountTask().execute(userField.getText().toString(), passField.getText().toString());
        }
    }

    private class CreateAccountTask extends AsyncTask<String, String, Object> {
        private boolean errored;
        private String errorText;

        /*
        * params[0] = username
        * params[1] = password
        */
        @Override
        protected Object doInBackground(String... params) {
            ServerCommunicator server = new ServerCommunicator(CreateAccountActivity.this);

            //get user details
            SignInModel signInModel = new SignInModel();
            signInModel.setUsername(params[0]);
            signInModel.setPassword(params[1]);

            JSONObject newUser = null;
            try {
                newUser = new JSONObject(new Gson().toJson(new NewUserModel(signInModel), NewUserModel.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String json = server.postToEndpointUnauthed("users", newUser);

            Log.d("JSON Received:", json);

            if (json != null && !json.equals("")){
                try{
                    JSONObject jsob = new JSONObject(json);
                    if (!jsob.getString("api_key").equals("null")){
                        String apiKey = jsob.getString("api_key");
                        ActivityUtils.saveApiKey(CreateAccountActivity.this, apiKey);
                        //create user using Gson
                        user = new Gson().fromJson(json, User.class);
                        ActivityUtils.saveUserId(CreateAccountActivity.this, user.getId());
                    }else if (jsob.has("errors")){
                        //error
                        errored = true;
                        errorText = jsob.getString("errors");
                    }else{
                        //error
                        errored = true;
                        errorText = "Unknown Error";
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    //error
                    errored = true;
                    errorText = e.getLocalizedMessage();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Object o){
            new Handler().post(new Runnable(){
                @Override
                public void run() {
                    findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    //go to the next activity
                    if (!errored && user != null){
                        CreateAccountActivity.this
                                .startActivity(new Intent(CreateAccountActivity.this, FeedActivity.class));
                    }else{
                        ((TextView)findViewById(R.id.update)).setText(errorText);
                    }
                }
            });
        }
    }
}
