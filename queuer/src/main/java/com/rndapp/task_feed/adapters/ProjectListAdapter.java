package com.rndapp.task_feed.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rndapp.task_feed.R;
import com.rndapp.task_feed.interfaces.RearrangementListener;
import com.rndapp.task_feed.listeners.OnProjectClickedListener;
import com.rndapp.task_feed.models.Project;
import com.rndapp.task_feed.views.ProjectViewHolder;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/23/13
 * Time: 9:53 AM
 *
 */
public class ProjectListAdapter extends RecyclerView.Adapter<ProjectViewHolder> implements RearrangementListener{
    private ArrayList<Project> projects;
    private OnProjectClickedListener listener;

    public ProjectListAdapter(ArrayList<Project> projects, OnProjectClickedListener listener){
        this.projects = (ArrayList<Project>) projects.clone();
        this.listener = listener;

        removeEmptyProjects();
    }

    public void setProjects(ArrayList<Project> projects){
        this.projects = (ArrayList<Project>) projects.clone();
        removeEmptyProjects();
        notifyDataSetChanged();
    }

    public void removeEmptyProjects(){
        for (Project project : projects){
            if (project.isEmpty() || project.isHidden()){
                projects.remove(project);
                removeEmptyProjects();
                break;
            }
        }
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.project_list_item, parent, false);
        return new ProjectViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder holder, int position) {
        holder.setProject(projects.get(position));
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    @Override
    public void swapElements(int indexOne, int indexTwo) {
        Project temp1 = projects.get(indexOne);
        Project temp2 = projects.get(indexTwo);

        projects.remove(indexOne);
        projects.add(indexOne, temp2);

        projects.remove(indexTwo);
        projects.add(indexTwo, temp1);
    }
}
