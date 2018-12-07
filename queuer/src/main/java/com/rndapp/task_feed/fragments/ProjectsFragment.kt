package com.rndapp.task_feed.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import com.android.volley.Response
import com.rndapp.task_feed.R
import com.rndapp.task_feed.activities.ChooserActivity
import com.rndapp.task_feed.activities.ProjectActivity
import com.rndapp.task_feed.activities.SprintActivity
import com.rndapp.task_feed.adapters.ProjectsAdapter
import com.rndapp.task_feed.api.CreateSprintRequest
import com.rndapp.task_feed.api.SprintRequest
import com.rndapp.task_feed.api.VolleyManager
import com.rndapp.task_feed.listeners.OnProjectClickedListener
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.models.Sprint
import java.util.*

/**
 * Created by ell on 11/26/17.
 */
class ProjectsFragment: Fragment() {
    var sprint: Sprint? = null
        set(value) {
            field = value
            projects.removeAll(projects)
            if (value != null) {
                projects.addAll(value.projects)
            }
        }
    var projects: ArrayList<Project> = ArrayList()
    private var adapter: ProjectsAdapter? = null
    private lateinit var projectClickedListener: OnProjectClickedListener

    companion object {
        private val SPRINT_ARG = "SPRINT_ARG"
        fun newInstance(projectClickedListener: OnProjectClickedListener): ProjectsFragment {
            val fragment = ProjectsFragment()
            fragment.projectClickedListener = projectClickedListener
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_projects, container, false)

        val manager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)//LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val rv = rootView.findViewById<RecyclerView>(R.id.rv_projects)
        rv.layoutManager = manager

        adapter = projects.let { ProjectsAdapter(it, projectClickedListener) }
        rv.adapter = adapter

        rootView.findViewById<View>(R.id.fab).setOnClickListener {
            chooseProject()
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    fun refresh() {
        if (sprint != null) {
            fetchSprintDetails(sprint)
        }
    }

    fun fetchSprintDetails(sprintToFetch: Sprint?) {
        if (sprintToFetch != null) {
            val request = SprintRequest(sprintToFetch.id.toString(), Response.Listener { sprint ->
                this@ProjectsFragment.sprint = sprint
                this@ProjectsFragment.adapter?.notifyDataSetChanged()
            }, Response.ErrorListener { error ->
                error.printStackTrace()
            })
            VolleyManager.queue?.add(request)
        }
    }

    fun chooseProject() {
        val intent = Intent(context, ChooserActivity::class.java)
        intent.putExtra(ChooserActivity.CHOOSER_TYPE, ChooserActivity.PROJECTS)
        activity?.startActivityForResult(intent, SprintActivity.PROJECT_REQUEST)
    }
}
