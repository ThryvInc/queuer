package com.rndapp.task_feed.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rndapp.task_feed.activities.LoginActivity;
import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.data.ProjectDataSource;
import com.rndapp.task_feed.data.TaskDataSource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    public static ArrayList<Project> loadProjectsFromDatabase(Context context){
        //load projects
        ProjectDataSource projectDataSource = new ProjectDataSource(context);
        projectDataSource.open();
        ArrayList<Project> projects = projectDataSource.getAllProjects();
        projectDataSource.close();

        //load tasks
        TaskDataSource taskDataSource = new TaskDataSource(context);
        taskDataSource.open();
        ArrayList<Task> tasks = taskDataSource.getAllTasks();
        taskDataSource.close();

        //associate
        for (Project project : projects){
            for (Task task : tasks){
                if (task.getProject_id() == project.getId()){
                    project.getTasks().add(task);
                }
            }
        }

        //sort
        for (final Project project : projects){
            project.sortTasks();
        }

        return projects;
    }

    public static void downloadProjectsFromServer(final Context context,
                                                                RequestQueue queue,
                                                                Response.Listener<JSONArray> listener,
                                                                Response.ErrorListener errorListener){
        ServerCommunicator.downloadProjectsFromServer(context, queue, listener, errorListener);
    }

    public static ArrayList<Project> syncProjectsWithServer(Context context,
                                                            RequestQueue queue,
                                                             ArrayList<Project> projects,
                                                             ArrayList<Project> serverProjects){
        for (Project project : projects){
            boolean isOnServer = false;
            if (serverProjects != null){
                for (Project serverProject : serverProjects){
                    if (project.equals(serverProject)) {
                        isOnServer = true;
                    }
                }
            }
            if (!isOnServer) {
                final Project newProject = project;
                Project.uploadProjectToServer(context, queue, project, new Response.Listener() {
                            @Override
                            public void onResponse(Object o) {
                                //upload tasks
                                for (Task task : newProject.getTasks()){
                                    task.setProject_id(newProject.getId());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                            }
                        });
            }
        }

        if (serverProjects != null){
            for (Project serverProject : serverProjects) {
                boolean isInDatabase = false;
                int indexOfProject = 0;
                Project syncedProject = null;
                for (Project project : projects){
                    if (project.equals(serverProject)){
                        isInDatabase = true;
                        indexOfProject = projects.indexOf(project);
                        syncedProject = new syncProjectsTask().doInBackground(context, queue, project, serverProject);
                    }
                }
                if (!isInDatabase) {
                    Project project = Project.addProjectToDatabase(context, serverProject);
                    for (Task task : serverProject.getTasks()){
                        project.addTaskRespectingOrder(context, task);
                    }
                    projects.add(project);
                }else if (syncedProject != null){
                    projects.set(indexOfProject, syncedProject);
                }
            }
        }

        return projects;
    }

    private static class syncProjectsTask extends AsyncTask <Object, Integer, Project> {
        protected Project doInBackground(Object... params) {
            return syncProjects(((Context)params[0]), (RequestQueue)params[1], (Project)params[2], (Project)params[3]);
            //Toast.makeText((Context)params[0], "you win, it's working.", Toast.LENGTH_SHORT).show();
            //return project;
        }

    }

    private static Project syncProjects(Context context, RequestQueue queue, Project localProject, Project remoteProject) {
        Project syncedProject = null;
        if (remoteProject == null){
            return localProject;
        }else {
            syncedProject =
                    localProject.getUpdated_at().before(remoteProject.getUpdated_at()) ? remoteProject : localProject;
        }
        if (syncedProject.getName().equals("Tonight")){
            Log.d("Sync Project", syncedProject.getName());
        }
        syncedProject.setTasks(syncTasks(context, queue, localProject.getTasks(), remoteProject.getTasks()));
        return syncedProject;
    }

    private static ArrayList<Task> syncTasks(Context context, RequestQueue queue, ArrayList<Task> localTasks, ArrayList<Task> remoteTasks){
        final ArrayList<Task> syncedTasks = new ArrayList<Task>();
        for (Task task : localTasks){
            boolean isOnServer = false;
            for (Task serverTask : remoteTasks){
                if (serverTask.equals(task)){
                    isOnServer = true;
                    if (task.getUpdated_at() == null || task.getUpdated_at().before(serverTask.getUpdated_at())){
                        //take the server version
                        Task.updateTask(context, serverTask);
                        syncedTasks.add(serverTask);
                    }
                }
            }
            if (!isOnServer){
                Task.uploadTaskToServer(context, queue, task, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject o) {
                                syncedTasks.add(new Gson().fromJson(o.toString(), Task.class));
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                            }
                        }
                );
            }
        }
        return syncedTasks;
    }

    public static void logout(Activity activity){
        saveApiKey(activity, "");
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }
}
