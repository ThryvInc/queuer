package com.rndapp.task_feed.activities

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.android.volley.Response
import com.android.volley.VolleyError
import com.rndapp.task_feed.R
import com.rndapp.task_feed.adapters.TaskAdapter
import com.rndapp.task_feed.api.*
import com.rndapp.task_feed.fragments.DayFragment
import com.rndapp.task_feed.fragments.ProjectFragment
import com.rndapp.task_feed.interfaces.TaskDisplayer
import com.rndapp.task_feed.listeners.OnTaskClickedListener
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.models.ProjectColor
import com.rndapp.task_feed.models.Task

/**
 * Created by eschrock on 2/4/14.
 */
class ProjectActivity : AppCompatActivity() {
    private var project: Project? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        project = intent.extras.getSerializable(ARG_PROJECT) as Project
        if (project?.color == ProjectColor.BLUE.rgb) setTheme(R.style.BlueTheme)
        if (project?.color == ProjectColor.RED.rgb) setTheme(R.style.RedTheme)
        if (project?.color == ProjectColor.YELLOW.rgb) setTheme(R.style.YellowTheme)
        if (project?.color == ProjectColor.GOLDENROD.rgb) setTheme(R.style.GoldenrodTheme)
        if (project?.color == ProjectColor.TURQUOISE.rgb) setTheme(R.style.TurquoiseTheme)
        if (project?.color == ProjectColor.ORANGE.rgb) setTheme(R.style.OrangeTheme)
        if (project?.color == ProjectColor.PLUM.rgb) setTheme(R.style.PlumTheme)
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
