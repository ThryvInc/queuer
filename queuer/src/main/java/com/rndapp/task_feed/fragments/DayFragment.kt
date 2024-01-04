package com.rndapp.task_feed.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import com.android.volley.Response
import com.rndapp.task_feed.R
import com.rndapp.task_feed.activities.ChooserActivity
import com.rndapp.task_feed.adapters.DayTaskAdapter
import com.rndapp.task_feed.api.*
import com.rndapp.task_feed.listeners.OnDayTaskClickedListener
import com.rndapp.task_feed.models.*
import com.rndapp.task_feed.view_models.SprintActivityViewModel
import com.rndapp.task_feed.views.PointsType
import com.rndapp.task_feed.views.PointsViewHolder
import kotlinx.android.synthetic.main.fragment_day.*
import kotlinx.android.synthetic.main.standard_recycler.*

class DayFragment: RecyclerViewFragment(), OnDayTaskClickedListener {
    var viewModel: SprintActivityViewModel? = null
    var sprintId: Int? = null
    var day: Day? = null
        set(value) {
            field = value
            if (value != null) {
                leftPointsHolder?.setupPoints(value.points - value.finishedPoints)
                rightPointsHolder?.setupPoints(value.finishedPoints)

                setupTasks()
            }
        }
    var sprintProjects: List<SprintProject>? = null

    private var adapter: DayTaskAdapter? = null
    private val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
//            adapter?.swapElements(viewHolder.adapterPosition, target.adapterPosition)
//            adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            val position = viewHolder.adapterPosition
            val task = adapter?.dayTasks?.get(position)?.task
            if (task != null) {
                task.isFinished = !task.isFinished
                val request = ToggleFinishedTaskRequest(task, Response.Listener {
                    refresh()
                }, Response.ErrorListener { error ->
                    error.printStackTrace()
                    refresh()
                })
                VolleyManager.queue?.add(request)
                setupTasks()
            }
        }
    }

    var leftPointsHolder: PointsViewHolder? = null
    var rightPointsHolder: PointsViewHolder? = null

    companion object {
        val DAY_KEY = "DAY_KEY"
        val SPRINT_KEY = "SPRINT_KEY"
        val SPRINT_PROJECTS_KEY = "SPRINT_PROJECTS_KEY"

        val PROJECT_REQUEST = 10
        val TASK_REQUEST = 20
    }

    init {
        layoutId = R.layout.fragment_day
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

        val extras = activity?.intent?.extras
        if (extras != null) {
            val dayExtra = extras.getSerializable(DAY_KEY)
            if (dayExtra != null && dayExtra is Day) {
                day = dayExtra
            }
            val sprintIdExtra = extras.getInt(SPRINT_KEY, -1)
            if (sprintIdExtra != -1) {
                sprintId = sprintIdExtra
            } else {
                sprintId = viewModel!!.sprintId
            }
            if (viewModel == null) {
                viewModel = SprintActivityViewModel(sprintId!!)
            }
            val sprintProjectsExtra = extras.getSerializable(SPRINT_PROJECTS_KEY) as? List<SprintProject>
            if (sprintProjectsExtra != null) {
                sprintProjects = sprintProjectsExtra
            }
        }

        viewModel?.sprintProjectsLiveData?.observeForever {
            this.sprintProjects = it
        }

//        activity.supportActionBar?.title = day?.nameFromDate()

        rootView.findViewById<View>(R.id.fab)?.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Add task")
                    .setMessage("What would you like to do?")
                    .setCancelable(true)
                    .setPositiveButton("Add Existing") { _, _ ->
                        chooseProject()
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
                PROJECT_REQUEST -> {
                    val project = data?.getSerializableExtra(ChooserActivity.SPRINT_PROJECT)
                    if (project != null && project is SprintProject) {
                        val intent = Intent(context, ChooserActivity::class.java)
                        intent.putExtra(ChooserActivity.CHOOSER_TYPE, ChooserActivity.TASKS)
                        intent.putExtra(ChooserActivity.SPRINT_PROJECT_ID, project.id)
                        intent.putExtra(ChooserActivity.SPRINT_ID, sprintId)
                        startActivityForResult(intent, TASK_REQUEST)
                    }
                }
                TASK_REQUEST -> {
                    if (data != null) {
                        val tasks: List<Task> = data.getSerializableExtra(ChooserActivity.TASKS) as List<Task>
                        for (task in tasks) {
                            createDayTask(task)
                        }
                    }
                }
            }
        }
    }

    override fun refresh() {
        if (day != null) {
            viewModel?.refreshDays()
            viewModel?.refreshSprintProjects()

            val sprintId = sprintId
            if (sprintId != null) {
                refreshLayout?.isRefreshing = true
                val request = DayRequest(sprintId, day!!, Response.Listener { day ->
                    if (day != null) {
                        this.day = day
                    }
                    refreshLayout?.isRefreshing = false
                }, Response.ErrorListener { error ->
                    error.printStackTrace()
                    refreshLayout?.isRefreshing = false
                })
                VolleyManager.queue?.add(request)
            }
        }
    }

    fun setupTasks() {
        if (adapter != null) {
            val dayTasks = ArrayList(day?.dayTasks?.filter { leftPointsHolder?.isSelected == !(it.task?.isFinished ?: true) } ?: ArrayList())
            adapter?.dayTasks = dayTasks
            adapter?.updateArray(dayTasks)
        } else {
            adapter = DayTaskAdapter(ArrayList(day?.dayTasks?.filter { leftPointsHolder?.isSelected == !(it.task?.isFinished ?: true) } ?: ArrayList()), this )
            recyclerView?.adapter = adapter
        }
    }

    fun createNewTask() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("New Task")

        val layout = activity?.layoutInflater?.inflate(R.layout.new_day_task, null) ?: return

        val taskTitle = layout.findViewById<EditText>(R.id.taskNameEditText)
        val taskPos = layout.findViewById<EditText>(R.id.pointsEditText)

        val projectSpinner = layout.findViewById<Spinner>(R.id.projectSpinner)
        val list = viewModel?.sprintProjectsLiveData?.value?.map { it.project?.name ?: "" }
        if (list != null) {
            projectSpinner.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, list)
        }

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok"
                ) { dialog, id ->
                    val sprintProject = viewModel?.sprintProjectsLiveData?.value?.filter { it.project?.name == list?.get(projectSpinner.selectedItemPosition) }?.firstOrNull()

                    val task = Task()
                    task.name = taskTitle.text.toString()
                    task.projectId = sprintProject?.project?.id ?: 0
                    task.points = Integer.valueOf(taskPos.text.toString())

                    refreshLayout?.isRefreshing = true
                    val request = CreateTaskRequest(task, Response.Listener { response ->
                        taskCreated(response, sprintProject!!)
                    }, Response.ErrorListener { error ->
                        error.printStackTrace()
                    })
                    VolleyManager.queue?.add(request)
                }
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun taskCreated(task: Task, sprintProject: SprintProject) {
        val request = CreateSprintProjectTaskRequest(sprintProject?.id ?: 0,
                task.id,
                Response.Listener {
                    createDayTask(task)
                },
                Response.ErrorListener { it.printStackTrace() })
        VolleyManager.queue?.add(request)
    }

    fun createDayTask(task: Task) {
        val taskId = task.id
        val sprintId = sprintId
        if (sprintId != null) {
            val request = CreateDayTaskRequest(sprintId, day!!.id, taskId, Response.Listener { response ->
                refresh()
            }, Response.ErrorListener { error ->
                error.printStackTrace()
            })
            VolleyManager.queue?.add(request)
        }
    }

    fun chooseProject() {
        val sprintProjects = sprintProjects as? ArrayList
        if (sprintProjects != null) {
            val intent = Intent(context, ChooserActivity::class.java)
            intent.putExtra(ChooserActivity.CHOOSER_TYPE, ChooserActivity.ARRAY)
            intent.putExtra(ChooserActivity.ARRAY, sprintProjects)
            startActivityForResult(intent, PROJECT_REQUEST)
        }
    }

    override fun onDayTaskClicked(dayTask: DayTask) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Task")
                .setMessage("What would you like to do?")
                .setCancelable(true)
                .setPositiveButton("Remove") { _, _ ->
                    deleteTask(dayTask)
                }
                .setNegativeButton("Edit") { _, _ ->
                    val task = dayTask.task
                    if (task != null) {
                        editTask(task)
                    }
                }
                .setNeutralButton("Cancel") { _, _ -> }

        alertDialogBuilder.show()
    }

    fun deleteTask(dayTask: DayTask) {
        val sprintId = sprintId
        val day = day
        if (day != null && sprintId != null) {
            val request = DeleteDayTaskRequest(sprintId, day.id, dayTask.id, Response.Listener {
                refresh()
            }, Response.ErrorListener { error ->
                error?.printStackTrace()
                refresh()
            })
            VolleyManager.queue?.add(request)
        }
    }

    fun editTask(task: Task) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        // set title
        alertDialogBuilder.setTitle(getString(R.string.edit_task))

        val layout = activity?.layoutInflater?.inflate(R.layout.new_task, null)
        if (layout == null) return

        val taskTitle = layout.findViewById<EditText>(R.id.taskNameEditText)
        val taskPos = layout.findViewById<EditText>(R.id.pointsEditText)

        //populate text fields
        taskTitle.setText(task.name)
        taskPos.setText(task.points.toString())

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok") { dialog, id ->
                    task.name = taskTitle.text.toString()
                    task.points = Integer.valueOf(taskPos.text.toString())
                    val request = EditTaskRequest(task, Response.Listener
                    { task1 ->
                        refresh()
                    },
                            Response.ErrorListener { error ->
                                error.printStackTrace()
                            })
                    VolleyManager.queue?.add(request)
                }
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
