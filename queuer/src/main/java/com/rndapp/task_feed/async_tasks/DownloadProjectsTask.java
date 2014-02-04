package com.rndapp.task_feed.async_tasks;

import android.content.Context;
import android.os.AsyncTask;
import com.rndapp.task_feed.interfaces.ProjectDisplayer;
import com.rndapp.task_feed.models.ActivityUtils;
import com.rndapp.task_feed.models.Project;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 7/17/13
 * Time: 10:24 AM
 */

public class DownloadProjectsTask extends AsyncTask<ArrayList<Project>, Object, ArrayList<Project>> {
    private Context context;
    private ProjectDisplayer delegate;

    public DownloadProjectsTask(Context context, ProjectDisplayer delegate) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        delegate.setupForAsync();
    }

    @Override
    protected ArrayList<Project> doInBackground(ArrayList<Project>... params) {
        return ActivityUtils.downloadProjectsFromServer(context, params[0]);
    }

    @Override
    protected void onPostExecute(ArrayList<Project> projectArrayList){
        delegate.setupNav(projectArrayList);
    }
}
