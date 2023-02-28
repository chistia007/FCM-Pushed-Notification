package com.example.fcm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

public class NewAppWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TaskRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class TaskRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<Task> mTasks;

    public TaskRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        // Load data from the database and initialize mTasks
    }

    @Override
    public void onDataSetChanged() {
        // Reload data from the database and update mTasks
    }

    @Override
    public void onDestroy() {
        // Clean up resources
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // Create a RemoteViews object for the task at the given position
        @SuppressLint("RemoteViewLayout") RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.task_item);
        rv.setTextViewText(R.id.task_name, mTasks.get(position).getTitle());
        rv.setTextViewText(R.id.task_desc, mTasks.get(position).getDescription());
        rv.setTextViewText(R.id.due_time, mTasks.get(position).getDueDate());
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
