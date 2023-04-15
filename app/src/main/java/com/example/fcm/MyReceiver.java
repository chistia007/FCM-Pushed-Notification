package com.example.fcm;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fcm.databinding.ActivityToDoBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyReceiver extends BroadcastReceiver {
    String title;
    Database db;
    Cursor t;

    String currentDate1;
    String nextDate;
    Date currentDate;
    int index;

    @SuppressLint("Range")
    public void onReceive(Context context, Intent intent) {
        db=new Database(context);
        ///fixed notification
        ArrayList<String> dates = new ArrayList<>();
        t = db.getInfo("allTasks");
        while (t.moveToNext()) {
            if (!t.getString(3).equals("")) {
                dates.add(t.getString(3));
            }
        }
        Collections.sort(dates);
        currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        currentDate1 = dateFormat.format(currentDate);

        // Find the index of the next upcoming date
        index = -1;
        for (int i = 0; i < dates.size(); i++) {
            if (dates.get(i).compareTo(currentDate1) > 0) {
                index = i;
                break;
            }
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel for Android Oreo and higher
            NotificationChannel channel = new NotificationChannel("channel_id", "CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("CHANNEL_DESCRIPTION");
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }

        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Boolean  alwaysOnNotificationValue= prefs.getBoolean("alwaysOnNotificationValue", false);
        // Create a notification with the details of the next upcoming date
        if (index != -1 && alwaysOnNotificationValue) {
            nextDate = dates.get(index);
            SQLiteDatabase sqLiteDatabase=this.db.getReadableDatabase();
            String[] columns = {"title"};
            String selection = "dueDate = ?";
            String[] selectionArgs = {nextDate};

            Cursor cursor = sqLiteDatabase.query("allTasks", columns, selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    title = cursor.getString(cursor.getColumnIndex("title"));
                    // do something with the title
                } while (cursor.moveToNext());
            }


            String message = "Next Task : " + title + "\nScheduled on : " + nextDate;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                    .setContentTitle("Upcoming Date")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_add_tasks)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setOngoing(true);


            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManager.notify(10000000, builder.build());
        }
        else {
            // No more upcoming dates, cancel the notification
            notificationManager.cancel(1000);

        }

    }
}
