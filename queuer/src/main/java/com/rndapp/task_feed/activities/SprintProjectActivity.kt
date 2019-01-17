package com.rndapp.task_feed.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rndapp.task_feed.R
import com.rndapp.task_feed.fragments.ProjectFragment
import com.rndapp.task_feed.fragments.SprintProjectFragment
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.models.SprintProject
import com.rndapp.task_feed.view_models.styleForProject

class SprintProjectActivity: AppCompatActivity() {
    private var project: SprintProject? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        val project = intent.extras?.getSerializable(ARG_PROJECT) as? SprintProject
        if (project?.project != null) setTheme(styleForProject(project.project))
        this.project = project

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        supportActionBar?.title = project?.project?.name

        if (savedInstanceState == null) {
            val fragment = SprintProjectFragment()
            fragment.sprintProject = project
            supportFragmentManager.beginTransaction()
                    .add(R.id.frameLayout, fragment)
                    .commit()
        }
    }

    companion object {
        val ARG_PROJECT = "project"
    }
}