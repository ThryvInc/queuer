package com.rndapp.task_feed.activities;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rndapp.task_feed.QueuerApplication;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.adapters.NavAdapter;
import com.rndapp.task_feed.adapters.ProjectListAdapter;
import com.rndapp.task_feed.broadcast_receivers.ListWidgetProvider;
import com.rndapp.task_feed.interfaces.ProjectDisplayer;
import com.rndapp.task_feed.models.*;
import com.rndapp.task_feed.views.EnhancedListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends ActionBarActivity implements ProjectDisplayer {
    private static final String TAG = "FeedActivity";
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    public ArrayList<Project> projects;
    private ProjectListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);

        //load projects
        projects = ActivityUtils.loadProjectsFromDatabase(this);

        setupForAsync();
        ActivityUtils.downloadProjectsFromServer(this,
                ((QueuerApplication)getApplication()).getRequestQueue(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray json) {
                        Type listOfProjects = new TypeToken<List<Project>>() {}.getType();
                        ArrayList<Project> serverProjects = new Gson().fromJson(json.toString(), listOfProjects);
                        projects = ActivityUtils.syncProjectsWithServer(FeedActivity.this,
                                ((QueuerApplication)getApplication()).getRequestQueue(),
                                projects, serverProjects);
                        setupNav(projects);
                        asyncEnded();
                        adapter.setProjects(projects);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        asyncEnded();
                    }
                }
        );

        EnhancedListView lv = (EnhancedListView)findViewById(R.id.project_list_view);

        adapter = new ProjectListAdapter(this, projects);

        lv.setDismissCallback(new EnhancedListView.OnDismissCallback() {
            /**
             * This method will be called when the user swiped a way or deleted it via
             * {@link EnhancedListView#delete(int)}.
             *
             * @param listView The {@link EnhancedListView} the item has been deleted from.
             * @param position The position of the item to delete from your adapter.
             * @return An {@link EnhancedListView.Undoable}, if you want
             *      to give the user the possibility to undo the deletion.
             */
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView listView, final int position) {
                removeItemFromProject(adjustPosition(position));
                adapter.notifyDataSetChanged();
                return null;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startProjectActivity(projects.get(adjustPosition(position)));
            }
        });

        lv.enableSwipeToDismiss();

        lv.setAdapter(adapter);
	}

    @Override
    protected void onResume() {
        super.onResume();

        //load projects
        projects = ActivityUtils.loadProjectsFromDatabase(this);

        setupNav(null);

        adapter.setProjects(projects);
    }

    private void startProjectActivity(Project project){
        Intent intent = new Intent(FeedActivity.this, ProjectActivity.class);
        intent.putExtra(ProjectActivity.ARG_PROJECT, project);
        startActivity(intent);
    }

    private int adjustPosition(int position){
        int result = position;
        for (int i = 0; i < result+1; i++){
            if (projects.get(i).isEmpty() || projects.get(i).isHidden()){
                result++;
            }
        }
        return result;
    }

    public void removeItemFromProject(int position){
        projects.get(position).removeFirstTask(this, ((QueuerApplication)getApplication()).getRequestQueue());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()){
            case R.id.action_add_project:
                newProject();
                break;
            case R.id.action_logout:
                ActivityUtils.logout(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void setupForAsync(){
        findViewById(R.id.loading_bar).setVisibility(View.VISIBLE);
    }

    @Override
    public void asyncEnded() {
        findViewById(R.id.loading_bar).setVisibility(View.GONE);
    }

    public void setupNav(ArrayList<Project> projectArrayList){
        if (projectArrayList != null){
            projects = projectArrayList;
        }

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(R.string.app_name);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.app_name);
            }
        };

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerLayout.setDrawerListener(drawerToggle);

        // Set the adapter for the list view
        drawerList.setAdapter(new NavAdapter(this, projects));

        // Set the list's click listener
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(drawerList);
                startProjectActivity(projects.get(position));
            }
        });
    }

    int swatchColor;
    public void newProject(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("New Project");

        View layout = getLayoutInflater().inflate(R.layout.new_project, null);

        final EditText taskTitle = (EditText)layout.findViewById(R.id.projectName);

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
                                Project.uploadProjectToServer(FeedActivity.this,
                                        ((QueuerApplication) getApplication()).getRequestQueue(),
                                        new Project(taskTitle.getText().toString(), swatchColor),
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject o) {
                                                Project project = new Gson().fromJson(o.toString(), Project.class);
                                                project = Project.addProjectToDatabase(getApplicationContext(), project);
                                                projects.add(project);
                                                setupNav(projects);
                                                Intent intent = new Intent(FeedActivity.this, ProjectActivity.class);
                                                intent.putExtra(ProjectActivity.ARG_PROJECT, project);
                                                startActivity(intent);
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                //try again?
                                            }
                                        });
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {}
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent updateWidget = new Intent(this, ListWidgetProvider.class);
        updateWidget.setAction("update_widget");
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, updateWidget, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pending.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
