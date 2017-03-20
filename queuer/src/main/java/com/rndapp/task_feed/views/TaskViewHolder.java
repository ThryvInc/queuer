package com.rndapp.task_feed.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rndapp.task_feed.R;
import com.rndapp.task_feed.listeners.OnTaskClickedListener;
import com.rndapp.task_feed.models.Task;

/**
 * Created by ell on 10/28/16.
 */

public class TaskViewHolder extends RecyclerView.ViewHolder {
    private TextView tv;
    private Task task;
    private OnTaskClickedListener listener;

    public TaskViewHolder(View itemView, OnTaskClickedListener listener) {
        super(itemView);

        this.listener = listener;
        tv = (TextView) itemView.findViewById(R.id.tv_task);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskViewHolder.this.listener.onTaskClicked(task);
            }
        });
    }

    public void setTask(Task task){
        this.task = task;
        tv.setText(task.getName());
    }
}
