package com.rndapp.task_feed.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.*
import android.widget.Button
import android.widget.EditText
import com.android.volley.Response
import com.android.volley.VolleyError
import com.rndapp.task_feed.R
import com.rndapp.task_feed.adapters.TaskAdapter
import com.rndapp.task_feed.api.*
import com.rndapp.task_feed.interfaces.TaskDisplayer
import com.rndapp.task_feed.listeners.OnTaskClickedListener
import com.rndapp.task_feed.models.Project
import com.rndapp.task_feed.models.Task

class ProjectFragment: Fragment(), TaskDisplayer, OnTaskClickedListener {
    var project: Project? = null
        set(value) {
            field = value
            if (value != null) {
                setupTasks()
            }
        }
    private var adapter: TaskAdapter? = null

    private var recyclerView: RecyclerView? = null

    private val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            val position = viewHolder.adapterPosition
            val task = adapter?.filteredTasks?.get(position)
            if (task != null) {
                val request = FinishTaskRequest(task, Response.Listener {

                }, Response.ErrorListener { error ->
                    error.printStackTrace()
                })
                VolleyManager.queue?.add(request)
            }
            task?.isFinished = true
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_project, container, false)

        val manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView = rootView.findViewById<RecyclerView>(R.id.task_list_view)
        recyclerView?.layoutManager = manager

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        refresh()

        return rootView
    }

    fun refresh() {
        val request = ProjectRequest(project?.id ?: 0, Response.Listener { project ->
            this.project = project
        }, Response.ErrorListener { error ->
            error.printStackTrace()
        })
        VolleyManager.queue?.add(request)
    }

    fun setupTasks() {
        adapter = TaskAdapter(project?.tasks ?: ArrayList(), this)
        recyclerView?.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.project, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == getString(R.string.action_add_task)) {
            //new task
            createNewTask()
        } else if (item.itemId == R.id.action_edit_project) {
            //edit project
            editProject()
        } else if (item.itemId == R.id.action_delete_project) {
            //edit project
            deleteProject()
        }
        return true
    }

    private fun createNewTask() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        // set title
        alertDialogBuilder.setTitle("New Task")

        val layout = activity.layoutInflater.inflate(R.layout.new_task, null)

        val taskTitle = layout.findViewById<EditText>(R.id.task)

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

        val layout = activity.layoutInflater.inflate(R.layout.new_task, null)

        val taskTitle = layout.findViewById<EditText>(R.id.task)
        val taskPos = layout.findViewById<EditText>(R.id.position)

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
                            val request = EditTaskRequest(task, Response.Listener
                            { task1 ->
                                taskUpdated(task1)
                            },
                                    Response.ErrorListener { error ->
                                        error.printStackTrace()
                                    })
                        })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    internal var swatchColor: Int = 0
    private fun editProject() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        // set title
        alertDialogBuilder.setTitle(getString(R.string.action_edit_project))

        val layout = activity.layoutInflater.inflate(R.layout.new_project, null)

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
                            val request = EditProjectRequest(project!!,
                                    Response.Listener { project ->
                                        asyncEnded()
                                    },
                                    { volleyError: VolleyError ->
                                        volleyError.printStackTrace()
                                        asyncEnded()
                                    } as Response.ErrorListener)
                            VolleyManager.queue?.add(request)
                        })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteProject() {

    }

    override fun setupForAsync() {
        view?.findViewById<View>(R.id.proj_loading_bar)?.visibility = View.VISIBLE
    }

    fun asyncEnded() {
        view?.findViewById<View>(R.id.proj_loading_bar)?.visibility = View.GONE
    }

    override fun taskUpdated(task: Task) {
        asyncEnded()
        adapter!!.notifyDataSetChanged()
    }

    override fun taskCreated(task: Task) {
        asyncEnded()
        project?.tasks?.add(0, task)
        adapter?.notifyDataSetChanged()
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