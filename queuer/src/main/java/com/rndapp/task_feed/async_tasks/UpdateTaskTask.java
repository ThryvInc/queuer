package com.rndapp.task_feed.async_tasks;

import android.content.Context;
import android.os.AsyncTask;
import com.rndapp.task_feed.interfaces.TaskDisplayer;
import com.rndapp.task_feed.models.Task;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 7/16/13
 * Time: 3:35 PM
 */

public class UpdateTaskTask extends AsyncTask<Task, String, Task> {
    private TaskDisplayer delegate;
    private Context context;

    public UpdateTaskTask(Context context, TaskDisplayer delegate) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (delegate != null) delegate.setupForAsync();
    }

    //param[0] = the task to upload
    @Override
    protected Task doInBackground(Task... params) {
        Task task = params[0];
        Task.updateTaskOnServer(context, task);
        return task;
    }

    @Override
    protected void onPostExecute(Task task){
        if (delegate != null) delegate.taskUpdated(task);
    }
}
