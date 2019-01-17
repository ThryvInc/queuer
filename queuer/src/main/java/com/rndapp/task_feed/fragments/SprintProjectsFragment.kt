package com.rndapp.task_feed.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.android.volley.Response
import com.rndapp.task_feed.R
import com.rndapp.task_feed.activities.ChooserActivity
import com.rndapp.task_feed.activities.SprintActivity
import com.rndapp.task_feed.adapters.ProjectsAdapter
import com.rndapp.task_feed.adapters.SprintProjectsAdapter
import com.rndapp.task_feed.api.SprintRequest
import com.rndapp.task_feed.api.VolleyManager
import com.rndapp.task_feed.listeners.OnProjectClickedListener
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.models.Sprint
import com.rndapp.task_feed.models.SprintProject
import kotlinx.android.synthetic.main.standard_recycler.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ell on 11/26/17.
 */
class SprintProjectsFragment: RecyclerViewFragment() {
    var sprint: Sprint? = null
        set(value) {
            field = value
            projects.removeAll(projects)
            if (value != null) {
                projects.addAll(value.sprintProjects)
            }
        }
    var projects: ArrayList<SprintProject> = ArrayList()
    private var adapter: SprintProjectsAdapter? = null
    private lateinit var projectClickedListener: (SprintProject) -> Unit

    companion object {
        private val SPRINT_ARG = "SPRINT_ARG"
        fun newInstance(projectClickedListener: (SprintProject) -> Unit): SprintProjectsFragment {
            val fragment = SprintProjectsFragment()
            fragment.projectClickedListener = projectClickedListener
            return fragment
        }
    }

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)

        adapter = projects.let { SprintProjectsAdapter(it, projectClickedListener) }
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
                this@SprintProjectsFragment.sprint = sprint
                this@SprintProjectsFragment.adapter?.sprintProjects = ArrayList(this@SprintProjectsFragment.projects.sortedBy { it.createdAt })
                this@SprintProjectsFragment.adapter?.updateArray(this@SprintProjectsFragment.projects.sortedBy { it.createdAt })
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
