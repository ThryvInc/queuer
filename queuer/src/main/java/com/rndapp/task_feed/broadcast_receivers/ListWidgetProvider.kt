package com.rndapp.task_feed.broadcast_receivers

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.rndapp.task_feed.R
import com.rndapp.task_feed.activities.FeedActivity
import com.rndapp.task_feed.services.ListWidgetService

class ListWidgetProvider : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "update_widget") {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context,
                    ListWidgetProvider::class.java))
            appWidgetManager
                    .notifyAppWidgetViewDataChanged(ids, R.id.widget_list)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager,
                          appWidgetIds: IntArray) {
        for (i in appWidgetIds.indices) {
            val intent = Intent(context, ListWidgetService::class.java)

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i])
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

            val widget = RemoteViews(context.packageName,
                    R.layout.list_widget)

            widget.setRemoteAdapter(appWidgetIds[i], R.id.widget_list, intent)

            val openIntent = Intent(context, FeedActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            widget.setPendingIntentTemplate(R.id.widget_list, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetIds[i], widget)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        val PROJECT_TASK = "project_task"
    }
}