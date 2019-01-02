package com.rndapp.task_feed.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Response
import com.rndapp.task_feed.R
import com.rndapp.task_feed.activities.ChooserActivity
import com.rndapp.task_feed.activities.SprintActivity
import com.rndapp.task_feed.adapters.ProjectsAdapter
import com.rndapp.task_feed.api.SprintRequest
import com.rndapp.task_feed.api.VolleyManager
import com.rndapp.task_feed.listeners.OnProjectClickedListener
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.models.Sprint
import kotlinx.android.synthetic.main.standard_recycler.*
import java.util.*

/**
 * Created by ell on 11/26/17.
 */
class ProjectsFragment: RecyclerViewFragment() {
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

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)

        adapter = projects.let { ProjectsAdapter(it, projectClickedListener) }
        recyclerView.adapter = adapter

        rootView.findViewById<View>(R.id.fab).setOnClickListener {
            chooseProject()
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
    }

    override fun refresh() {
        if (sprint != null) {
            fetchSprintDetails(sprint)
        }
    }

    fun fetchSprintDetails(sprintToFetch: Sprint?) {
        if (sprintToFetch != null) {
            refreshLayout.isRefreshing = true
            val request = SprintRequest(sprintToFetch.id.toString(), Response.Listener { sprint ->
                this@ProjectsFragment.sprint = sprint
                this@ProjectsFragment.adapter?.projects = this@ProjectsFragment.projects
                this@ProjectsFragment.adapter?.updateArray(this@ProjectsFragment.projects)
                refreshLayout.isRefreshing = false
            }, Response.ErrorListener { error ->
                error.printStackTrace()
                refreshLayout.isRefreshing = false
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
