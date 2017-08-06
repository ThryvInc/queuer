package com.rndapp.task_feed.services

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService

import com.rndapp.task_feed.R
import com.rndapp.task_feed.broadcast_receivers.ListWidgetProvider
import com.rndapp.task_feed.models.Project

import java.util.ArrayList

class ProjectItemFactory(context: Context, intent: Intent) : RemoteViewsService.RemoteViewsFactory {
    private var context: Context
    private val appWidgetId: Int
    private val projects: ArrayList<Project>? = null

    init {
        this.context = context
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
    }

    override fun onCreate() {}

    override fun onDestroy() {}

    override fun getCount(): Int {
        return projects?.size ?: 0
    }

    override fun getViewAt(position: Int): RemoteViews {
        val item = RemoteViews(context!!.packageName, R.layout.list_widget_item)

        item.setTextViewText(R.id.list_widget_item, projects!![position].name + ": "
                + projects[position].firstTaskText)

        val i = Intent()
        val bundle = Bundle()

        bundle.putString(ListWidgetProvider.PROJECT_TASK, projects[position].name + ": "
                + projects[position].firstTaskText)
        i.putExtras(bundle)
        item.setOnClickFillInIntent(R.id.list_widget_item, i)

        return item
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun onDataSetChanged() {}
}