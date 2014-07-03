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

    public static ArrayList<Project> syncProjectsWithServer(final Context context,
                                                            final RequestQueue queue,
                                                             ArrayList<Project> projects,
                                                             final ArrayList<Project> serverProjects) {
        for (Project project : projects) {
            boolean isOnServer = false;
            if (project.getId() != 0) isOnServer = true;
            if (!isOnServer) {
                final Project newProject = project;
                Project.uploadProjectToServer(context, queue, project, new Response.Listener() {
                    @Override
                    public void onResponse(Object o) {
                        //upload tasks
                        for (Task task : newProject.getTasks()) {
                            task.setProject_id(newProject.getId());
                        }
                    }
                }, null);
            }
        }
        return projects;
    }

    public static ArrayList<Project> syncProjectsWithDatabase(final Context context,
                                                              final RequestQueue queue,
                                                              ArrayList<Project> projects,
                                                              final ArrayList<Project> serverProjects) {

        if (serverProjects != null){
            for (Project serverProject : serverProjects) {
                boolean isInDatabase = false;
                int indexOfProject = 0;
                Project syncedProject = null;
                if (projects.contains(serverProject)){
                    isInDatabase = true;
                    indexOfProject = projects.indexOf(serverProject);
                    syncedProject = syncProjects(context, queue, projects.get(indexOfProject), serverProject);
                }
                if (!isInDatabase) {
                    Project project = Project.addProjectToDatabase(context, serverProject);
                    for (Task task : serverProject.getTasks()) {
                        project.addTaskRespectingOrder(context, task);
                    }
                    projects.add(project);
                } else if (syncedProject != null) {
                    projects.set(indexOfProject, syncedProject);
                }
            }
        }


        return projects;
    }

    private static Project syncProjects(Context context, RequestQueue queue, Project localProject, Project remoteProject) {
        Project syncedProject;
        if (remoteProject == null){
            return localProject;
        }else {
            syncedProject =
                    localProject.getUpdated_at().before(remoteProject.getUpdated_at()) ? remoteProject : localProject;
        }
        syncedProject.setTasks(syncTasks(context, queue, localProject.getTasks(), remoteProject.getTasks()));
        return syncedProject;
    }

    private static ArrayList<Task> syncTasks(Context context, RequestQueue queue, ArrayList<Task> localTasks, ArrayList<Task> remoteTasks){
        final ArrayList<Task> syncedTasks = new ArrayList<Task>();
        if (localTasks.size() < remoteTasks.size()){
            for (Task remoteTask : remoteTasks){
                if (!localTasks.contains(remoteTask)){
                    //add it
                    remoteTask = Task.addTaskToDatabase(context, remoteTask);
                }
                syncedTasks.add(remoteTask);
            }
        }else {
            for (Task task : localTasks){
                boolean isOnServer = task.getId() != 0;
                if (isOnServer){
                    //test which is more up to date
                    Task serverTask = remoteTasks.get(remoteTasks.indexOf(task));
                    if (!task.isUpToDateWithServerTask(serverTask)){
                        //then it must be outdated
                        //take the server version
                        Task.updateTask(context, serverTask);
                        syncedTasks.add(serverTask);
                    }else {
                        syncedTasks.add(task);
                    }
                }else {
                    //put it on the server
                    Task.uploadTaskToServer(context, queue, task, null, null);
                    syncedTasks.add(task);
                }
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
