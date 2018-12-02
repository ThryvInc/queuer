package com.rndapp.task_feed.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rndapp.task_feed.R
import com.rndapp.task_feed.fragments.ProjectFragment
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.view_models.styleForProject

/**
 * Created by eschrock on 2/4/14.
 */
class ProjectActivity : AppCompatActivity() {
    private var project: Project? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        val project = intent.extras.getSerializable(ARG_PROJECT) as? Project
        if (project != null) setTheme(styleForProject(project))
        this.project = project

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        supportActionBar?.title = project?.name

        if (savedInstanceState == null) {
            val fragment = ProjectFragment()
            fragment.project = project
            supportFragmentManager.beginTransaction()
                    .add(R.id.frameLayout, fragment)
                    .commit()
        }
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        val ARG_PROJECT = "project"
    }
}
