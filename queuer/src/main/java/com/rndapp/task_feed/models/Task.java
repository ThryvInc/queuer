package com.rndapp.task_feed.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.async_tasks.UpdateTaskTask;
import com.rndapp.task_feed.data.TaskDataSource;
import com.rndapp.task_feed.interfaces.TaskDisplayer;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 1:32 PM
 *
 */
public class Task implements Serializable, Comparable<Task>{
    private int id;
    private int localId;
    private int project_id;
    private String name;
    private boolean finished = false;
    private int points = 1;
    private int order;
    private Date created_at;
    private Date updated_at;

    public static void updateTask(Context context, Task task){
        TaskDataSource source = new TaskDataSource(context);
        source.open();
        source.updateTask(task);
        source.close();
    }

    @Override
    public int compareTo(Task task){
        return this.order - task.order;
    }

    public static Task uploadTaskToServer(Context context, Task task){
        ServerCommunicator server = new ServerCommunicator(context);
        HashMap<String, Object> hash = new HashMap<String, Object>();
        hash.put("task",task);
        Task newTask = new Task();
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(hash));
            SharedPreferences sp = context.getSharedPreferences(ActivityUtils.USER_ID_PREF, Activity.MODE_PRIVATE);
            String json = server.postToEndpointAuthed("users/"+sp.getInt("user_id", 0)+"/projects/"+task.project_id+"/tasks",jsonObject);
            newTask = new Gson().fromJson(json, Task.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return newTask;
    }

    public static Task markAsFinished(Context context, Task task){
        task.setFinished(true);
        new UpdateTaskTask(context, null).execute(task);
        updateTask(context, task);
        return task;
    }

    public static Task updateTaskOnServer(Context context, Task task){
        ServerCommunicator server = new ServerCommunicator(context);
        HashMap<String, Object> hash = new HashMap<String, Object>();
        hash.put("task",task);
        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(hash));
            SharedPreferences sp = context.getSharedPreferences(ActivityUtils.USER_ID_PREF, Activity.MODE_PRIVATE);
            server.putToEndpointAuthed(
                    "users/" + sp.getInt("user_id", 0) + "/projects/" + task.project_id + "/tasks/" + task.getId(),
                    jsonObject);
        }catch (Exception e){
            e.printStackTrace();
        }
        Task.updateTask(context, task);
        return task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (project_id != task.project_id) return false;
        if (id != task.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + localId;
        result = 31 * result + project_id;
        result = 31 * result + name.hashCode();
        return result;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public String toString(){
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
