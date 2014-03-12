package com.rndapp.task_feed.testing;

import android.content.Context;
import android.test.AndroidTestCase;

import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.data.ProjectDataSource;
import com.rndapp.task_feed.models.ActivityUtils;
import com.rndapp.task_feed.models.Project;
import com.rndapp.task_feed.models.Task;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by mammothbane on 3/11/14.
 */
public class ProjectTest extends AndroidTestCase {
    public final static String tag = "AndroidTestCase";
    private Context context;
    private Task task;
    private Project project;
    private static final String testString = "mytask";
    private static final int testInt = -50;

    public void setUp() throws Exception {
        task = new Task();
        project = new Project();
        context = getContext();
        super.setUp();
    }

    public void tearDown() throws Exception {
        task = null;
        context = null;
        project = null;
        super.tearDown();
    }

    public void testToString() throws Exception {

    }

    public void testGetFirstTaskText() throws Exception {

    }

    public void testAddTaskToBeginning() throws Exception {

    }

    public void testAddTaskRespectingOrder() throws Exception {

    }

    public void testRemoveFirstTask() throws Exception {

    }

    public void testSortTasks() throws Exception {

    }

    public void testGetTask() throws Exception {
        ArrayList<Task> tasks = new ArrayList<Task>();
        task.setId(testInt);
        tasks.add(task);
        project.setTasks(tasks);
        if (!project.getTask(0).equals(task)) throw new Exception();
    }

    public void testIsEmpty() throws Exception {
        project.setTasks(new ArrayList<Task>());
        project.setHidden(false);
        if (!project.isEmpty()) throw new Exception("Unexpected value for isEmpty");
        ArrayList<Task> temp = new ArrayList<Task>();
        temp.add(task);
        project.setTasks(temp);
        if (project.isEmpty()) throw new Exception("Unexpected value for isEmpty");
        project.setHidden(true);
        if (!project.isEmpty()) throw new Exception("Unexpected value for isEmpty");
        project.setTasks(new ArrayList<Task>());
        if (!project.isEmpty()) throw new Exception("Unexpected value for isEmpty");
    }

    public void testIsUpToDateWithServerProject() throws Exception {
        project.setUpdated_at(new Date(1));
        Project project1 = new Project();
        project1.setUpdated_at(new Date(2));
        if (project.isUpToDateWithServerProject(project1)) throw new
                Exception("project should only be considered up to date if it was updated *after* the server project");
    }

    public void testAddProjectToDatabase() throws Exception {
        project.setName(testString);
        project.setId(testInt);
        project.setLocalId(testInt);
        project.setColor(testInt);
        project.setHidden(true);
        project.setCreated_at(new Date(1));
        project.setUpdated_at(new Date(1));
        Project.addProjectToDatabase(context, project);
        ProjectDataSource pds = new ProjectDataSource(context);
        ArrayList<Project> projects = ActivityUtils.loadProjectsFromDatabase(context);
        pds.open();
        pds.deleteProject(project);
        pds.close();
        if (!projects.get(projects.size()-1).equals(project)) throw new Exception("adding project to database unsuccessful");

    }


    public void testEquals() throws Exception {
        if (!project.equals(project)) throw new Exception("Test project doesn't equal itself.");
        Project project2 = new Project();
        project2.setId(5); project.setId(3);
        if (project.equals(project2)) throw new Exception("Test projects shouldn't be equal");
        project2.setId(3);
        if (!project.equals(project2)) throw new Exception("Test projects should be equal");
    }

    public void testHashCode() throws Exception {
        project.setId(1);
        project.setLocalId(2);
        if (project.hashCode() != 33) throw new Exception(
                "Hashcode doesn't return the expected value"
        );
    }

    public void testHidden() throws Exception {
        project.setHidden(true);
        if (!project.isHidden()) throw new Exception("Project isn't hidden as expected");
    }

    public void testTasks() throws Exception {
        ArrayList<Task> tasks = new ArrayList<Task>();
        task.setId(testInt);
        tasks.add(task);
        project.setTasks(tasks);
        if (!project.getTasks().equals(tasks)) throw new Exception("" +
                "Issue setting or retrieving task list from project");

    }

    public void testCreated_at() throws Exception {
        project.setCreated_at(new Date(1));
        if (!project.getCreated_at().equals(new Date(1))) throw new Exception(
                "project doesn't return the date it was set to have"
        );
    }

    public void testUpdated_at() throws Exception {
        project.setUpdated_at(new Date(1));
        if (!project.getUpdated_at().equals(new Date(1))) throw new Exception(
                "project doesn't return the date it was set to have"
        );
    }


    public void testColor() throws Exception {
        project.setColor(testInt);
        if (project.getColor() != testInt) throw new Exception(
                "project returns a different color than it was given"
        );
    }

    public void testName() throws Exception {
        project.setName(testString);
        if (!project.getName().equals(testString)) throw new Exception(
                "project returns a different name than it was given"
        );
    }

    public void testId() throws Exception {
        project.setId(testInt);
        if (project.getId() != testInt) throw new Exception(
                "project's id doesn't match the id it was given"
        );
    }

    public void testLocalId() throws Exception {
        project.setLocalId(testInt);
        if (project.getLocalId() != testInt) throw new Exception(
                "project's localId doesn't match the localId it was given"
        );
    }

}
