package com.rndapp.task_feed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.async_tasks.UpdateTaskTask;
import com.rndapp.task_feed.interfaces.RearrangementListener;
import com.rndapp.task_feed.interfaces.TaskDisplayer;
import com.rndapp.task_feed.models.Task;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/23/13
 * Time: 9:53 AM
 *
 */
public class TaskListAdapter extends BaseAdapter implements RearrangementListener {
    public static final int INVALID_ID = -1;
    private Context context;
    private TaskDisplayer displayer;
    private ArrayList<Task> tasks;

    public TaskListAdapter(TaskDisplayer displayer, ArrayList<Task> tasks){
        this.context = displayer.getContext();
        this.displayer = displayer;
        this.tasks = tasks;
        removeFinishedTasks();
    }

    @Override
    public Task getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= tasks.size()) {
            return INVALID_ID;
        }
        return getItem(position).getId();
    }

    @Override
    public int getCount(){
        return tasks.size();
    }

    private void removeFinishedTasks(){
        for (Task task : tasks){
            if (task.isFinished()){
                tasks.remove(task);
                removeFinishedTasks();
                break;
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.task_list_item, null);
        }

        Task task = getItem(position);
        ((TextView)convertView.findViewById(R.id.tv_task)).setText(task.getName());
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        removeFinishedTasks();
    }

    @Override
    public void onStartedRearranging() {

    }

    @Override
    public void swapElements(int indexOne, int indexTwo) {
        Task temp1 = tasks.get(indexOne);
        Task temp2 = tasks.get(indexTwo);

        tasks.remove(indexOne);
        tasks.add(indexOne, temp2);

        tasks.remove(indexTwo);
        tasks.add(indexTwo, temp1);
    }

    @Override
    public void onFinishedRearranging() {
        for (int i = 0; i<tasks.size(); i++){
            Task task = tasks.get(i);
            if (task.getOrder() != i){
                displayer.taskChangedOrder(task);
            }
        }
    }
}
