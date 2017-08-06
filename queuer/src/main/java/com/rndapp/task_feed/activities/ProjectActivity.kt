package com.rndapp.task_feed.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
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
import com.google.gson.Gson
import com.rndapp.task_feed.QueuerApplication
import com.rndapp.task_feed.R
import com.rndapp.task_feed.adapters.TaskListAdapter
import com.rndapp.task_feed.api.ServerCommunicator
import com.rndapp.task_feed.api.VolleyManager
import com.rndapp.task_feed.interfaces.TaskDisplayer
import com.rndapp.task_feed.listeners.OnTaskClickedListener
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.models.Task

import org.json.JSONObject

/**
 * Created by eschrock on 2/4/14.
 */
class ProjectActivity : AppCompatActivity(), TaskDisplayer, OnTaskClickedListener {
    private var project: Project? = null
    private var adapter: TaskListAdapter? = null
    private val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            adapter!!.swapElements(viewHolder.adapterPosition, target.adapterPosition)
            adapter!!.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            project!!.markTaskAtPositionAsFinished(this@ProjectActivity, VolleyManager.queue!!, adjustPosition(viewHolder.adapterPosition))
            runOnUiThread {
                adapter!!.notifyDataSetChanged()
                adapter!!.removeFinishedTasks()
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        project = intent.extras.getSerializable(ARG_PROJECT) as Project
        if (Build.VERSION.SDK_INT > 19) {
            val blue = resources.getColor(R.color.blue)
            val red = resources.getColor(R.color.red)
            val yellow = resources.getColor(R.color.yellow)
            val goldenrod = resources.getColor(R.color.goldenrod)
            val turquoise = resources.getColor(R.color.turquoise)
            val orange = resources.getColor(R.color.orange)
            val plum = resources.getColor(R.color.plum)

            if (project!!.color == blue) setTheme(R.style.BlueTheme)
            if (project!!.color == red) setTheme(R.style.RedTheme)
            if (project!!.color == yellow) setTheme(R.style.YellowTheme)
            if (project!!.color == goldenrod) setTheme(R.style.GoldenrodTheme)
            if (project!!.color == turquoise) setTheme(R.style.TurquoiseTheme)
            if (project!!.color == orange) setTheme(R.style.OrangeTheme)
            if (project!!.color == plum) setTheme(R.style.PlumTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)


        actionBar!!.title = project!!.name

        if (Build.VERSION.SDK_INT <= 19) {
            val rootView = findViewById(R.id.project_root_view)
            rootView.setBackgroundColor(project!!.color)
        }

        adapter = TaskListAdapter(this, project!!.tasks, this)

        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val lv = findViewById(R.id.task_list_view) as RecyclerView
        lv.layoutManager = manager
        lv.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(lv)
    }

    override fun getContext(): Context {
        return this
    }

    private fun adjustPosition(position: Int): Int {
        var result = position
        for (i in 0..result + 1 - 1) {
            if (project!!.tasks[i].isFinished) {
                result++
            }
        }
        return result
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.project, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == getString(R.string.action_add_task)) {
            //new task
            createNewTask()
        } else if (item.itemId == R.id.action_edit_project) {
            //edit project
            editProject()
        } else if (item.itemId == R.id.action_hide_project) {
            //edit project
            hideProject()
        } else if (item.itemId == R.id.action_delete_project) {
            //edit project
            deleteProject()
        }
        return true
    }

    private fun createNewTask() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        // set title
        alertDialogBuilder.setTitle("New Task")

        val layout = layoutInflater.inflate(R.layout.new_task, null)

        val taskTitle = layout.findViewById(R.id.task) as EditText

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok",
                        DialogInterface.OnClickListener { dialog, id ->
                            setupForAsync()

                            val task = Task()
                            task.name = taskTitle.text.toString()
                            task.project_id = project!!.id

                            ServerCommunicator.uploadTaskToServer(this@ProjectActivity, task,
                                    VolleyManager.queue!!,
                                    { o: JSONObject ->
                                        val task1 = Gson().fromJson<Task>(o.toString(), Task::class.java!!)
                                        taskCreated(task1)
                                    } as Response.Listener<JSONObject>, { volleyError: VolleyError ->
                                //try again?
                            } as Response.ErrorListener)
                        })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun editTask(task: Task) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        // set title
        alertDialogBuilder.setTitle(getString(R.string.edit_task))

        val layout = layoutInflater.inflate(R.layout.new_task, null)

        val taskTitle = layout.findViewById(R.id.task) as EditText
        val taskPos = layout.findViewById(R.id.position) as EditText

        //populate text fields
        taskTitle.setText(task.name)
        taskPos.setText(task.order.toString())

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok",
                        DialogInterface.OnClickListener { dialog, id ->
                            setupForAsync()
                            task.name = taskTitle.text.toString()
                            project!!.updateTask(this@ProjectActivity,
                                    VolleyManager.queue!!,
                                    task,
                                    { o: JSONObject ->
                                        val task1 = Gson().fromJson<Task>(o.toString(), Task::class.java!!)
                                        taskUpdated(task1)
                                    } as Response.Listener<JSONObject>,
                            { volleyError: VolleyError ->
                                //try again?
                            } as Response.ErrorListener)
                        })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    internal var swatchColor: Int = 0
    private fun editProject() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        // set title
        alertDialogBuilder.setTitle(getString(R.string.action_edit_project))

        val layout = layoutInflater.inflate(R.layout.new_project, null)

        val projectTitle = layout.findViewById(R.id.projectName) as EditText
        projectTitle.setText(project!!.name)

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
                            project!!.name = projectTitle.text.toString()
                            project!!.color = swatchColor
                            Project.updateProjectOnServer(this@ProjectActivity,
                                    VolleyManager.queue!!,
                                    project!!,
                                    { o: JSONObject -> project = Gson().fromJson<Project>(o.toString(), Project::class.java!!) } as Response.Listener<JSONObject>,
                                    { volleyError: VolleyError ->
                                        //try again?
                                    } as Response.ErrorListener)
                            asyncEnded()
                        })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun hideProject() {
        project!!.isHidden = !project!!.isHidden
        finish()
    }

    private fun deleteProject() {

    }

    override fun setupForAsync() {
        findViewById(R.id.proj_loading_bar).visibility = View.VISIBLE
    }

    fun asyncEnded() {
        findViewById(R.id.proj_loading_bar).visibility = View.GONE
    }

    override fun taskUpdated(task: Task) {
        asyncEnded()
        runOnUiThread { adapter!!.notifyDataSetChanged() }
    }

    override fun taskCreated(task: Task) {
        asyncEnded()
        project!!.addTaskToBeginning(this,
                VolleyManager.queue!!, task)
        runOnUiThread { adapter!!.notifyDataSetChanged() }
    }

    override fun taskChangedOrder(task: Task) {
        setupForAsync()
        project!!.updateTask(this,
                VolleyManager.queue!!,
                task,
                { o: JSONObject ->
                    val task1 = Gson().fromJson<Task>(o.toString(), Task::class.java!!)
                    taskUpdated(task1)
                } as Response.Listener<JSONObject>, { volleyError: VolleyError ->
            //try again?
        } as Response.ErrorListener)
        runOnUiThread { adapter!!.notifyDataSetChanged() }
    }

    override fun onTaskClicked(task: Task) {
        editTask(task)
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        val ARG_PROJECT = "project"
    }
}
