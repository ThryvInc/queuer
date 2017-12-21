package com.rndapp.task_feed.interfaces

import com.rndapp.task_feed.models.Project

import java.util.ArrayList

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/23/13
 * Time: 5:47 PM
 */
interface ProjectDisplayer {
    fun setupNav(projects: ArrayList<Project>?)
    fun setupForAsync()
    fun asyncEnded()
}
