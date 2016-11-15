package com.rndapp.task_feed.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rndapp.task_feed.R;
import com.rndapp.task_feed.interfaces.RearrangementListener;
import com.rndapp.task_feed.interfaces.TaskDisplayer;
import com.rndapp.task_feed.listeners.OnTaskClickedListener;
import com.rndapp.task_feed.models.Task;
import com.rndapp.task_feed.views.TaskViewHolder;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/23/13
 * Time: 9:53 AM
 *
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskViewHolder> implements RearrangementListener {
    private Context context;
    private TaskDisplayer displayer;
    private OnTaskClickedListener listener;
    private ArrayList<Task> tasks;

    public TaskListAdapter(TaskDisplayer displayer, ArrayList<Task> tasks, OnTaskClickedListener listener){
        this.context = displayer.getContext();
        this.displayer = displayer;
        this.listener = listener;
        this.tasks = tasks;
        removeFinishedTasks();
    }

    public void removeFinishedTasks(){
        for (Task task : tasks){
            if (task.isFinished()){
                tasks.remove(task);
                removeFinishedTasks();
                break;
            }
        }
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.task_list_item, parent, false);

        return new TaskViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        holder.setTask(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

//    @Override
//    public void notifyDataSetChanged() {
//        super.notifyDataSetChanged();
//        removeFinishedTasks();
//    }

    @Override
    public void swapElements(int indexOne, int indexTwo) {
        Task temp1 = tasks.get(indexOne);
        Task temp2 = tasks.get(indexTwo);

        tasks.remove(indexOne);
        tasks.add(indexOne, temp2);

        tasks.remove(indexTwo);
        tasks.add(indexTwo, temp1);
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
