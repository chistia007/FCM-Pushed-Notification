package com.example.fcm;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    OnItemClickListener listener;
    private List<Task> tasks = new ArrayList<>();
    Cursor t;

    @SuppressLint("ResourceType")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
            Database db = new Database(context);

            Cursor t = db.getInfo("allTasks");
            if (t.getCount() == 0) {
                Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
            } else {
                tasks.clear(); // clear previous tasks
                while (t.moveToNext()) {
                    Task task = new Task(t.getLong(0), t.getString(1), t.getString(2), t.getString(3),false);
                    tasks.add(task);
                }

                TaskAdapter adapter = new TaskAdapter(tasks, listener);

                // Set the adapter on the ListView
                remoteViews.setRemoteAdapter(R.id.task_list, new Intent(context, NewAppWidgetService.class));

                // Set the empty view on the ListView
                remoteViews.setEmptyView(R.id.task_list, R.layout.empty_view);
            }

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

}