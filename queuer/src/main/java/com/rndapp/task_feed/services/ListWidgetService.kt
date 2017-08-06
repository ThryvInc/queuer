package com.rndapp.task_feed.services

import android.content.Intent
import android.widget.RemoteViewsService

class ListWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
        return ProjectItemFactory(this.applicationContext, intent)
    }
}