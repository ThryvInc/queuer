package com.rndapp.task_feed.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rndapp.task_feed.R
import com.rndapp.task_feed.interfaces.RearrangementListener
import com.rndapp.task_feed.interfaces.TaskDisplayer
import com.rndapp.task_feed.listeners.OnTaskClickedListener
import com.rndapp.task_feed.models.Task
import com.rndapp.task_feed.views.TaskViewHolder

import java.util.ArrayList

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/23/13
 * Time: 9:53 AM

 */
class TaskListAdapter(private val displayer: TaskDisplayer, private val tasks: ArrayList<Task>, private val listener: OnTaskClickedListener) : RecyclerView.Adapter<TaskViewHolder>(), RearrangementListener {
    private val context: Context

    init {
        this.context = displayer.getContext()
        removeFinishedTasks()
    }

    fun removeFinishedTasks() {
        for (task in tasks) {
            if (task.isFinished) {
                tasks.remove(task)
                removeFinishedTasks()
                break
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.task_list_item, parent, false)

        return TaskViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.setTask(tasks[position])
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    //    @Override
    //    public void notifyDataSetChanged() {
    //        super.notifyDataSetChanged();
    //        removeFinishedTasks();
    //    }

    override fun swapElements(indexOne: Int, indexTwo: Int) {
        val temp1 = tasks[indexOne]
        val temp2 = tasks[indexTwo]

        tasks.removeAt(indexOne)
        tasks.add(indexOne, temp2)

        tasks.removeAt(indexTwo)
        tasks.add(indexTwo, temp1)
    }

    //    @Override
    //    public void onFinishedRearranging() {
    //        for (int i = 0; i<tasks.size(); i++){
    //            Task task = tasks.get(i);
    //            if (task.getOrder() != i){
    //                displayer.taskChangedOrder(task);
    //            }
    //        }
    //    }
}
