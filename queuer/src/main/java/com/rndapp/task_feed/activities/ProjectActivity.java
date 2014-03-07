package com.rndapp.task_feed.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.rndapp.task_feed.QueuerApplication;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.adapters.TaskListAdapter;
import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.interfaces.TaskDisplayer;
import com.rndapp.task_feed.models.Project;
import com.rndapp.task_feed.models.Task;
import com.rndapp.task_feed.views.EnhancedListView;

import org.json.JSONObject;

/**
 * Created by eschrock on 2/4/14.
 */
public class ProjectActivity extends ActionBarActivity implements TaskDisplayer {
    private Project project;
    private TaskListAdapter adapter;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_PROJECT = "project";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        project = (Project)getIntent().getExtras().getSerializable(ARG_PROJECT);

        getSupportActionBar().setTitle(project.getName());

        View rootView = findViewById(R.id.project_root_view);
        rootView.setBackgroundColor(project.getColor());

        adapter = new TaskListAdapter(this, project.getTasks());

        EnhancedListView lv = (EnhancedListView)findViewById(R.id.task_list_view);
        lv.setAdapter(adapter);

        lv.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView listView, int position) {
                project.markTaskAtPositionAsFinished(ProjectActivity.this, ((QueuerApplication)getApplication()).getRequestQueue(), adjustPosition(position));
                adapter.notifyDataSetChanged();
                return null;
            }
        });
        lv.enableSwipeToDismiss();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editTask(project.getTask(adjustPosition(position)));
            }
        });
    }

    private int adjustPosition(int position){
        int result = position;
        for (int i = 0; i < result+1; i++){
            if (project.getTasks().get(i).isFinished()){
                result++;
            }
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getTitle().equals(getString(R.string.action_add_task))){
            //new task
            createNewTask();
        } else if (item.getItemId() == R.id.action_edit_project){
            //edit project
            editProject();
        } else if (item.getItemId() == R.id.action_hide_project){
            //edit project
            hideProject();
        } else if (item.getItemId() == R.id.action_delete_project){
            //edit project
            deleteProject();
        }
        return true;
    }

    private void createNewTask(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("New Task");

        View layout = getLayoutInflater().inflate(R.layout.new_task, null);

        final EditText taskTitle = (EditText)layout.findViewById(R.id.task);

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                setupForAsync();

                                Task task = new Task();
                                task.setName(taskTitle.getText().toString());
                                task.setProject_id(project.getId());

                                ServerCommunicator.uploadTaskToServer(ProjectActivity.this, task,
                                        ((QueuerApplication) getApplication()).getRequestQueue(),
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject o) {
                                                Task task1 = new Gson().fromJson(o.toString(), Task.class);
                                                taskCreated(task1);
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                //try again?
                                            }
                                        }
                                );
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void editTask(final Task task){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle(getString(R.string.edit_task));

        View layout = getLayoutInflater().inflate(R.layout.new_task, null);

        final EditText taskTitle = (EditText)layout.findViewById(R.id.task);
        final EditText taskPos = (EditText)layout.findViewById(R.id.position);

        //populate text fields
        taskTitle.setText(task.getName());
        taskPos.setText(String.valueOf(task.getOrder()));

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                setupForAsync();
                                task.setName(taskTitle.getText().toString());
                                project.updateTask(ProjectActivity.this,
                                        ((QueuerApplication) getApplication()).getRequestQueue(),
                                        task,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject o) {
                                                Task task1 = new Gson().fromJson(o.toString(), Task.class);
                                                Task.updateTask(ProjectActivity.this, task1);
                                                taskUpdated(task1);
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                //try again?
                                            }
                                        }
                                );
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {}
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    int swatchColor;
    private void editProject(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle(getString(R.string.action_edit_project));

        View layout = getLayoutInflater().inflate(R.layout.new_project, null);

        final EditText projectTitle = (EditText)layout.findViewById(R.id.projectName);
        projectTitle.setText(project.getName());

        final View swatch = layout.findViewById(R.id.color_swatch);

        Button btnRed = (Button)layout.findViewById(R.id.btn_red);
        Button btnBlue = (Button)layout.findViewById(R.id.btn_blue);
        Button btnPlum = (Button)layout.findViewById(R.id.btn_plum);
        Button btnGold = (Button)layout.findViewById(R.id.btn_yellow);
        Button btnOrange = (Button)layout.findViewById(R.id.btn_orange);
        Button btnGreen = (Button)layout.findViewById(R.id.btn_green);
        Button btnTurquoise = (Button)layout.findViewById(R.id.btn_turquoise);

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_blue:
                        swatchColor = getResources().getColor(R.color.blue);
                        swatch.setBackgroundColor(getResources().getColor(R.color.blue));
                        break;
                    case R.id.btn_green:
                        swatchColor = getResources().getColor(R.color.green);
                        swatch.setBackgroundColor(getResources().getColor(R.color.green));
                        break;
                    case R.id.btn_orange:
                        swatchColor = getResources().getColor(R.color.orange);
                        swatch.setBackgroundColor(getResources().getColor(R.color.orange));
                        break;
                    case R.id.btn_plum:
                        swatchColor = getResources().getColor(R.color.plum);
                        swatch.setBackgroundColor(getResources().getColor(R.color.plum));
                        break;
                    case R.id.btn_red:
                        swatchColor = getResources().getColor(R.color.red);
                        swatch.setBackgroundColor(getResources().getColor(R.color.red));
                        break;
                    case R.id.btn_yellow:
                        swatchColor = getResources().getColor(R.color.yellow);
                        swatch.setBackgroundColor(getResources().getColor(R.color.yellow));
                        break;
                    case R.id.btn_turquoise:
                        swatchColor = getResources().getColor(R.color.turquoise);
                        swatch.setBackgroundColor(getResources().getColor(R.color.turquoise));
                        break;
                }
            }
        };

        btnRed.setOnClickListener(listener);
        btnBlue.setOnClickListener(listener);
        btnOrange.setOnClickListener(listener);
        btnGreen.setOnClickListener(listener);
        btnGold.setOnClickListener(listener);
        btnPlum.setOnClickListener(listener);
        btnTurquoise.setOnClickListener(listener);

        swatchColor = getResources().getColor(R.color.goldenrod);

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                setupForAsync();
                                project.setName(projectTitle.getText().toString());
                                project.setColor(swatchColor);
                                Project.updateProjectOnServer(ProjectActivity.this,
                                        ((QueuerApplication) getApplication()).getRequestQueue(),
                                        project,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject o) {
                                                project = new Gson().fromJson(o.toString(), Project.class);
                                                Project.updateProject(ProjectActivity.this, project);
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                //try again?
                                            }
                                        }
                                );
                                asyncEnded();
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {}
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void hideProject(){
        project.setHidden(!project.isHidden());
        Project.updateProject(this, project);
        finish();
    }

    private void deleteProject(){

    }

    @Override
    public void setupForAsync() {
        findViewById(R.id.proj_loading_bar).setVisibility(View.VISIBLE);
    }

    public void asyncEnded() {
        findViewById(R.id.proj_loading_bar).setVisibility(View.GONE);
    }

    @Override
    public void taskUpdated(Task task) {
        asyncEnded();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void taskCreated(Task task) {
        asyncEnded();
        project.addTaskToBeginning(this,
                ((QueuerApplication)getApplication()).getRequestQueue(), task);
        adapter.notifyDataSetChanged();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void taskChangedOrder(Task task) {
        setupForAsync();
        project.updateTask(this,
                ((QueuerApplication)getApplication()).getRequestQueue(),
                task,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject o) {
                        Task task1 = new Gson().fromJson(o.toString(), Task.class);
                        Task.updateTask(ProjectActivity.this, task1);
                        taskUpdated(task1);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //try again?
                    }
                });
        adapter.notifyDataSetChanged();
    }
}
