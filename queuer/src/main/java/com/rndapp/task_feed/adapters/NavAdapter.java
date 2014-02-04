package com.rndapp.task_feed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rndapp.task_feed.R;
import com.rndapp.task_feed.models.Project;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Created by eschrock on 2/4/14.
 */
public class NavAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Project> projects;

    public NavAdapter(Context context, ArrayList<Project> projects) {
        this.context = context;
        this.projects = projects;
    }

    @Override
    public int getCount() {
        return projects.size();
    }

    @Override
    public Project getItem(int position) {
        return projects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return projects.get(position).getId();
    }

    @Nullable
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.task_list_item, null);
        }

        Project project = getItem(position);
        TextView tv = (TextView)convertView.findViewById(R.id.tv_task);
        tv.setText(project.getName());
        tv.setTextColor(project.getColor());
        return convertView;
    }
}
