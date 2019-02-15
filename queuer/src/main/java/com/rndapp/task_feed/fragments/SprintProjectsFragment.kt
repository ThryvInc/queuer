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
import com.rndapp.task_feed.view_models.SprintActivityViewModel
import kotlinx.android.synthetic.main.standard_recycler.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ell on 11/26/17.
 */
class SprintProjectsFragment: RecyclerViewFragment() {
    var viewModel: SprintActivityViewModel? = null
    var projects: ArrayList<SprintProject> = ArrayList()
    private var adapter: SprintProjectsAdapter? = null
    private lateinit var projectClickedListener: (SprintProject) -> Unit

    companion object {
        fun newInstance(projectClickedListener: (SprintProject) -> Unit, viewModel: SprintActivityViewModel): SprintProjectsFragment {
            val fragment = SprintProjectsFragment()
            fragment.projectClickedListener = projectClickedListener
            fragment.viewModel = viewModel
            return fragment
        }
    }

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)

        adapter = projects.let { SprintProjectsAdapter(it, projectClickedListener) }
        recyclerView.adapter = adapter

        viewModel?.isLoadingSprintProjectsLiveData?.observeForever {
            if (it != null) {
                refreshLayout?.isRefreshing = it
            }
        }
        viewModel?.sprintProjectsLiveData?.observeForever {
            projects.removeAll(projects)
            val sprintProjects = it
            if (sprintProjects != null) {
                projects.addAll(sprintProjects)
                adapter?.updateArray(projects)
            }
        }

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
        viewModel?.refreshSprintProjects()
    }

    fun chooseProject() {
        val intent = Intent(context, ChooserActivity::class.java)
        intent.putExtra(ChooserActivity.CHOOSER_TYPE, ChooserActivity.PROJECTS)
        activity?.startActivityForResult(intent, SprintActivity.PROJECT_REQUEST)
    }
}
