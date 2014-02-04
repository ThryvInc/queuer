package com.rndapp.task_feed.models;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 7/13/13
 * Time: 2:40 PM
 */
public class User {
    private int id;
    private String username;
    private List<Project> projects;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
