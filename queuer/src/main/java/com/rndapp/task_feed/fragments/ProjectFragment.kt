package com.rndapp.task_feed.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.VolleyError
import com.rndapp.task_feed.R
import com.rndapp.task_feed.adapters.TaskAdapter
import com.rndapp.task_feed.api.*
import com.rndapp.task_feed.interfaces.TaskDisplayer
import com.rndapp.task_feed.listeners.OnTaskClickedListener
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.models.ProjectColor
import com.rndapp.task_feed.models.Task
import kotlinx.android.synthetic.main.fragment_project.*
import kotlinx.android.synthetic.main.standard_recycler.*

class ProjectFragment: RecyclerViewFragment(), TaskDisplayer, OnTaskClickedListener {
    var project: Project? = null
        set(value) {
            field = value
            if (value != null) {
                setupTasks()
            }
        }
    private var adapter: TaskAdapter? = null

    private val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            val position = viewHolder.adapterPosition
            val task = adapter?.tasks?.get(position)
            if (task != null) {
                val request = FinishTaskRequest(task, Response.Listener {

                }, Response.ErrorListener { error ->
                    error.printStackTrace()
                })
                VolleyManager.queue?.add(request)
            }
            task?.isFinished = true
            adapter?.updateArray(adapter!!.tasks.filter { !it.isFinished })
            adapter?.notifyDataSetChanged()
        }
    }

    init {
        layoutId = R.layout.fragment_project
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)

        leftPoints.visibility = View.GONE
        rightPoints.visibility = View.GONE
        leftPoints.findViewById<TextView>(R.id.ptTypeTextView).text = "remaining"

        recyclerView?.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        rootView.findViewById<View>(R.id.fab).setOnClickListener {
            createNewTask()
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun refresh() {
        refreshLayout.isRefreshing = true
        val request = ProjectRequest(project?.id ?: 0, Response.Listener { project ->
            this.project = project
            refreshLayout.isRefreshing = false
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            refreshLayout.isRefreshing = false
        })
        VolleyManager.queue?.add(request)
    }

    fun setupTasks() {
        leftPoints?.visibility = View.VISIBLE
        leftPoints?.findViewById<TextView>(R.id.pointsTextView)?.text =
                project?.remainingPoints.toString()

        rightPoints?.visibility = View.VISIBLE
        rightPoints?.findViewById<TextView>(R.id.pointsTextView)?.text =
                "${(project?.points ?: 0) - (project?.remainingPoints ?: 0)}"

        if (adapter != null) {
            adapter?.updateArray(ArrayList(project?.tasks?.filter { !it.isFinished } ?: ArrayList()))
        } else {
            adapter = TaskAdapter(ArrayList(project?.tasks?.filter { !it.isFinished } ?: ArrayList()), this)
            recyclerView?.adapter = adapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.project, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when {
            item.itemId == R.id.action_edit_project -> editProject()
            item.itemId == R.id.action_delete_project -> deleteProject()
        }
        return true
    }

    private fun createNewTask() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        // set title
        alertDialogBuilder.setTitle("New Task")

        val layout = activity?.layoutInflater?.inflate(R.layout.new_task, null)

        if (layout == null) return

        val taskTitle = layout.findViewById<EditText>(R.id.task)
        val taskPos = layout.findViewById<EditText>(R.id.position)

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok",
                        { dialog, id ->
                            setupForAsync()

                            val task = Task()
                            task.name = taskTitle.text.toString()
                            task.project_id = project!!.id
                            task.points = Integer.valueOf(taskPos.text.toString())

                            val request = CreateTaskRequest(task, Response.Listener { response ->
                                taskCreated(response)
                            }, Response.ErrorListener { error ->
                                error.printStackTrace()
                            })
                            VolleyManager.queue?.add(request)
                        })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun editTask(task: Task) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        // set title
        alertDialogBuilder.setTitle(getString(R.string.edit_task))

        val layout = activity?.layoutInflater?.inflate(R.layout.new_task, null)
        if (layout == null) return

        val taskTitle = layout.findViewById<EditText>(R.id.task)
        val taskPos = layout.findViewById<EditText>(R.id.position)

        //populate text fields
        taskTitle.setText(task.name)
        taskPos.setText(task.points.toString())

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok",
                        { dialog, id ->
                            setupForAsync()
                            task.name = taskTitle.text.toString()
                            task.points = Integer.valueOf(taskPos.text.toString())
                            val request = EditTaskRequest(task, Response.Listener
                            { task1 ->
                                taskUpdated(task1)
                            },
                                    Response.ErrorListener { error ->
                                        error.printStackTrace()
                                        asyncEnded()
                                    })
                            VolleyManager.queue?.add(request)
                        })
                .setNegativeButton("Cancel", { dialog, id -> })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    internal var swatchColor: Int = 0
    internal var swatchId: Int = R.color.goldenrod
    private fun editProject() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        // set title
        alertDialogBuilder.setTitle(getString(R.string.action_edit_project))

        val layout = activity?.layoutInflater?.inflate(R.layout.new_project, null)
        if (layout == null) return

        val projectTitle = layout.findViewById<EditText>(R.id.projectName)
        projectTitle.setText(project!!.name)

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
                            project!!.name = projectTitle.text.toString()
                            project!!.color = color.rgb
                            val request = EditProjectRequest(project!!,
                                    Response.Listener { project ->
                                        asyncEnded()
                                    },
                                    { volleyError: VolleyError ->
                                        volleyError.printStackTrace()
                                        asyncEnded()
                                        refresh()
                                    } as Response.ErrorListener)
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

    private fun deleteProject() {

    }

    override fun setupForAsync() {
        refreshLayout.isRefreshing = true
    }

    fun asyncEnded() {
        refreshLayout.isRefreshing = false
    }

    override fun taskUpdated(task: Task) {
        asyncEnded()
        adapter!!.notifyDataSetChanged()
    }

    override fun taskCreated(task: Task) {
        asyncEnded()
        project?.tasks?.add(0, task)
        setupTasks()
    }

    override fun taskChangedOrder(task: Task) {
        setupForAsync()
        val request = EditTaskRequest(task,
                Response.Listener { task1 ->
                    taskUpdated(task1)
                }, { volleyError: VolleyError ->
            volleyError.printStackTrace()
        } as Response.ErrorListener)
        VolleyManager.queue?.add(request)
        adapter?.notifyDataSetChanged()
    }

    override fun onTaskClicked(task: Task) {
        editTask(task)
    }
}