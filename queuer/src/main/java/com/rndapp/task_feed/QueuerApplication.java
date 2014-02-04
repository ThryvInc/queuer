package com.rndapp.task_feed;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by eschrock on 2/4/14.
 */
public class QueuerApplication extends Application{
    private RequestQueue queue;

    public RequestQueue getRequestQueue(){
        if (queue == null) queue = Volley.newRequestQueue(this);
        return queue;
    }
}
