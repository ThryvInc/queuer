package com.rndapp.task_feed.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.rndapp.task_feed.activities.LoginActivity;
import com.rndapp.task_feed.api.ServerCommunicator;

import org.json.JSONArray;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 7/14/13
 * Time: 10:56 AM
 */
public class ActivityUtils {
    public static final String USER_CREDENTIALS_PREF = "com.rndapp.queuer.user_creds";
    public static final String USER_ID_PREF = "com.rndapp.queuer.user_id_pref";

    public static void saveApiKey(Context context, String apiKey){
        SharedPreferences sp = context.getSharedPreferences(ServerCommunicator.API_KEY_PREFERENCE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editPrefs = sp.edit();
        //store api key
        editPrefs.putString("api_key", apiKey);
        editPrefs.commit();
    }

    public static void saveUserId(Context context, int userId){
        SharedPreferences sp = context.getSharedPreferences(USER_ID_PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editPrefs = sp.edit();
        //store api key
        editPrefs.putInt("user_id", userId);
        editPrefs.commit();
    }

    public static void saveUserCredential(Context context, String credKey, String credential){
        SharedPreferences sp = context.getSharedPreferences(USER_CREDENTIALS_PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editPrefs = sp.edit();
        //store api key
        editPrefs.putString(credKey, credential);
        editPrefs.commit();
    }

    public static String getUserCredential(Context context, String credKey, String credential){
        return context.getSharedPreferences(USER_CREDENTIALS_PREF, Activity.MODE_PRIVATE)
                .getString(credKey, credential);
    }

    public static void setCredentialBoolean(Context context, String credKey, boolean cred){
        SharedPreferences sp = context.getSharedPreferences(USER_CREDENTIALS_PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editPrefs = sp.edit();
        //store api key
        editPrefs.putBoolean(credKey, cred);
        editPrefs.commit();
    }

    public static boolean getCredentialBoolean(Context context, String credKey, boolean cred){
        return context.getSharedPreferences(USER_CREDENTIALS_PREF, Activity.MODE_PRIVATE)
                .getBoolean(credKey, cred);
    }

    public static void downloadProjectsFromServer(final Context context,
                                                                RequestQueue queue,
                                                                Response.Listener<JSONArray> listener,
                                                                Response.ErrorListener errorListener){
        ServerCommunicator.downloadProjectsFromServer(context, queue, listener, errorListener);
    }

    public static void logout(Activity activity){
        saveApiKey(activity, "");
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }
}
