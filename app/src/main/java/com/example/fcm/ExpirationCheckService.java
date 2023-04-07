package com.example.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ExpirationCheckService extends Service {
    Cursor t;
    Database db;
    Date currentDate;
    String currentDate1;
    int index;
    String nextDate;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Check for expiration
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

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel for Android Oreo and higher
            NotificationChannel channel = new NotificationChannel("channel_id", "CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("CHANNEL_DESCRIPTION");
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }


        // Create a notification with the details of the next upcoming date
        if (index != -1) {
            nextDate = dates.get(index);
            String message = "Next Schedule : " + nextDate.toString();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                    .setContentTitle("Upcoming Date")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_add_tasks)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setSound(null)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setOngoing(true);


            notificationManager.notify(1000, builder.build());



        }

        // Stop the service once it has finished its work
        stopSelf();
        return START_NOT_STICKY;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
