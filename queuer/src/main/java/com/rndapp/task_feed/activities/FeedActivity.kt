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
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.android.volley.Response
import com.android.volley.VolleyError
import com.rndapp.task_feed.R
import com.rndapp.task_feed.adapters.NavAdapter
import com.rndapp.task_feed.adapters.ProjectsAdapter
import com.rndapp.task_feed.api.CreateProjectRequest
import com.rndapp.task_feed.api.ProjectsRequest
import com.rndapp.task_feed.api.VolleyManager
import com.rndapp.task_feed.broadcast_receivers.ListWidgetProvider
import com.rndapp.task_feed.interfaces.ProjectDisplayer
import com.rndapp.task_feed.listeners.OnProjectClickedListener
import com.rndapp.task_feed.managers.SessionManager
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.models.ProjectColor
import java.util.*

class FeedActivity : AppCompatActivity(), ProjectDisplayer, OnProjectClickedListener {
    private var drawerLayout: DrawerLayout? = null
    private var drawerList: ListView? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    var projects: ArrayList<Project> = ArrayList()
    private var adapter: ProjectsAdapter? = null
    internal var simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
//            adapter?.swapElements(viewHolder.adapterPosition, target.adapterPosition)
//            adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            removeItemFromProject(adjustPosition(viewHolder.adapterPosition))
//            adapter?.removeEmptyProjects()
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        refresh()

        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val lv = findViewById(R.id.project_list_view) as RecyclerView
        lv.layoutManager = manager

        adapter = projects.let { ProjectsAdapter(it, this) }
        lv.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(lv)
    }

    override fun onResume() {
        super.onResume()
        setupNav(null)
    }

    private fun refresh() {
        setupForAsync()
        val request = ProjectsRequest(Response.Listener { serverProjects ->
            projects.removeAll(projects)
            projects.addAll(serverProjects)
            setupNav(projects)
            if (adapter == null) {
                adapter = ProjectsAdapter(projects, this@FeedActivity)
            } else {
                adapter?.projects = projects
                adapter?.notifyDataSetChanged()
            }
            asyncEnded()
        }, object: Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError?) {
                error!!.printStackTrace()
                asyncEnded()
            }
        })
        VolleyManager.queue?.add(request)
    }

    private fun startProjectActivity(project: Project) {
        val intent = Intent(this@FeedActivity, ProjectActivity::class.java)
        intent.putExtra(ProjectActivity.ARG_PROJECT, project)
        startActivity(intent)
    }

    private fun adjustPosition(position: Int): Int {
        var result = position
//        for (i in 0..result + 1 - 1) {
//            if (/*projects!![i] ||*/ projects!![i].isHidden) {
//                result++
//            }
//        }
        return result
    }

    fun removeItemFromProject(position: Int) {
//        projects!![position].removeFirstTask(this, VolleyManager.queue!!)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.feed, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        //TODO: Project list drawer
        if (drawerToggle?.onOptionsItemSelected(item) ?: false) {
            return true
        }
        when (item.itemId) {
            R.id.action_add_project -> newProject()
            R.id.action_logout -> SessionManager.logout(this)
        }

        return super.onOptionsItemSelected(item)
    }


    override fun setupForAsync() {
        findViewById<View>(R.id.loading_bar).visibility = View.VISIBLE
    }

    override fun asyncEnded() {
        findViewById<View>(R.id.loading_bar).visibility = View.GONE
    }

    override fun setupNav(projectArrayList: ArrayList<Project>?) {
        if (projectArrayList != null) {
            projects = projectArrayList
        }

        // Set up the action bar to show a dropdown list.
        actionBar?.setDisplayShowTitleEnabled(true)

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
                getActionBar()?.setTitle(R.string.app_name)
            }

            /** Called when a drawer has settled in a completely open state.  */
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                getActionBar()?.setTitle(R.string.app_name)
            }
        }

        getActionBar()?.setDisplayHomeAsUpEnabled(true)
        getActionBar()?.setHomeButtonEnabled(true)

        drawerLayout?.setDrawerListener(drawerToggle)

        // Set the adapter for the list view
        drawerList?.adapter = NavAdapter(this, projects)

        // Set the list's click listener
        drawerList?.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            drawerList!!.setItemChecked(position, true)
            drawerLayout!!.closeDrawer(drawerList!!)
            startProjectActivity(projects!![position])
        }
    }

    internal var swatchColor: Int = 0
    internal var swatchId: Int = R.color.goldenrod
    fun newProject() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        // set title
        alertDialogBuilder.setTitle("New Project")

        val layout = layoutInflater.inflate(R.layout.new_project, null)

        val taskTitle = layout.findViewById<EditText>(R.id.projectName)

        val swatch: View = layout.findViewById(R.id.color_swatch)

        val btnRed = layout.findViewById<Button>(R.id.btn_red)
        val btnBlue = layout.findViewById<Button>(R.id.btn_blue)
        val btnPlum = layout.findViewById<Button>(R.id.btn_plum)
        val btnGold = layout.findViewById<Button>(R.id.btn_yellow)
        val btnOrange = layout.findViewById<Button>(R.id.btn_orange)
        val btnGreen = layout.findViewById<Button>(R.id.btn_green)
        val btnTurquoise = layout.findViewById<Button>(R.id.btn_turquoise)

        val listener = View.OnClickListener { v ->
            when (v.id) {
                R.id.btn_blue -> setSwatch(R.color.blue, swatch)
                R.id.btn_green -> setSwatch(R.color.green, swatch)
                R.id.btn_orange -> setSwatch(R.color.orange, swatch)
                R.id.btn_plum -> setSwatch(R.color.plum, swatch)
                R.id.btn_red -> setSwatch(R.color.red, swatch)
                R.id.btn_yellow -> setSwatch(R.color.yellow, swatch)
                R.id.btn_turquoise -> setSwatch(R.color.turquoise, swatch)
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
                            val color = ProjectColor.projectColorFromId(swatchId)
                            val project = Project(name = taskTitle.text.toString(), color = color.rgb)
                            val request = CreateProjectRequest(project, Response.Listener { response ->
                                projects.add(response)
                                setupNav(projects)
                                val intent = Intent(this@FeedActivity, ProjectActivity::class.java)
                                intent.putExtra(ProjectActivity.ARG_PROJECT, response)
                                startActivity(intent)
                                asyncEnded()
                            }, Response.ErrorListener { error ->
                                error.printStackTrace()
                                asyncEnded()
                            })
                            VolleyManager.queue?.add(request)
                        })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun setSwatch(id: Int, swatch: View) {
        swatchId = id
        swatchColor = resources.getColor(id)
        swatch.setBackgroundColor(swatchColor)
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
