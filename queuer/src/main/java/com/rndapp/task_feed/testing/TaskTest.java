package com.rndapp.task_feed.testing;

import android.content.Context;
import android.test.AndroidTestCase;

import com.rndapp.task_feed.models.Task;

import junit.framework.TestCase;

import java.util.Date;

/**
 * Created by mammothbane on 3/11/14.
 */
public class TaskTest extends AndroidTestCase {
    public final static String tag = "AndroidTestCase";
    private Context context;
    private Task task;
    private static final String testString = "mytask";
    private static final int testInt = -50;

    @Override
    public void setUp() throws Exception {
        task = new Task();
        context = getContext();
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        task = null;
        context = null;
        super.tearDown();
    }

    public void testIsUpToDateWithServerTask() throws Exception {
        task.setUpdated_at(new Date(1));
        Task task1 = new Task();
        task1.setUpdated_at(new Date(2));
        if (task.isUpToDateWithServerTask(task1)) throw new
                Exception("task should only be considered up to date if it was updated *after* the server task");
    }

    public void testEquals() throws Exception {
        if (!task.equals(task)) throw new Exception("Test task doesn't equal itself.");
        Task task2 = new Task();
        task2.setId(5); task.setId(3);
        task.setProject_id(10); task2.setProject_id(13);
        if (task.equals(task2)) throw new Exception("Test tasks shouldn't be equal");
        task2.setId(3);
        task2.setProject_id(10);
        if (!task.equals(task2)) throw new Exception("Test tasks should be equal");
    }

    public void testHashCode() throws Exception {
        task.setId(1);
        task.setLocalId(2);
        task.setProject_id(3);
        task.setName(testString);
        if (task.hashCode() !=
                ((task.getId()*31+task.getLocalId())*31+task.getProject_id())*31+testString.hashCode())
                throw new Exception("Hashcode doesn't return the expected value.");

    }

    public void testCreated_at() throws Exception {
        task.setCreated_at(new Date(1));
        if (!task.getCreated_at().equals(new Date(1))) throw new Exception(
                "task doesn't return the date it was set to have"
        );
    }

    public void testUpdated_at() throws Exception {
        task.setUpdated_at(new Date(1));
        if (!task.getUpdated_at().equals(new Date(1))) throw new Exception(
                "task doesn't return the date it was set to have"
        );
    }

    public void testName() throws Exception {
        task.setName(testString);
        if (!task.getName().equals(testString)) throw new Exception(
                "task returns a different name than it was given"
        );
    }

    public void testId() throws Exception {
        task.setId(testInt);
        if (task.getId() != testInt) throw new Exception(
                "task's id doesn't match the id it was given"
        );
    }

    public void testLocalId() throws Exception {
        task.setLocalId(testInt);
        if (task.getLocalId() != testInt) throw new Exception(
                "task's localId doesn't match the localId it was given"
        );
    }

    public void testProjectId() throws Exception {
        task.setProject_id(testInt);
        if (task.getProject_id() != testInt) throw new Exception(
                "task's project id doesn't match the project id it was given"
        );
    }
    public void testFinished() throws Exception {
        task.setFinished(true);
        if (!task.isFinished()) throw new Exception(
                "task's finished status doesn't match the status it was assigned"
        );
    }

    public void testPoints() throws Exception {
        task.setPoints(testInt);
        if (task.getPoints() != testInt) throw new Exception(
                "task's points doesn't match the points it was assigned"
        );
    }


    public void testOrder() throws Exception {
        task.setOrder(testInt);
        if (task.getOrder() != testInt) throw new Exception(
                "task's order doesn't match the order it was assigned"
        );
    }

    public void testCompare() throws Exception {
        Task task1 = new Task();
        task1.setOrder(1);
        task.setOrder(0);
        if (task.compareTo(task1) != -1) throw new Exception(
                "task doesn't compare correctly"
        );
    }

    public void testToString() throws Exception {
        task.setName(testString);
        if (!task.toString().equals(testString)) throw new Exception("" +
                "task name set or toString doesn't function properly");
    }

}
