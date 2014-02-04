package com.rndapp.task_feed.interfaces;

import com.rndapp.task_feed.models.Project;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/23/13
 * Time: 5:47 PM
 */
public interface ProjectDisplayer {
    public void setupNav(ArrayList<Project> projects);
    public void setupForAsync();
    public void asyncEnded();
}
