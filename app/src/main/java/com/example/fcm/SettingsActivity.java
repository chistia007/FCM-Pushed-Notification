package com.example.fcm;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String ALPHA_KEY = "alpha";

    private SeekBar seekBar;
    private float alpha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
