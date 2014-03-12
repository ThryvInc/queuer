package com.rndapp.task_feed.testing;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import android.util.Log;

import com.rndapp.task_feed.QueuerApplication;
import com.rndapp.task_feed.activities.LoginActivity;
import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.data.ProjectDataSource;
import com.rndapp.task_feed.data.TaskDataSource;
import com.rndapp.task_feed.models.ActivityUtils;
import com.rndapp.task_feed.models.Project;
import com.rndapp.task_feed.models.Task;

import junit.framework.TestCase;

import org.jetbrains.annotations.Nullable;

import java.lang.Exception;
import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;


/**
 * Created by mammothbane on 3/10/14.
 */

public class ActivityUtilsTest extends AndroidTestCase {
    public final static String tag = "ActivityUtilsTest";
    private final static String testKey = "mytestapikey";
    private final static int testInt = 0;
    private final static String testCredKey = "mytestcredkey";
    private final static String testCred = "myusername";
    private final static boolean testBool = true;
    private final static int numProjects = 5;
    private Context context;

    @Override
    public void setUp() throws Exception {
        context = getContext();
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        context = null;
        super.tearDown();
    }

    public void testSaveApiKey() throws Exception {
        ActivityUtils.saveApiKey(context, testKey);
        String ret = context.getSharedPreferences(ServerCommunicator.API_KEY_PREFERENCE, Context.MODE_PRIVATE)
                .getString("api_key",null);
        if (!ret.equals(testKey))
            throw new Exception("saveApiKey returns " + ret + " instead of expected" + testKey);
    }

    public void testSaveUserId() throws Exception {
        ActivityUtils.saveUserId(context, testInt);
        int ret = context.getSharedPreferences(ActivityUtils.USER_ID_PREF, Context.MODE_PRIVATE).getInt("user_id",-1);
        if (ret != testInt)
            throw new Exception("saveUserId returns " + ret + "instead of expected " + testInt);
    }

    public void testUserCredential() throws Exception {
        ActivityUtils.saveUserCredential(context, testCredKey, testCred);
        String ret = ActivityUtils.getUserCredential(context, testCredKey, "failure");
        if (!(ret.equals(testCred)))
            throw new Exception("saveUserId returns " + ret + "instead of expected " + testCred);
    }

    public void testCredentialBoolean() throws Exception {
        ActivityUtils.setCredentialBoolean(context, testCredKey, testBool);
        boolean ret = !(ActivityUtils.getCredentialBoolean(context, testCredKey, false));
        if (ret)
            throw new Exception("saveUserId returns " + false + "instead of expected " + testBool);
    }

    public void testDeleteTask() throws Exception {
        TaskDataSource tds = new TaskDataSource(context);
        tds.open();
        Task task;
        tds.createTask("mytitle", 1, 1, 1, 0, false);
        ArrayList<Task> temp = tds.getAllTasks();
        task = temp.get(temp.size()-1);
        Log.d(tag, task.getName());
        tds.deleteTask(task);
        tds.close();
    }

    private ArrayList<Project> populateProjectList(Context context) {
        ArrayList<Project> projects = new ArrayList<Project>();
        String time_id;
        for (int i = 0; i < numProjects; i++) { //manually create new projects
            time_id = Long.toString(Calendar.getInstance(TimeZone.getDefault()).getTimeInMillis()/1000L);
            projects.add(new Project(("proj-test#token" + time_id),i));
            Project project = projects.get(i);
            project.setId(-(i + 10) * 1000); //pick a server id; negative, so we don't conflict
            project.setCreated_at(new Date(i)); //pick an unlikely date
            project.setUpdated_at(new Date(1000 * i)); //pick another unlikely date
            TaskDataSource tds = new TaskDataSource(context);
            tds.open();
            Task task = tds.createTask("task-test#token" + time_id, project.getId(), -i,
                    Integer.parseInt(time_id), 0, false);
                    //make tasks with positions based on when they were created; this forces them to be sorted based on creation-time
                    //i was running into issues sometimes ending up with several tasks on one project when it failed and several tasks left over in the database
            tds.close();
            ArrayList<Task> tasks = new ArrayList<Task>();
            tasks.add(task);
            project.setTasks(tasks);
        }

        ProjectDataSource pds = new ProjectDataSource(context);
        pds.open();
        for (Project project : projects) { //enter all of the projects into the database
            pds.createProject(project.getName(), project.getColor(), project.getId(), project.getCreated_at(), project.getUpdated_at());
        }
        pds.close();
        return projects;
    }

    public void testLoadProjects() throws Exception {
        ArrayList<Project> projects = populateProjectList(context); //create a project list and a bunch of database entries
        TaskDataSource tds = new TaskDataSource(context);
        ArrayList<Project> dbProjects = ActivityUtils.loadProjectsFromDatabase(context); //load a project list from the database
        for (Project project : dbProjects) { //clean out duplicate tasks, if they exist
            if (project.getTasks().size() > 1) {
                tds.open();
                for (int i = 1; i < project.getTasks().size(); i++) {
                    tds.deleteTask(project.getTask(i));
                }
                tds.close();
                ArrayList<Task> temp = new ArrayList<Task>();
                temp.add(project.getTask(0));
                project.setTasks(temp);
            }
        }
        String names1 = "", names2 = ""; //not the cleanest solution, but it ends up working
        for (Project project : projects) names1 += "\n\n" + project.getName() + ", " + project.getId() + "\ntask name " + project.getFirstTaskText();
        ProjectDataSource pds = new ProjectDataSource(context);
        pds.open();
        tds.open();
        for (int i = dbProjects.size()-numProjects; i < dbProjects.size(); i++) {
            Project project = dbProjects.get(i);
            names2 += "\n\n" + project.getName() + ", " + project.getId() + "\ntask name " + project.getFirstTaskText();
            tds.deleteTask(project.getTask(0));
            pds.deleteProject(project);
        }
        pds.close();
        tds.close();
        if (!names1.equals(names2))
            throw new Exception("Loaded list deviates from generated one. List of names of generated projects, " +
                    "followed by their local IDs: " + names1 + "\n" + "List of names of loaded projects, " +
                    "followed by their local IDs:" + names2);

        //couldn't use projectlist.equals(otherprojectlist) here, even though it would have been lovely.
        //the first list never gets assigned localIds.
    }

    public void testSyncProjectsWithServer() throws Exception {
        ArrayList<Project> projects = new ArrayList<Project>(), serverProjects = new ArrayList<Project>();
        projects.add(new Project());
        if (!(projects.equals(ActivityUtils.syncProjectsWithServer(context, null, projects, projects)))) throw
                new Exception("syncProjectsWithServer should return the project list it was given");
    }


    public void testSyncProjectsWithDatabase() throws Exception {
        ArrayList<Project> projects = new ArrayList<Project>();
        Project project = new Project("mytestproject", 1);
        project.setUpdated_at(new Date(1));
        projects.add(project);
        if (!(projects.equals(ActivityUtils.syncProjectsWithDatabase(context, null, projects, projects)))) throw
                new Exception("syncProjectsWithDatabase doesn't return an identical list of projects to the one that's passed in");
        ArrayList<Project> updatedProjects = projects;
        updatedProjects.get(0).setUpdated_at(new Date(2));
        if (!projects.equals(ActivityUtils.syncProjectsWithDatabase(context, null, projects, updatedProjects))) throw
            new Exception("syncProjectsWithDatabase doesn't return a correctly updated arraylist of tasks");
    }
}
