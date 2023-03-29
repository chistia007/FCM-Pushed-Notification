package com.example.fcm;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

public class MyWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private Cursor cursor;

    public MyWidgetFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        // Initialize the cursor
        cursor = null;
    }

    @Override
    public void onDataSetChanged() {
        // Fetch data from database
        Database database = new Database(context);
        cursor = database.getInfo("allTasks");
    }

    @Override
    public void onDestroy() {
        // Close the cursor
        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public int getCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    @SuppressLint("Range")
    @Override
    public RemoteViews getViewAt(int position) {
        // Move the cursor to the correct position
        cursor.moveToPosition(position);

        // Get the data from the cursor
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String description = cursor.getString(cursor.getColumnIndex("description"));
        String dueDate = cursor.getString(cursor.getColumnIndex("dueDate"));

        // Create a new RemoteViews object for the item view
        @SuppressLint("RemoteViewLayout") RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.task_item);


        // Update the views with data
        views.setTextViewText(R.id.task_name, title);
        views.setTextViewText(R.id.task_desc, description);
        views.setTextViewText(R.id.due_time, dueDate);




//        // Create an Intent to launch the TaskDetailActivity when the item is clicked
//        Intent intent = new Intent(context, ToDoActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        //intent.putExtra("ToDoActivity.EXTRA_TASK_ID", cursor.getInt(cursor.getColumnIndex("_id")));
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
//        views.setOnClickPendingIntent(R.id.item, pendingIntent);


        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        // Create a loading view for the widget
        return new RemoteViews(context.getPackageName(), R.layout.widget_loading);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @SuppressLint("Range")
    @Override
    public long getItemId(int position) {
        // Return the id of the item at the given position
        if (cursor != null) {
            cursor.moveToPosition(position);
            return cursor.getInt(cursor.getColumnIndex("_id"));
        }
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}