package com.rndapp.task_feed.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rndapp.task_feed.R;
import com.rndapp.task_feed.listeners.OnProjectClickedListener;
import com.rndapp.task_feed.models.Project;

/**
 * Created by ell on 10/28/16.
 */

public class ProjectViewHolder extends RecyclerView.ViewHolder {
    private TextView tv;
    private Project project;
    private OnProjectClickedListener listener;

    public ProjectViewHolder(View itemView, OnProjectClickedListener listener) {
        super(itemView);

        this.listener = listener;
        tv = (TextView)itemView.findViewById(R.id.tv_project);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectViewHolder.this.listener.onProjectClicked(project);
            }
        });
    }

    public void setProject(Project project){
        this.project = project;
        tv.setText(project.getName() + ": " + project.getFirstTaskText());
        tv.setBackgroundColor(project.getColor());
    }
}
