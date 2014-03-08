package com.rndapp.task_feed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.interfaces.RearrangementListener;
import com.rndapp.task_feed.models.Project;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/23/13
 * Time: 9:53 AM
 *
 */
public class ProjectListAdapter extends BaseAdapter implements RearrangementListener{
    public static final int INVALID_ID = -1;
    private Context context;
    private ArrayList<Project> projects;

    public ProjectListAdapter(Context context, ArrayList<Project> projects){
        this.context = context;
        this.projects = (ArrayList<Project>) projects.clone();

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
    public int getCount() {
        return projects.size();
    }

    @Override
    public Project getItem(int position) {
        return projects.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position < projects.size() || position >= projects.size())
            return INVALID_ID;
        return projects.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.project_list_item, null);
        }

        Project project = getItem(position);
        TextView tv = (TextView)convertView.findViewById(R.id.tv_project);
        tv.setText(project.getName() + ": " + project.getFirstTaskText());
        tv.setBackgroundColor(project.getColor());
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        removeEmptyProjects();
    }

    @Override
    public void onStartedRearranging() {

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

    @Override
    public void onFinishedRearranging() {

    }

    public void remove(int position) {

    }

    public void insert(int position, Project item) {

    }
}
