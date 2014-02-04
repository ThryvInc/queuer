package com.rndapp.task_feed.broadcast_receivers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.activities.FeedActivity;
import com.rndapp.task_feed.services.ListWidgetService;

public class ListWidgetProvider extends AppWidgetProvider {
    public static final String PROJECT_TASK = "project_task";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals("update_widget")) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                    ListWidgetProvider.class));
            appWidgetManager
                    .notifyAppWidgetViewDataChanged(ids, R.id.widget_list);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            Intent intent = new Intent(context, ListWidgetService.class);

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(),
                    R.layout.list_widget);

            widget.setRemoteAdapter(appWidgetIds[i], R.id.widget_list, intent);

            Intent openIntent = new Intent(context, FeedActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            widget.setPendingIntentTemplate(R.id.widget_list, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}