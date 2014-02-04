package com.rndapp.task_feed.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.*;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.async_tasks.DownloadProjectsTask;
import com.rndapp.task_feed.broadcast_receivers.ListWidgetProvider;
import com.rndapp.task_feed.fragments.FeedFragment;
import com.rndapp.task_feed.fragments.ProjectFragment;
import com.rndapp.task_feed.interfaces.ProjectDisplayer;
import com.rndapp.task_feed.models.*;

import java.util.ArrayList;

public class FeedActivity extends ActionBarActivity implements
		ActionBar.OnNavigationListener, ProjectDisplayer {
    private static final String TAG = "FeedActivity";
    private ArrayList<Project> projects;

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);

        //load projects
        projects = ActivityUtils.loadProjectsFromDatabase(this);

        setupNav(null);

        new DownloadProjectsTask(this, this).execute(projects);
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
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        //get titles
        String[] dropdownTitles = new String[projects.size()+1];

        dropdownTitles[0] = "Feed";
        for (int i = 0; i < projects.size(); i++){
            dropdownTitles[i+1] = projects.get(i).getName();
        }

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1, dropdownTitles), this);

        asyncEnded();

        getSupportActionBar().setSelectedNavigationItem(0);
    }

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
                .getSelectedNavigationIndex());
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
        if (position == 0){
            Fragment fragment1 = new FeedFragment();
            ((FeedFragment)fragment1).delegate = this;
            ((FeedFragment)fragment1).projects = projects;
            switchFragBack(fragment1);
        } else {
            Fragment fragment3 = new ProjectFragment();
            ((ProjectFragment)fragment3).delegate = this;
            Bundle args2 = new Bundle();
            args2.putSerializable(ProjectFragment.ARG_PROJECT, projects.get(position - 1));
            fragment3.setArguments(args2);
            switchFragBack(fragment3);
        }
        return true;
    }

    private void switchFragBack(Fragment frag){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, frag)
                .addToBackStack(frag.getClass().getCanonicalName()).commit();
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
