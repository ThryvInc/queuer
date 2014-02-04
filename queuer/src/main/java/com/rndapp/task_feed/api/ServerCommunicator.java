package com.rndapp.task_feed.api;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.rndapp.task_feed.models.ActivityUtils;
import com.rndapp.task_feed.models.NewUserModel;
import com.rndapp.task_feed.models.Project;
import com.rndapp.task_feed.models.SignInModel;
import com.rndapp.task_feed.models.Task;

public class ServerCommunicator {
	private Context context;
    public static final String USER_CREDENTIALS_PREF = "com.rndapp.queuer.user_creds";
    public static final String USER_ID_PREF = "com.rndapp.queuer.user_id_pref";

    public static final String BASE_URL = "http://queuer-rndapp.rhcloud.com/api/v1/"; //192.168.11.152:3000
    public static final String API_KEY_PREFERENCE = "com.rndapp.queuer.api_key_pref";
    public static final String API_KEY_HEADER = "X-Qer-Authorization";

    public static void createAccount(Context context,
                             RequestQueue queue,
                             String username, String password,
                             Response.Listener listener,
                             Response.ErrorListener errorListener){
        //get user details
        SignInModel signInModel = new SignInModel();
        signInModel.setUsername(username);
        signInModel.setPassword(password);

        JSONObject newUser = null;
        try {
            newUser = new JSONObject(new Gson().toJson(new NewUserModel(signInModel), NewUserModel.class));
            postToEndpointUnauthed("users", newUser, queue, listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void login(Context context,
                             RequestQueue queue,
                             String username, String password,
                             Response.Listener listener,
                             Response.ErrorListener errorListener){
        //get user details
        SignInModel signInModel = new SignInModel();
        signInModel.setUsername(username);
        signInModel.setPassword(password);

        JSONObject signInJson = null;
        try {
            signInJson = new JSONObject(new Gson().toJson(signInModel));
            postToEndpointUnauthed("session", signInJson, queue, listener, errorListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void downloadProjectsFromServer(Context context,
                                                  RequestQueue queue,
                                                  Response.Listener listener,
                                                  Response.ErrorListener errorListener){

        final SharedPreferences sp = context.getSharedPreferences(USER_ID_PREF, Activity.MODE_PRIVATE);
        getEndpointAuthed("users/" + sp.getInt("user_id", 0) + "/projects",
                context,
                queue,
                listener,
                errorListener);
    }

    public static void uploadProjectToServer(Context context, Project project,
                                             RequestQueue queue,
                                             Response.Listener listener,
                                             Response.ErrorListener errorListener){
        HashMap<String, Object> hash = new HashMap<String, Object>();
        hash.put("project",project);
        Project newProject = new Project();
        newProject.setColor(project.getColor());
        newProject.setName(project.getName());
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(hash));
            SharedPreferences sp = context.getSharedPreferences(ActivityUtils.USER_ID_PREF, Activity.MODE_PRIVATE);
            postToEndpointAuthed("users/"+sp.getInt("user_id", 0)+"/projects",jsonObject,
                    context,queue,listener,errorListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void updateProjectOnServer(Context context, Project project,
                                                RequestQueue queue,
                                                Response.Listener listener,
                                                Response.ErrorListener errorListener){
        HashMap<String, Object> hash = new HashMap<String, Object>();
        hash.put("project",project);
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(hash));
            SharedPreferences sp = context.getSharedPreferences(ActivityUtils.USER_ID_PREF, Activity.MODE_PRIVATE);
            putToEndpointAuthed("users/" + sp.getInt("user_id", 0) + "/projects/" + project.getId(),
                    jsonObject,
                    context,
                    queue,
                    listener,
                    errorListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void uploadTaskToServer(Context context,
                                          Task task,
                                          RequestQueue queue,
                                          Response.Listener listener,
                                          Response.ErrorListener errorListener){
        HashMap<String, Object> hash = new HashMap<String, Object>();
        hash.put("task",task);
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(hash));
            SharedPreferences sp = context.getSharedPreferences(ActivityUtils.USER_ID_PREF, Activity.MODE_PRIVATE);
            postToEndpointAuthed("users/"+sp.getInt("user_id", 0)+"/projects/"+task.getProject_id()+"/tasks",
                    jsonObject,
                    context,
                    queue,
                    listener,
                    errorListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void updateTask(Context context,
                           RequestQueue queue,
                           Task task,
                           Response.Listener listener,
                           Response.ErrorListener errorListener){
        HashMap<String, Object> hash = new HashMap<String, Object>();
        hash.put("task",task);
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(hash));

            SharedPreferences sp = context.getSharedPreferences(ActivityUtils.USER_ID_PREF, Activity.MODE_PRIVATE);
            putToEndpointAuthed("users/" + sp.getInt("user_id", 0) + "/projects/" + task.getProject_id() + "/tasks/" + task.getId(),
                    jsonObject, context, queue, listener, errorListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void postToEndpointAuthed(String endpoint,
                                       JSONObject postData,
                                       Context context,
                                       RequestQueue queue,
                                       Response.Listener listener,
                                       Response.ErrorListener errorListener){

        final SharedPreferences sp = context.getSharedPreferences(API_KEY_PREFERENCE, Activity.MODE_PRIVATE);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                BASE_URL + endpoint,
                postData,
                listener,
                errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(API_KEY_HEADER, sp.getString("api_key", ""));
                params.put("Content-type", "application/json; charset=utf-8");
                return params;
            }
        };
        queue.add(request);
    }

    private static void putToEndpointAuthed(String endpoint,
                                      JSONObject postData,
                                      Context context,
                                      RequestQueue queue,
                                      Response.Listener listener,
                                      Response.ErrorListener errorListener){

        final SharedPreferences sp = context.getSharedPreferences(API_KEY_PREFERENCE, Activity.MODE_PRIVATE);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT,
                BASE_URL + endpoint,
                postData,
                listener,
                errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(API_KEY_HEADER, sp.getString("api_key", ""));
                params.put("Content-type", "application/json; charset=utf-8");
                return params;
            }
        };
        queue.add(request);
    }
	
	private static void postToEndpointUnauthed(String endpoint,
                                         JSONObject postData,
                                         RequestQueue queue,
                                         Response.Listener listener,
                                         Response.ErrorListener errorListener){
        Log.d("postData", postData.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                BASE_URL + endpoint,
                postData,
                listener,
                errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Content-type", "application/json; charset=utf-8");
                return params;
            }
        };
        queue.add(request);
	}
	
	private static void getEndpointUnauthed(String endpoint,
                                      RequestQueue queue,
                                      Response.Listener listener,
                                      Response.ErrorListener errorListener){
        Request request = new JsonObjectRequest(Request.Method.GET,
                BASE_URL + endpoint,
                null,
                listener,
                errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Content-type", "application/json; charset=utf-8");
                return params;
            }
        };
        queue.add(request);
	}
	
	private static void getEndpointAuthed(String endpoint,
                                    Context context,
                                    RequestQueue queue,
                                    Response.Listener listener,
                                    Response.ErrorListener errorListener){

        SharedPreferences sp = context.getSharedPreferences(API_KEY_PREFERENCE, Activity.MODE_PRIVATE);
        final String apiKey = sp.getString("api_key", "");
        Request request = new JsonArrayRequest(BASE_URL + endpoint,
                listener,
                errorListener){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(API_KEY_HEADER, apiKey);
                params.put("Content-type", "application/json; charset=utf-8");
                return params;
            }
        };
        queue.add(request);
	}

}
