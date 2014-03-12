package com.rndapp.task_feed.models;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.data.TaskDataSource;

import java.io.Serializable;
import java.util.Date;

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

    public static void uploadTaskToServer(Context context,
                                          RequestQueue queue,
                                          Task task,
                                          Response.Listener listener,
                                          Response.ErrorListener errorListener){
        ServerCommunicator.uploadTaskToServer(context, task, queue, listener, errorListener);
    }

    public static Task markAsFinished(Context context, RequestQueue queue, Task task){
        task.setFinished(true);
        updateTaskOnServer(context, queue, task);
        updateTask(context, task);
        return task;
    }

    public static Task updateTaskOnServer(Context context, RequestQueue queue, Task task){
        ServerCommunicator.updateTask(context,
                queue,
                task,
                new Response.Listener() {@Override public void onResponse(Object o) {}},
                new Response.ErrorListener() {@Override public void onErrorResponse(VolleyError volleyError) {}});

        Task.updateTask(context, task);
        return task;
    }

    public boolean isUpToDateWithServerTask(Task serverTask){
        return this.getUpdated_at().before(serverTask.getUpdated_at());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return !(project_id != task.project_id || id != task.id);

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
