package com.rndapp.task_feed.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.rndapp.task_feed.QueuerApplication;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.adapters.TaskListAdapter;
import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.interfaces.ProjectDisplayer;
import com.rndapp.task_feed.interfaces.TaskDisplayer;
import com.rndapp.task_feed.models.Project;
import com.rndapp.task_feed.models.Task;
import com.rndapp.task_feed.views.EnhancedListView;

import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 5/22/13
 * Time: 1:33 PM
 *
 */
public class ProjectFragment extends Fragment implements TaskDisplayer{
    public ProjectDisplayer delegate;
    private Project project;
    private TaskListAdapter adapter;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_PROJECT = "project";

    public ProjectFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        project = (Project)getArguments().getSerializable(ARG_PROJECT);

        View rootView = inflater.inflate(R.layout.task_list_item, container, false);
        rootView.setBackgroundColor(project.getColor());

        adapter = new TaskListAdapter(this, project.getTasks());

        EnhancedListView lv = (EnhancedListView)rootView.findViewById(R.id.task_list_view);
        lv.setAdapter(adapter);

        lv.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView listView, int position) {
                project.markTaskAtPositionAsFinished(getActivity(), ((QueuerApplication)getActivity().getApplication()).getRequestQueue(), adjustPosition(position));
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

        return rootView;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.project, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // set title
        alertDialogBuilder.setTitle("New Task");

        View layout = getActivity().getLayoutInflater().inflate(R.layout.new_task, null);

        final EditText taskTitle = (EditText)layout.findViewById(R.id.task);

        // set dialog message
        alertDialogBuilder
                //.setMessage(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)))
                .setCancelable(true)
                .setView(layout)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Task task = new Task();
                                task.setName(taskTitle.getText().toString());
                                task.setProject_id(project.getId());

                                ServerCommunicator.uploadTaskToServer(getActivity(), task,
                                        ((QueuerApplication)getActivity().getApplication()).getRequestQueue(),
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
                                        });
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // set title
        alertDialogBuilder.setTitle(getString(R.string.edit_task));

        View layout = getActivity().getLayoutInflater().inflate(R.layout.new_task, null);

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
                                task.setName(taskTitle.getText().toString());
                                project.updateTask(getActivity(),                                        ((QueuerApplication) getActivity().getApplication()).getRequestQueue(),
                                        task,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject o) {
                                                Task task1 = new Gson().fromJson(o.toString(), Task.class);
                                                Task.updateTask(getActivity(), task1);
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // set title
        alertDialogBuilder.setTitle(getString(R.string.action_edit_project));

        View layout = getActivity().getLayoutInflater().inflate(R.layout.new_project, null);

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
                                project.setName(projectTitle.getText().toString());
                                project.setColor(swatchColor);
                                Project.updateProjectOnServer(getActivity(),
                                        ((QueuerApplication) getActivity().getApplication()).getRequestQueue(),
                                        project,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject o) {
                                                project = new Gson().fromJson(o.toString(), Project.class);
                                                Project.updateProject(getActivity(), project);
                                                delegate.setupNav(null);
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

    private void hideProject(){
        project.setHidden(!project.isHidden());
        Project.updateProject(getActivity(), project);
        delegate.setupNav(null);
    }

    private void deleteProject(){

    }

    @Override
    public void setupForAsync() {
        delegate.setupForAsync();
    }

    @Override
    public void taskUpdated(Task task) {
        delegate.asyncEnded();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void taskCreated(Task task) {
        delegate.asyncEnded();
        project.addTaskToBeginning(getActivity(),
                ((QueuerApplication)getActivity().getApplication()).getRequestQueue(), task);
        adapter.notifyDataSetChanged();
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void taskChangedOrder(Task task) {
        project.updateTask(getActivity(),
                ((QueuerApplication)getActivity().getApplication()).getRequestQueue(),
                task,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject o) {
                        Task task1 = new Gson().fromJson(o.toString(), Task.class);
                        Task.updateTask(getActivity(), task1);
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
