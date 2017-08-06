package com.rndapp.task_feed.models

import android.content.Context

import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.rndapp.task_feed.api.ServerCommunicator
import org.json.JSONObject

import java.io.Serializable
import java.util.ArrayList
import java.util.Collections
import java.util.Date

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 1:39 PM

 */
class Project : Serializable {
    var id: Int = 0
    var localId: Int = 0
    var name: String? = null
    var created_at: Date? = null
    var updated_at: Date? = null
    var tasks = ArrayList<Task>()
    var color: Int = 0
    var isHidden = false

    constructor(name: String, color: Int) {
        this.name = name
        this.color = color
    }

    constructor() {}

    override fun toString(): String {
        return name ?: ""
    }

    val firstTaskText: String?
        get() {
            var output: String? = null
            for (task in tasks) {
                if (!task.isFinished) {
                    output = task.name
                    break
                }
            }
            return output
        }

    fun addTaskToBeginning(context: Context, queue: RequestQueue, task: Task) {
        task.project_id = this.id
        tasks.add(0, task)
        updatePositions(context, queue, {json: JSONObject ->

        }, Response.ErrorListener { })
    }

    fun addTaskRespectingOrder(context: Context, task: Task) {
        task.project_id = this.id
        tasks.add(0, task)
        sortTasks()
    }

    fun removeFirstTask(context: Context, queue: RequestQueue) {
        if (tasks.size != 0) {
            var indexOfTask = -1
            for (task in tasks) {
                if (!task.isFinished) {
                    indexOfTask = tasks.indexOf(task)
                    break
                }
            }
            if (indexOfTask != -1) markTaskAtPositionAsFinished(context, queue, indexOfTask)
        }
    }

    fun markTaskAtPositionAsFinished(context: Context, queue: RequestQueue, position: Int) {
        tasks[position] = Task.markAsFinished(context, queue, tasks[position])
        updatePositions(context, queue, null, null)
    }

    //    public void deleteTask(Context context, int position){
    //        TaskDataSource source = new TaskDataSource(context);
    //        source.open();
    //        source.deleteTask(tasks.get(position));
    //        source.close();
    //        tasks.remove(position);
    //        updatePositions(context);
    //    }

    fun updateTask(context: Context, queue: RequestQueue, task: Task,
                   listener: Response.Listener<*>,
                   errorListener: Response.ErrorListener) {
        if (task.order != tasks.indexOf(task)) {
            task.order = tasks.indexOf(task)
        }
        ServerCommunicator.updateTask(context,
                queue,
                task, listener, errorListener)
        updatePositions(context, queue, listener, errorListener)
    }

    private fun updatePositions(context: Context, queue: RequestQueue,
                                listener: Response.Listener<*>?,
                                errorListener: Response.ErrorListener?) {
        val tasksToUpdate = ArrayList<Task>()
        for (task in tasks) {
            if (task.order != tasks.indexOf(task)) {
                task.order = tasks.indexOf(task)
                tasksToUpdate.add(task)

                ServerCommunicator.updateTask(context,
                        queue,
                        task, listener!!, errorListener!!)
            }
        }
    }

    fun sortTasks() {
        Collections.sort(tasks)
    }

    fun getTask(position: Int): Task {
        return tasks[position]
    }

    val isEmpty: Boolean
        get() = firstTaskText == null || isHidden

    fun isUpToDateWithServerProject(serverProject: Task): Boolean {
        return this.updated_at.before(serverProject.updated_at)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val project = o as Project?

        if (id != project!!.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + localId
        return result
    }

    companion object {

        fun uploadProjectToServer(context: Context, queue: RequestQueue, project: Project,
                                  listener: Response.Listener<*>,
                                  errorListener: Response.ErrorListener) {

            ServerCommunicator.uploadProjectToServer(context, project, queue,
                    listener,
                    errorListener)
        }

        fun updateProjectOnServer(context: Context, queue: RequestQueue, project: Project,
                                  listener: Response.Listener<*>,
                                  errorListener: Response.ErrorListener) {
            ServerCommunicator.updateProjectOnServer(context,
                    project,
                    queue,
                    listener,
                    errorListener
            )
        }
    }
}
