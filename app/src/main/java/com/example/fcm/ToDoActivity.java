package com.example.fcm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.fcm.databinding.ActivityToDoBinding;
import com.example.fcm.Task;
import com.google.firebase.FirebaseApp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ToDoActivity extends AppCompatActivity {
    ActivityToDoBinding binding;
    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter taskAdapter;
    private String taskTitle;
    private  String taskDescription;

    AlarmManager alarmManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityToDoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        RecyclerView taskList = findViewById(R.id.task_list);
        taskAdapter = new TaskAdapter(tasks);
        taskList.setAdapter(taskAdapter);
        taskList.setLayoutManager(new LinearLayoutManager(this));

        // Set up the FAB to add new tasks

        binding.addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog to add a new task
                showAddTaskDialog();
            }
        });

        FirebaseApp.initializeApp(this);
    }


    private void showAddTaskDialog() {
        // Create an AlertDialog builder and set the title and message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_task_dialog_title);
        builder.setMessage(R.string.add_task_dialog_message);

        // Inflate the layout for the dialog and get the EditText views
        View view = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        EditText titleEditText = view.findViewById(R.id.edit_text_title);
        EditText descriptionEditText = view.findViewById(R.id.edit_text_description);
        EditText dueDateEditText = view.findViewById(R.id.edit_text_due_date);

        // Set the layout for the AlertDialog and set the positive and negative buttons
        builder.setView(view);
        builder.setPositiveButton(R.string.add_task_dialog_add_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the task data from the EditText views and add it to the list
                taskTitle = titleEditText.getText().toString().trim();
                taskDescription = descriptionEditText.getText().toString().trim();
                String dueDate = dueDateEditText.getText().toString();
                Task task = new Task(taskTitle, taskDescription, dueDate);
                tasks.add(task);

                // Notify the adapter that the data has changed
                taskAdapter.notifyDataSetChanged();

                // Show a Toast message to confirm that the task was added
                Toast.makeText(ToDoActivity.this, R.string.task_added_message, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.add_task_dialog_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Set up the due date EditText view to show a date and time picker dialog
        dueDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date and time
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Show a date and time picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(ToDoActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // When the user sets the date, show a time picker dialog
                                TimePickerDialog timePickerDialog = new TimePickerDialog(ToDoActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                // When the user sets the time, update the due date EditText view
                                                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                                dueDateEditText.setText(dateFormat.format(calendar.getTime()));

                                                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                                // Create a new Intent to trigger the alarm
                                                Intent intent = new Intent(ToDoActivity.this, AlarmReceiver.class);
                                                intent.putExtra("taskTitle", taskTitle);
                                                intent.putExtra("taskDescription", taskDescription);
                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(ToDoActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);

                                                // Set the alarm
                                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                                            }
                                        }, hour, minute, false);
                                timePickerDialog.show();
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}





