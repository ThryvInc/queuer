package com.rndapp.task_feed.models;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rndapp.task_feed.activities.LoginActivity;
import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.data.ProjectDataSource;
import com.rndapp.task_feed.data.TaskDataSource;

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

    public static ArrayList<Project> downloadProjectsFromServer(Context context, ArrayList<Project> projects){
        ArrayList<Project> serverProjects;
        ServerCommunicator server = new ServerCommunicator(context);
        try {
            SharedPreferences sp = context.getSharedPreferences(ActivityUtils.USER_ID_PREF, Activity.MODE_PRIVATE);
            String json = server.getEndpointAuthed("users/" + sp.getInt("user_id", 0) + "/projects");
            Type listOfProjects = new TypeToken<List<Project>>(){}.getType();
            serverProjects = new Gson().fromJson(json, listOfProjects);

            projects = syncProjectsWithServer(context, projects, serverProjects);
        }catch (Exception e){
            e.printStackTrace();
        }
        return projects;
    }

    private static ArrayList<Project> syncProjectsWithServer(Context context,
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
            if (!isOnServer) Project.uploadProjectToServer(context, project);
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
                        syncedProject = syncProjects(context, project, serverProject);
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

    private static Project syncProjects(Context context, Project localProject, Project remoteProject){
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
        syncedProject.setTasks(syncTasks(context, localProject.getTasks(), remoteProject.getTasks()));
        return syncedProject;
    }

    private static ArrayList<Task> syncTasks(Context context, ArrayList<Task> localTasks, ArrayList<Task> remoteTasks){
        ArrayList<Task> syncedTasks = new ArrayList<Task>();
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
                syncedTasks.add(Task.uploadTaskToServer(context, task));
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
