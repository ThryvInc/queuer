package com.rndapp.task_feed.services;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class ListWidgetService extends RemoteViewsService {
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return new ProjectItemFactory(this.getApplicationContext(), intent);
  }
}