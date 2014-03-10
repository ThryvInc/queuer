package com.rndapp.task_feed.testing;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import android.util.Log;

import com.rndapp.task_feed.activities.LoginActivity;
import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.data.ProjectDataSource;
import com.rndapp.task_feed.models.ActivityUtils;
import com.rndapp.task_feed.models.Project;

import junit.framework.TestCase;

import org.jetbrains.annotations.Nullable;

import java.lang.Exception;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;


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
    Context context;

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

    private ArrayList<Project> populateProjectList(Context context) {
        ArrayList<Project> projects = new ArrayList<Project>();
        for (int i = 0; i < 5; i++) {
            projects.add(new Project(("proj" + i),i));
        }

        ProjectDataSource pds = new ProjectDataSource(context);
        pds.open();
        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            pds.createProject(project.getName(), project.getColor(), i, new Date(1000*i), new Date(10000*i));
        }
        pds.close();
        return projects;
    }

    public void testLoadProjects() throws Exception {
        ArrayList<Project> projects = populateProjectList(context);

    }

    public void testSyncProjectsWithDatabase() throws Exception {

    }
}
