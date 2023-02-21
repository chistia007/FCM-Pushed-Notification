package com.example.fcm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.fcm.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private static final int channel_name = 111;
    private static final int channel_description =124;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.toDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ToDoActivity.class));
            }
        });

        FirebaseApp.initializeApp(this);


    }

}