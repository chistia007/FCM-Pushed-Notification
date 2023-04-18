package com.example.fcm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("rrr", "onReceive: rrr");
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
           SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            Boolean  alwaysOnNotificationValue= prefs.getBoolean("alwaysOnNotificationValue", false);
            if(alwaysOnNotificationValue){
                Intent intent1 = new Intent(context, MyReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 1000, pendingIntent);
            }
        }
    }
}

