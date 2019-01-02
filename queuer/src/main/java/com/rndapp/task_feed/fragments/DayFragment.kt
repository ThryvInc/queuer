package com.rndapp.task_feed.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.VolleyError
import com.rndapp.task_feed.R
import com.rndapp.task_feed.activities.ChooserActivity
import com.rndapp.task_feed.adapters.DayTaskAdapter
import com.rndapp.task_feed.api.*
import com.rndapp.task_feed.listeners.OnDayTaskClickedListener
import com.rndapp.task_feed.models.*
import kotlinx.android.synthetic.main.fragment_day.*
import kotlinx.android.synthetic.main.standard_recycler.*

class DayFragment: RecyclerViewFragment(), OnDayTaskClickedListener {
    var sprint: Sprint? = null
    var day: Day? = null
        set(value) {
            field = value
            if (value != null) {
                leftPoints.visibility = View.VISIBLE
                leftPoints.findViewById<TextView>(R.id.pointsTextView).text =
                        "${value.points - value.finishedPoints}"

                rightPoints.visibility = View.VISIBLE
                rightPoints.findViewById<TextView>(R.id.pointsTextView).text =
                        value.finishedPoints.toString()

                if (adapter != null) {
                    val dayTasks = ArrayList(value.dayTasks?.filter { !(it.task?.isFinished ?: true) } ?: ArrayList())
                    adapter?.dayTasks = dayTasks
                    adapter?.updateArray(dayTasks)
                } else {
                    adapter = DayTaskAdapter(ArrayList(value.dayTasks?.filter { !(it.task?.isFinished ?: true) } ?: ArrayList()), this )
                    recyclerView?.adapter = adapter
                }
            }
        }
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
                val request = FinishTaskRequest(task, Response.Listener {
                    refresh()
                }, Response.ErrorListener { error ->
                    error.printStackTrace()
                    refresh()
                })
                VolleyManager.queue?.add(request)
            }
            task?.isFinished = true
            adapter?.dayTasks = ArrayList(adapter!!.dayTasks.filter {
                !(it.task?.isFinished ?: true)
            })
            adapter?.updateArray(adapter?.dayTasks ?: ArrayList())
        }
    }

    companion object {
        val DAY_KEY = "DAY_KEY"
        val SPRINT_KEY = "SPRINT_KEY"

        val PROJECT_REQUEST = 10
        val TASK_REQUEST = 20
    }

    init {
        layoutId = R.layout.fragment_day
    }

    override fun onViewCreated(rootView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)

        leftPoints.visibility = View.GONE
        rightPoints.visibility = View.GONE
        leftPoints.findViewById<TextView>(R.id.ptTypeTextView).text = "remaining"

        recyclerView?.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val extras = activity?.intent?.extras
        if (extras != null) {
            val dayExtra = extras.getSerializable(DAY_KEY)
            if (dayExtra != null && dayExtra is Day) {
                day = dayExtra
            }
            val sprintExtra = extras.getSerializable(SPRINT_KEY)
            if (sprintExtra != null && sprintExtra is Sprint) {
                sprint = sprintExtra
            }
        }

//        activity.supportActionBar?.title = day?.nameFromDate()

        rootView.findViewById<View>(R.id.fab)?.setOnClickListener {
            chooseProject()
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
                    val project = data?.getSerializableExtra(ChooserActivity.PROJECT)
                    if (project != null && project is Project) {
                        val intent = Intent(context, ChooserActivity::class.java)
                        intent.putExtra(ChooserActivity.CHOOSER_TYPE, ChooserActivity.TASKS)
                        intent.putExtra(ChooserActivity.PROJECT_ID, project.id)
                        startActivityForResult(intent, TASK_REQUEST)
                    }
                }
                TASK_REQUEST -> {
                    if (data != null) {
                        val tasks: List<Task> = data.getSerializableExtra(ChooserActivity.TASKS) as List<Task>
                        for (task in tasks) {
                            val taskId = task.id
                            val request = CreateDayTaskRequest(sprint?.id!!, day!!.id, taskId, Response.Listener { response ->
                                refresh()
                            }, Response.ErrorListener { error ->
                                error.printStackTrace()
                            })
                            VolleyManager.queue?.add(request)
                        }
                    }
                }
            }
        }
    }

    override fun refresh() {
        if (day != null) {
            refreshLayout.isRefreshing = true
            val request = DayRequest(sprint?.id!!, day!!, Response.Listener { day ->
                if (day != null) {
                    this.day = day
                }
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
        intent.putExtra(ChooserActivity.CHOOSER_TYPE, ChooserActivity.ARRAY)
        intent.putExtra(ChooserActivity.ARRAY, sprint?.projects)
        startActivityForResult(intent, PROJECT_REQUEST)
    }

    override fun onDayTaskClicked(dayTask: DayTask) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Task")
                .setMessage("What would you like to do?")
                .setCancelable(true)
                .setPositiveButton("Delete") { _, _ ->
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
        val sprint = sprint
        val day = day
        if (sprint != null && day != null) {
            val request = DeleteDayTaskRequest(sprint.id, day.id, dayTask.id, object: Response.Listener<DayTask?> {
                override fun onResponse(response: DayTask?) {
                    refresh()
                }
            }, object: Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    error?.printStackTrace()
                    refresh()
                }
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
