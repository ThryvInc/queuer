package com.rndapp.task_feed.models;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.rndapp.task_feed.api.ServerCommunicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 1:39 PM
 *
 */
public class Project implements Serializable{
    private int id;
    private int localId;
    private String name;
    private Date created_at;
    private Date updated_at;
    private ArrayList<Task> tasks = new ArrayList<Task>();
    private int color;
    private boolean isHidden = false;

    public Project(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public Project() {}

    public String toString(){
        return name;
    }

    public String getFirstTaskText(){
        String output = null;
        for (Task task : tasks){
            if (!task.isFinished()) {
                output = task.getName();
                break;
            }
        }
        return output;
    }

    public void addTaskToBeginning(Context context, RequestQueue queue, Task task){
        task.setProject_id(this.getId());
        tasks.add(0,task);
        updatePositions(context, queue, new Response.Listener() {
            @Override
            public void onResponse(Object o) {

            }
        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
    }

    public void addTaskRespectingOrder(Context context, Task task){
        task.setProject_id(this.getId());
        tasks.add(0,task);
        sortTasks();
    }

    public void removeFirstTask(Context context, RequestQueue queue){
        if (tasks.size() != 0){
            int indexOfTask = -1;
            for (Task task : tasks){
                if (!task.isFinished()) {
                    indexOfTask = tasks.indexOf(task);
                    break;
                }
            }
            if (indexOfTask != -1) markTaskAtPositionAsFinished(context, queue, indexOfTask);
        }
    }

    public void markTaskAtPositionAsFinished(Context context, RequestQueue queue, int position){
        tasks.set(position, Task.markAsFinished(context, queue, tasks.get(position)));
        updatePositions(context, queue, null, null);
    }

//    public void deleteTask(Context context, int position){
//        TaskDataSource source = new TaskDataSource(context);
//        source.open();
//        source.deleteTask(tasks.get(position));
//        source.close();
//        tasks.remove(position);
//        updatePositions(context);
//    }

    public void updateTask(Context context, RequestQueue queue, Task task,
                           Response.Listener listener,
                           Response.ErrorListener errorListener){
        if (task.getOrder() != tasks.indexOf(task)){
            task.setOrder(tasks.indexOf(task));
        }
        ServerCommunicator.updateTask(context,
                queue,
                task, listener, errorListener);
        updatePositions(context, queue, listener, errorListener);
    }

    private void updatePositions(Context context, RequestQueue queue,
                                 Response.Listener listener,
                                 Response.ErrorListener errorListener){
        ArrayList<Task> tasksToUpdate = new ArrayList<Task>();
        for (Task task : tasks){
            if (task.getOrder() != tasks.indexOf(task)){
                task.setOrder(tasks.indexOf(task));
                tasksToUpdate.add(task);

                ServerCommunicator.updateTask(context,
                        queue,
                        task, listener, errorListener);
            }
        }
    }

    public void sortTasks(){
        Collections.sort(tasks);
    }

    public Task getTask(int position){
        return tasks.get(position);
    }

    public boolean isEmpty(){
        return getFirstTaskText() == null || isHidden();
    }

    public boolean isUpToDateWithServerProject(Task serverProject){
        return this.getUpdated_at().before(serverProject.getUpdated_at());
    }

    public static void uploadProjectToServer(Context context, RequestQueue queue, Project project,
                                             Response.Listener listener,
                                             Response.ErrorListener errorListener){

            ServerCommunicator.uploadProjectToServer(context, project, queue,
                    listener,
                    errorListener);
    }

    public static void updateProjectOnServer(Context context, RequestQueue queue, Project project,
                                             Response.Listener listener,
                                             Response.ErrorListener errorListener){
            ServerCommunicator.updateProjectOnServer(context,
                    project,
                    queue,
                    listener,
                    errorListener
            );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (id != project.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + localId;
        return result;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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

    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
