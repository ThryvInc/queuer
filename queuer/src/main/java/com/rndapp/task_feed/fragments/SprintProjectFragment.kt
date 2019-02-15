package com.rndapp.task_feed.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
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
import com.rndapp.task_feed.activities.ChooserActivity
import com.rndapp.task_feed.activities.ProjectActivity
import com.rndapp.task_feed.activities.SprintProjectActivity
import com.rndapp.task_feed.adapters.SprintProjectTaskAdapter
import com.rndapp.task_feed.adapters.TaskAdapter
import com.rndapp.task_feed.api.*
import com.rndapp.task_feed.interfaces.TaskDisplayer
import com.rndapp.task_feed.listeners.OnTaskClickedListener
import com.rndapp.task_feed.models.*
import com.rndapp.task_feed.view_models.SprintActivityViewModel
import com.rndapp.task_feed.views.PointsType
import com.rndapp.task_feed.views.PointsViewHolder
import kotlinx.android.synthetic.main.fragment_project.*
import kotlinx.android.synthetic.main.standard_recycler.*

class SprintProjectFragment: RecyclerViewFragment() {

    companion object {
        val TASK_REQUEST = 20
    }

    var viewModel: SprintActivityViewModel? = null
    var sprintProject: SprintProject? = null
        set(value) {
            field = value
            if (value != null) {
                setupTasks()
            }
        }
    private var adapter: SprintProjectTaskAdapter? = null

    var leftPointsHolder: PointsViewHolder? = null
    var rightPointsHolder: PointsViewHolder? = null

    private val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            val position = viewHolder.adapterPosition
            val task = adapter?.sprintProjectTasks?.get(position)?.task
            if (task != null) {
                val request = ToggleFinishedTaskRequest(task, Response.Listener {

                }, Response.ErrorListener { error ->
                    error.printStackTrace()
                })
                VolleyManager.queue?.add(request)
            }
            task?.isFinished = true
            adapter?.updateArray(adapter!!.sprintProjectTasks.filter { it.task != null }.filter { !(it.task?.isFinished ?: true) })
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

        val listener: (View) -> Unit = {
            leftPointsHolder?.toggleIsSelected()
            rightPointsHolder?.toggleIsSelected()

            setupTasks()
        }

        leftPoints.visibility = View.GONE
        leftPointsHolder = PointsViewHolder(leftPoints, PointsType.remaining)
        leftPointsHolder?.toggleIsSelected()
        leftPoints.setOnClickListener(listener)

        rightPoints.visibility = View.GONE
        rightPointsHolder = PointsViewHolder(rightPoints, PointsType.finished)
        rightPoints.setOnClickListener(listener)

        recyclerView?.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        rootView.findViewById<View>(R.id.fab).setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Add task")
                    .setMessage("What would you like to do?")
                    .setCancelable(true)
                    .setPositiveButton("Add Existing") { _, _ ->
                        chooseTask()
                    }
                    .setNegativeButton("Create New") { _, _ ->
                        createNewTask()
                    }
                    .setNeutralButton("Cancel") { _, _ -> }

            alertDialogBuilder.show()
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                TASK_REQUEST -> {
                    if (data != null) {
                        val tasks: List<Task> = data.getSerializableExtra(ChooserActivity.TASKS) as List<Task>
                        for (task in tasks) {
                            val taskId = task.id
                            val request = CreateSprintProjectTaskRequest(sprintProject?.id ?: 0,
                                    taskId,
                                    Response.Listener { response -> refresh() },
                                    Response.ErrorListener { error -> error.printStackTrace() }
                            )
                            VolleyManager.queue?.add(request)
                        }
                    }
                }
            }
        }
    }

    override fun refresh() {
        viewModel?.refreshSprintProjects()

        refreshLayout.isRefreshing = true
        val request = SprintProjectRequest(sprintProject?.id ?: 0, Response.Listener {
            this.sprintProject = it
            refreshLayout.isRefreshing = false
        }, Response.ErrorListener { error ->
            error.printStackTrace()
            refreshLayout.isRefreshing = false
        })
        VolleyManager.queue?.add(request)
    }

    fun chooseTask() {
        if (sprintProject?.project != null && sprintProject?.project is Project) {
            val intent = Intent(context, ChooserActivity::class.java)
            intent.putExtra(ChooserActivity.CHOOSER_TYPE, ChooserActivity.TASKS)
            intent.putExtra(ChooserActivity.PROJECT_ID, sprintProject?.project?.id ?: 0)
            startActivityForResult(intent, TASK_REQUEST)
        }

    }

    fun setupTasks() {
        leftPoints?.visibility = View.VISIBLE
        leftPoints?.findViewById<TextView>(R.id.pointsTextView)?.text =
                (sprintProject?.remainingPoints ?: 0).toString()

        rightPoints?.visibility = View.VISIBLE
        rightPoints?.findViewById<TextView>(R.id.pointsTextView)?.text =
                "${(sprintProject?.points ?: 0) - (sprintProject?.remainingPoints ?: 0)}"

        val tasks = if (leftPointsHolder?.isSelected != false) sprintProject?.unfinishedSprintTasks() else sprintProject?.finishedSprintTasks()
        if (tasks != null) {
            if (adapter != null) {
                adapter?.updateArray(ArrayList(tasks))
            } else {
                adapter = SprintProjectTaskAdapter(ArrayList(tasks), this::onSprintTaskClicked)
                recyclerView?.adapter = adapter
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.sprint_project, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_view_project -> viewProject()
            R.id.action_delete_project -> deleteProject()
        }
        return true
    }

    private fun viewProject() {
        val intent = Intent(context, ProjectActivity::class.java)
        intent.putExtra(ProjectActivity.ARG_PROJECT, sprintProject?.project)
        startActivity(intent)
    }

    private fun deleteProject() {
        VolleyManager.queue?.add(DeleteSprintProjectRequest(sprintProject?.id ?: 0,
                Response.Listener {
                    if (activity is SprintProjectActivity) activity?.finish()
                },
                Response.ErrorListener { it.printStackTrace() }))
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
                .setPositiveButton("Ok"
                ) { dialog, id ->
                    setupForAsync()

                    val task = Task()
                    task.name = taskTitle.text.toString()
                    task.projectId = sprintProject?.project?.id ?: 0
                    task.points = Integer.valueOf(taskPos.text.toString())

                    val request = CreateTaskRequest(task, Response.Listener { response ->
                        taskCreated(response)
                    }, Response.ErrorListener { error ->
                        error.printStackTrace()
                    })
                    VolleyManager.queue?.add(request)
                }
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
                .setPositiveButton("Ok") { dialog, id ->
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
                }
                .setNegativeButton("Cancel", { dialog, id -> })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun deleteSprintProjectTask(task: SprintProjectTask) {
        val request = DeleteSprintProjectTaskRequest(sprintProject?.id ?: 0, task.id ?: 0, Response.Listener { refresh() }, Response.ErrorListener { error ->
            error?.printStackTrace()
            refresh()
        })
        VolleyManager.queue?.add(request)
    }

    fun setupForAsync() {
        refreshLayout.isRefreshing = true
    }

    fun asyncEnded() {
        refreshLayout.isRefreshing = false
    }

    fun taskUpdated(task: Task) {
        asyncEnded()
        adapter!!.notifyDataSetChanged()
    }

    fun taskCreated(task: Task) {
        asyncEnded()
        val request = CreateSprintProjectTaskRequest(sprintProject?.id ?: 0,
                task.id,
                Response.Listener {
                    sprintProject?.project?.tasks?.add(0, task)
                    setupTasks()
                    refresh()
                },
                Response.ErrorListener { it.printStackTrace() })
        VolleyManager.queue?.add(request)
    }

    fun taskChangedOrder(task: Task) {
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

    fun onSprintTaskClicked(sprintTask: SprintProjectTask) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Task")
                .setMessage("What would you like to do?")
                .setCancelable(true)
                .setPositiveButton("Remove") { _, _ ->
                    deleteSprintProjectTask(sprintTask)
                }
                .setNegativeButton("Edit") { _, _ ->
                    val task = sprintTask.task
                    if (task != null) {
                        editTask(task)
                    }
                }
                .setNeutralButton("Cancel") { _, _ -> }

        alertDialogBuilder.show()
    }
}