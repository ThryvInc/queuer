package com.rndapp.task_feed.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.android.volley.Response
import com.rndapp.task_feed.R
import com.rndapp.task_feed.adapters.SprintAdapter
import com.rndapp.task_feed.api.SprintsRequest
import com.rndapp.task_feed.api.VolleyManager
import com.rndapp.task_feed.listeners.OnSprintClickedListener
import com.rndapp.task_feed.models.Sprint

/**
 * Created by ell on 8/5/17.
 */
class SprintsActivity : AppCompatActivity(), OnSprintClickedListener {
    private var adapter: SprintAdapter? = null
    private var sprints: ArrayList<Sprint> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sprints)

        supportActionBar?.title = "Sprints"

        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val rv = findViewById(R.id.rv_sprints) as RecyclerView
        rv.layoutManager = manager

        adapter = SprintAdapter(sprints, this)
        rv.adapter = adapter

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            newSprint()
        }
    }

    override fun onResume() {
        super.onResume()

        if (VolleyManager.queue == null) {
            VolleyManager.init(this.applicationContext)
        }

        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sprints, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        startActivity(Intent(this, FeedActivity::class.java))
        return true
    }

    private fun refresh() {
        val request = SprintsRequest(Response.Listener { serverSprints ->
            this@SprintsActivity.sprints.removeAll(this@SprintsActivity.sprints)
            this@SprintsActivity.sprints.addAll(serverSprints)
            adapter?.updateArray(this@SprintsActivity.sprints)
            adapter?.notifyDataSetChanged()
        }, Response.ErrorListener { error ->
            error.printStackTrace()
        })
        VolleyManager.queue?.add(request)
    }

    override fun onSprintClicked(sprint: Sprint) {
        val intent = Intent(this, SprintActivity::class.java)
        intent.putExtra(SprintActivity.SPRINT_EXTRA, sprint)
        startActivity(intent)
    }

    private fun newSprint() {
        startActivity(Intent(this, SprintActivity::class.java))
    }
}