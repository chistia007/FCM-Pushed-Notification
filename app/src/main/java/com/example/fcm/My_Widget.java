package com.example.fcm;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.SimpleCursorAdapter;

public class My_Widget extends AppWidgetProvider {
    Database db;
    Cursor cursor;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Log.d("TAG", "onUpdate: ramadan1111");
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my__widget);

            // Set the ListView adapter
            Intent intent = new Intent(context, MyWidgetService.class);
            views.setRemoteAdapter(R.id.list_view, intent);

            Intent intent0 = new Intent(context, ToDoActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent0, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.totalTodos, pendingIntent);

            db=new Database(context);
            cursor=db.getInfo("allTasks");
            int index = 0; // keep track of the current index
            while (cursor.moveToNext()) {
                index++;
            }
            views.setTextViewText(R.id.totalTodos,"Total " +index +" Todos");


            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.list_view);
        }
    }
}

