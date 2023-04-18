package com.example.fcm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.fcm.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String ALPHA_KEY = "alpha";

    private SeekBar seekBar;
    private float alpha;
    Intent intent;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        seekBar = findViewById(R.id.seekBar);

        // Restore the alpha value from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        alpha = prefs.getFloat(ALPHA_KEY, 1.0f);
        seekBar.setProgress((int) (alpha * 255));



        // Get the widget's RemoteViews
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.my__widget);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alpha = (float) progress / 255;
                // Set the alpha value of the widget's RemoteViews
                remoteViews.setFloat(R.id.widgetLayout, "setAlpha", alpha);
                remoteViews.setFloat(R.id.totalTodos,"setAlpha",1.0f);

                // Update the widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(SettingsActivity.this);
                ComponentName componentName = new ComponentName(SettingsActivity.this, My_Widget.class);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        binding.switchAlwaysOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(SettingsActivity.this, MyReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                if (binding.switchAlwaysOn.isChecked()){
                    Toast.makeText(SettingsActivity.this, "aaa", Toast.LENGTH_SHORT).show();
                    Log.d("11", "onClick: 11");
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("alwaysOnNotificationValue", true);
                    editor.apply();
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 1000, pendingIntent);

                }
                else{
                    Log.d("33", "onClick: 11");
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("alwaysOnNotificationValue", false);
                    editor.apply();
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                    NotificationManagerCompat notificationManager= NotificationManagerCompat.from(SettingsActivity.this);
                    notificationManager.cancel(10000000);

                }

            }
        });

        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Boolean  alwaysOnNotificationValue= prefs.getBoolean("alwaysOnNotificationValue", false);

        if (alwaysOnNotificationValue) {
            binding.switchAlwaysOn.setChecked(true);
        }
        else{
            binding.switchAlwaysOn.setChecked(false);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        // Save the alpha value to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putFloat(ALPHA_KEY, alpha);
        editor.apply();
    }

}
