package com.rndapp.task_feed.activities

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle

import android.support.v4.app.ActionBarDrawerToggle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.android.volley.Response
import com.android.volley.VolleyError

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rndapp.task_feed.QueuerApplication
import com.rndapp.task_feed.R
import com.rndapp.task_feed.adapters.NavAdapter
import com.rndapp.task_feed.adapters.ProjectListAdapter
import com.rndapp.task_feed.api.VolleyManager
import com.rndapp.task_feed.broadcast_receivers.ListWidgetProvider
import com.rndapp.task_feed.interfaces.ProjectDisplayer
import com.rndapp.task_feed.listeners.OnProjectClickedListener
import com.rndapp.task_feed.models.*
import org.json.JSONArray
import org.json.JSONObject

import java.util.ArrayList

class FeedActivity : AppCompatActivity(), ProjectDisplayer, OnProjectClickedListener {
    private var drawerLayout: DrawerLayout? = null
    private var drawerList: ListView? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    var projects: ArrayList<Project>? = ArrayList()
    private var adapter: ProjectListAdapter? = null
    internal var simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            adapter!!.swapElements(viewHolder.adapterPosition, target.adapterPosition)
            adapter!!.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            removeItemFromProject(adjustPosition(viewHolder.adapterPosition))
            adapter!!.removeEmptyProjects()
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        setupForAsync()
        ActivityUtils.downloadProjectsFromServer(this, VolleyManager.queue!!, object: Response.Listener<JSONArray> {
            override fun onResponse(response: JSONArray?) {
                val listOfProjects = object : TypeToken<List<Project>>() {}.type
                val serverProjects = Gson().fromJson<ArrayList<Project>>(response.toString(), listOfProjects)
                if (projects == null) {
                    projects = serverProjects
                } else {
                    projects!!.removeAll(projects!!)
                    projects!!.addAll(serverProjects)
                }
                setupNav(projects)
                if (adapter == null) {
                    adapter = ProjectListAdapter(projects, this@FeedActivity)
                } else {
                    adapter!!.notifyDataSetChanged()
                }
                asyncEnded()
            }
        }, object: Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
                error!!.printStackTrace()
                asyncEnded()
            }
        })

        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val lv = findViewById(R.id.project_list_view) as RecyclerView
        lv.layoutManager = manager

        adapter = projects?.let { ProjectListAdapter(it, this) }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(lv)

        lv.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        //        setupNav(null);
    }

    private fun startProjectActivity(project: Project) {
        val intent = Intent(this@FeedActivity, ProjectActivity::class.java)
        intent.putExtra(ProjectActivity.ARG_PROJECT, project)
        startActivity(intent)
    }

    private fun adjustPosition(position: Int): Int {
        var result = position
        for (i in 0..result + 1 - 1) {
            if (projects!![i].isEmpty || projects!![i].isHidden) {
                result++
            }
        }
        return result
    }

    fun removeItemFromProject(position: Int) {
        projects!![position].removeFirstTask(this, VolleyManager.queue!!)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.feed, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle!!.onOptionsItemSelected(item)) {
            return true
        }
        when (item.itemId) {
            R.id.action_add_project -> newProject()
            R.id.action_logout -> ActivityUtils.logout(this)
        }

        return super.onOptionsItemSelected(item)
    }


    override fun setupForAsync() {
        findViewById(R.id.loading_bar).visibility = View.VISIBLE
    }

    override fun asyncEnded() {
        findViewById(R.id.loading_bar).visibility = View.GONE
    }

    override fun setupNav(projectArrayList: ArrayList<Project>?) {
        if (projectArrayList != null) {
            projects = projectArrayList
        }

        // Set up the action bar to show a dropdown list.
        val actionBar = actionBar
        actionBar!!.setDisplayShowTitleEnabled(true)

        drawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        drawerLayout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        drawerList = findViewById(R.id.left_drawer) as ListView

        drawerToggle = object : ActionBarDrawerToggle(
                this, /* host Activity */
                drawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state.  */
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                getActionBar()!!.setTitle(R.string.app_name)
            }

            /** Called when a drawer has settled in a completely open state.  */
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                getActionBar()!!.setTitle(R.string.app_name)
            }
        }

        getActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getActionBar()!!.setHomeButtonEnabled(true)

        drawerLayout!!.setDrawerListener(drawerToggle)

        // Set the adapter for the list view
        drawerList!!.adapter = NavAdapter(this, projects!!)

        // Set the list's click listener
        drawerList!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            drawerList!!.setItemChecked(position, true)
            drawerLayout!!.closeDrawer(drawerList)
            startProjectActivity(projects!![position])
        }
    }

    internal var swatchColor: Int = 0
    fun newProject() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        // set title
        alertDialogBuilder.setTitle("New Project")

        val layout = layoutInflater.inflate(R.layout.new_project, null)

        val taskTitle = layout.findViewById(R.id.projectName) as EditText

        val swatch = layout.findViewById(R.id.color_swatch)

        val btnRed = layout.findViewById(R.id.btn_red) as Button
        val btnBlue = layout.findViewById(R.id.btn_blue) as Button
        val btnPlum = layout.findViewById(R.id.btn_plum) as Button
        val btnGold = layout.findViewById(R.id.btn_yellow) as Button
        val btnOrange = layout.findViewById(R.id.btn_orange) as Button
        val btnGreen = layout.findViewById(R.id.btn_green) as Button
        val btnTurquoise = layout.findViewById(R.id.btn_turquoise) as Button

        val listener = View.OnClickListener { v ->
            when (v.id) {
                R.id.btn_blue -> {
                    swatchColor = resources.getColor(R.color.blue)
                    swatch.setBackgroundColor(resources.getColor(R.color.blue))
                }
                R.id.btn_green -> {
                    swatchColor = resources.getColor(R.color.green)
                    swatch.setBackgroundColor(resources.getColor(R.color.green))
                }
                R.id.btn_orange -> {
                    swatchColor = resources.getColor(R.color.orange)
                    swatch.setBackgroundColor(resources.getColor(R.color.orange))
                }
                R.id.btn_plum -> {
                    swatchColor = resources.getColor(R.color.plum)
                    swatch.setBackgroundColor(resources.getColor(R.color.plum))
                }
                R.id.btn_red -> {
                    swatchColor = resources.getColor(R.color.red)
                    swatch.setBackgroundColor(resources.getColor(R.color.red))
                }
                R.id.btn_yellow -> {
                    swatchColor = resources.getColor(R.color.yellow)
                    swatch.setBackgroundColor(resources.getColor(R.color.yellow))
                }
                R.id.btn_turquoise -> {
                    swatchColor = resources.getColor(R.color.turquoise)
                    swatch.setBackgroundColor(resources.getColor(R.color.turquoise))
                }
            }
        }

        btnRed.setOnClickListener(listener)
        btnBlue.setOnClickListener(listener)
        btnOrange.setOnClickListener(listener)
        btnGreen.setOnClickListener(listener)
        btnGold.setOnClickListener(listener)
        btnPlum.setOnClickListener(listener)
        btnTurquoise.setOnClickListener(listener)

        swatchColor = resources.getColor(R.color.goldenrod)

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok",
                        DialogInterface.OnClickListener { dialog, id ->
                            setupForAsync()
                            Project.uploadProjectToServer(this@FeedActivity,
                                    VolleyManager.queue!!,
                                    Project(taskTitle.text.toString(), swatchColor),
                                    { o: JSONObject ->
                                        val project = Gson().fromJson<Project>(o.toString(), Project::class.java!!)
                                        projects!!.add(project)
                                        setupNav(projects)
                                        val intent = Intent(this@FeedActivity, ProjectActivity::class.java)
                                        intent.putExtra(ProjectActivity.ARG_PROJECT, project)
                                        startActivity(intent)
                                    } as Response.Listener<JSONObject>, { volleyError: VolleyError ->
                                //try again?
                            } as Response.ErrorListener)
                            asyncEnded()
                        })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onPause() {
        super.onPause()
        val updateWidget = Intent(this, ListWidgetProvider::class.java)
        updateWidget.action = "update_widget"
        val pending = PendingIntent.getBroadcast(this, 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT)
        try {
            pending.send()
        } catch (e: PendingIntent.CanceledException) {
            e.printStackTrace()
        }

    }

    override fun onProjectClicked(project: Project) {
        startProjectActivity(project)
    }

    companion object {
        private val TAG = "FeedActivity"
    }
}
