package com.rndapp.task_feed.services;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.broadcast_receivers.ListWidgetProvider;
import com.rndapp.task_feed.data.ProjectDataSource;
import com.rndapp.task_feed.data.TaskDataSource;
import com.rndapp.task_feed.models.Project;
import com.rndapp.task_feed.models.Task;

import java.util.ArrayList;

public class ProjectItemFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context = null;
    private int appWidgetId;
    private ArrayList<Project> projects;

    public ProjectItemFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    private void loadProjects() {
        //load projects
        ProjectDataSource projectDataSource = new ProjectDataSource(context);
        projectDataSource.open();
        projects = projectDataSource.getAllProjects();
        projectDataSource.close();

        //load tasks
        TaskDataSource taskDataSource = new TaskDataSource(context);
        taskDataSource.open();
        ArrayList<Task> tasks = taskDataSource.getAllTasks();
        taskDataSource.close();

        //associate
        for (Project project : projects) {
            for (Task task : tasks) {
                if (task.getProject_id() == project.getId()) {
                    project.getTasks().add(task);
                }
            }
        }

        //sort
        for (Project project : projects) {
            project.sortTasks();
        }
    }

    @Override public void onCreate() {}

    @Override public void onDestroy() {}

    @Override public int getCount() {return projects.size();}

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews item = new RemoteViews(context.getPackageName(), R.layout.list_widget_item);

        item.setTextViewText(R.id.list_widget_item, projects.get(position).getName() + ": "
                + projects.get(position).getFirstTaskText());

        Intent i = new Intent();
        Bundle bundle = new Bundle();

        bundle.putString(ListWidgetProvider.PROJECT_TASK, projects.get(position).getName() + ": "
                + projects.get(position).getFirstTaskText());
        i.putExtras(bundle);
        item.setOnClickFillInIntent(R.id.list_widget_item, i);

        return (item);
    }

    @Override public RemoteViews getLoadingView() {return null;}

    @Override public int getViewTypeCount() {return 1;}

    @Override public long getItemId(int position) {return position;}

    @Override public boolean hasStableIds() {return true;}

    @Override public void onDataSetChanged() {loadProjects();}
}