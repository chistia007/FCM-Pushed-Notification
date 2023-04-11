package com.example.fcm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.fcm.databinding.ActivityToDoBinding;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ToDoActivity extends AppCompatActivity {
    ActivityToDoBinding binding;
    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter taskAdapter;
    private String taskTitle;
    private String taskDescription;
    private String dueDateBroadcast;

    EditText titleEditText;
    EditText descriptionEditText;
    EditText dueDateEditText;
    AutoCompleteTextView dropDowns;
    AlarmManager alarmManager;

    Database db;

    Task task;
    Cursor t;
    boolean i;
    long k;
    Boolean checked = false;
    OnItemClickListener listener;
    RecyclerView taskList;
    LinearLayoutManager layoutManager;
    public String selectTable = "allTasks";
    Boolean navigation_clicked = true;
    Calendar calendar;
    public static final String ACTION_DATA_UPDATED = "com.example.fcm.ACTION_DATA_UPDATED";
    Boolean doneChecked = false;
    String currentDate1;
    String nextDate;
    Date currentDate;
    int index;


    @SuppressLint({"NotifyDataSetChanged", "ShortAlarm"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityToDoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        db = new Database(this);

        updateUI(selectTable);


        binding.addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog to add a new task
                showAddTaskDialog();

            }
        });


        navigationDrawer();

        try {
            listener = new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Task task = tasks.get(position);
                    long line0 = task.get_id(position);
                    String line1 = task.getTitle();
                    String line2 = task.getDescription();
                    String line3 = task.getDueDate();
                    int line4 = task.getCorrespondingTableId();
                    String line5 = task.getCorrespondingTable();
                    UpdateTaskInDialog(line0, line1, line2, line3, line4, line5);
                }

                //Delete operation

                @Override

                public void onCheckboxClick(View view, int position, boolean isChecked) {
                    Task task = tasks.get(position);
                    task.setComplete(isChecked);


                    if (doneChecked.equals(true)) {
                        Log.d("11", "onCheckboxClick: 11");
                        try {
                            if (!isChecked) {    //recovering a done to-do to its corresponding table
                                Log.d("11", "onCheckboxClick: 22");
                                k = db.insert_data(task.getCorrespondingTable(), task.getTitle(), task.getDescription(), task.getDueDate());
                                if (k != 0) {
                                    Log.d("11", "onCheckboxClick: 33");
                                    Toast.makeText(ToDoActivity.this, "Successfully data Inserted", Toast.LENGTH_SHORT).show();
                                    db.doneTaskDelete(task.get_id());
                                    updateUI("doneTasks");
                                } else {
                                    Log.d("11", "onCheckboxClick: 44");
                                    Toast.makeText(ToDoActivity.this, "Insertion failed to", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } finally {

                        }

                    }
                    try {
                        if (isChecked) {
                            // delete task from database using auto-incremented ID
                            Boolean s = db.deleteData(task.get_id(position), task.getTitle(), task.getDescription(), task.getDueDate(), task.getCorrespondingTableId(), task.getCorrespondingTable(), navigation_clicked);
                            if (s) {
                                Toast.makeText(ToDoActivity.this, "Task Done", Toast.LENGTH_SHORT).show();
                            }
                            // remove task from list and notify adapter
                            tasks.remove(position);
                            tasks.clear();
                            taskAdapter.notifyItemRemoved(position);
                            taskAdapter.notifyDataSetChanged();
                            updateUI(selectTable);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }


                }


            };
        } catch (Exception e) {
            Log.e("listener error", "Error occurred while performing database operations: " + e.getMessage());
        }


        updateUI(selectTable);

        Intent intent = new Intent(ToDoActivity.this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ToDoActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 1000, pendingIntent);


    }

    private void updateUI(String selectTable) {
        taskList = findViewById(R.id.task_list);
        taskAdapter = new TaskAdapter(tasks, listener);
        taskList.setAdapter(taskAdapter);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        taskList.setLayoutManager(layoutManager);
        t = db.getInfo(selectTable); //t is Cursor
        if (t.getCount() == 0) {
            Toast.makeText(ToDoActivity.this, "No data founded", Toast.LENGTH_SHORT).show();
            tasks.clear();
            doneChecked = false;
        } else {
            tasks.clear();
            int index = 0; // keep track of the current index
            while (t.moveToNext()) {
                if (selectTable.equals("doneTasks")) {
                    task = new Task(t.getLong(0), t.getString(1), t.getString(2), t.getString(3), true, t.getString(4));
                } else {
                    task = new Task(t.getLong(0), t.getString(1), t.getString(2), t.getString(3), checked, t.getInt(4), t.getString(5));
                }
                tasks.add(index, task); // add the new item at the current index
                taskAdapter.notifyItemInserted(index); // notify the adapter of the new item
                index++; // increment the index for the next item
            }
        }
        taskAdapter.notifyDataSetChanged();

        // Updating the widget whenever adding a new TODOs
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, My_Widget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        My_Widget m = new My_Widget();
        m.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @SuppressLint({"NonConstantResourceId", "MissingInflatedId"})
    private void navigationDrawer() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_View);

        // Set up ActionBarDrawerToggle and add it to DrawerLayout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                ToDoActivity.this,
                drawerLayout,
                R.string.open,
                R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set up NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            doneChecked = false;
            switch (item.getItemId()) {
                case R.id.officeWorks:
                    navigation_clicked = false;
                    binding.bucketName.setText("OFFICE");
                    updateUI("office");
                    break;
                case R.id.houseWork:
                    navigation_clicked = false;
                    binding.bucketName.setText("HOUSE");
                    updateUI("house");
                    break;
                case R.id.learning:
                    navigation_clicked = false;
                    binding.bucketName.setText("LEARNING");
                    updateUI("learning");
                    break;
                case R.id.extra_curr:
                    navigation_clicked = false;
                    binding.bucketName.setText("EXTRA CURRICULUM");
                    updateUI("extra");
                    break;
                case R.id.done_tasks:
                    navigation_clicked = false;
                    doneChecked = true;
                    binding.bucketName.setText("DONE TASKS");
                    updateUI("doneTasks");
                    break;
                case R.id.settings:
                    startActivity(new Intent(ToDoActivity.this, SettingsActivity.class));
                    break;
                default:
                    navigation_clicked = true;
                    binding.bucketName.setText("ALL TASKS");
                    updateUI("allTasks");
                    break;
            }

            // Close the navigation drawer
            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        });

        // Set up AppBar click event to open the navigation drawer
        binding.imageMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));


    }


    private void showAddTaskDialog() {
        // Create an AlertDialog builder and set the title and message
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
        builder.setTitle(R.string.add_task_dialog_title);
        builder.setMessage(R.string.add_task_dialog_message);

        // Set the message text color


        // Inflate the layout for the dialog and get the EditText views
        View view = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        titleEditText = view.findViewById(R.id.edit_text_title);
        descriptionEditText = view.findViewById(R.id.edit_text_description);
        dueDateEditText = view.findViewById(R.id.edit_text_due_date);
        dropDowns = view.findViewById(R.id.dropDownText);

        dropDownMenu();

        // Set the layout for the AlertDialog and set the positive and negative buttons
        builder.setView(view);
        builder.setPositiveButton(R.string.add_task_dialog_add_button, new DialogInterface.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(DialogInterface dialog, int which) {


                // Show a Toast message to confirm that the task was added
                Toast.makeText(ToDoActivity.this, R.string.task_added_message, Toast.LENGTH_SHORT).show();

                taskTitle = titleEditText.getText().toString();
                taskDescription = descriptionEditText.getText().toString();
                String dueDate = dueDateEditText.getText().toString();
                selectTable = dropDowns.getText().toString();
                if (selectTable.equals("")) {
                    selectTable = "allTasks";
                }

                k = db.insert_data(selectTable, taskTitle, taskDescription, dueDate);
                if (k != 0) {
                    Toast.makeText(ToDoActivity.this, "Successfully data Inserted", Toast.LENGTH_SHORT).show();
                    updateUI(selectTable);
                    binding.bucketName.setText("ALL TASKS");
                } else {
                    Toast.makeText(ToDoActivity.this, "Insertion failed to", Toast.LENGTH_SHORT).show();
                }

                //setting reminder and sending the selected table so that we can delete it from the notification bar when we receive broadcast
                if (!dueDate.equals("")) {
                    setReminder(selectTable);
                }


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
                calendar = Calendar.getInstance();
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
                                            @RequiresApi(api = Build.VERSION_CODES.S)
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                // When the user sets the time, update the due date EditText view
                                                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                                dueDateEditText.setText(dateFormat.format(calendar.getTime()));

                                                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                                // Create a new Intent to trigger the alarm
                                                taskTitle = titleEditText.getText().toString();
                                                taskDescription = descriptionEditText.getText().toString();
                                                dueDateBroadcast = dateFormat.format(calendar.getTime());


                                            }
                                        }, hour, minute, false);
                                timePickerDialog.show();
                            }
                        }, year, month, day);
                datePickerDialog.show();
                taskAdapter.notifyDataSetChanged();
            }
        });

        // Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void setReminder(String selectTable) {
        Intent intent = new Intent(ToDoActivity.this, AlarmReceiver.class);
        intent.putExtra("taskTitle", taskTitle);
        intent.putExtra("taskDescription", taskDescription);
        intent.putExtra("tableName", selectTable);
        intent.putExtra("dueDate", dueDateBroadcast);
        int id2 = db.getMaxRowNumber(selectTable);
        int id1 = db.getMaxRowNumber("allTasks");

        intent.putExtra("id1", id1);
        intent.putExtra("id2", id2);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(ToDoActivity.this, id1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        // Set the alarm

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);


    }


    private void UpdateTaskInDialog(long id, String line1, String line2, String line3, int line4, String line5) {
        // Create an AlertDialog builder and set the title and message
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
        builder.setTitle(R.string.add_task_dialog_title);
        builder.setMessage(R.string.add_task_dialog_message);

        // Inflate the layout for the dialog and get the EditText views
        View view = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        titleEditText = view.findViewById(R.id.edit_text_title);
        descriptionEditText = view.findViewById(R.id.edit_text_description);
        dueDateEditText = view.findViewById(R.id.edit_text_due_date);
        dropDowns = view.findViewById(R.id.dropDownText);

//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        LocalDateTime dateTime = LocalDateTime.parse("2023-04-01 19:02", formatter);
//        int year = dateTime.getYear();
//        int month = dateTime.getMonthValue();
//        int day = dateTime.getDayOfMonth();
//        int hour = dateTime.getHour();
//        int minute = dateTime.getMinute();

        dropDownMenu();
        // setting texts to edittext when user click an item on recyclerview
        titleEditText.setText(line1);
        descriptionEditText.setText(line2);
        dueDateEditText.setText(line3);


        // Set the layout for the AlertDialog and set the positive and negative buttons
        builder.setView(view);
        builder.setPositiveButton(R.string.add_task_dialog_add_button, new DialogInterface.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(DialogInterface dialog, int which) {


                // Show a Toast message to confirm that the task was added
                Toast.makeText(ToDoActivity.this, R.string.task_added_message, Toast.LENGTH_SHORT).show();

                taskTitle = titleEditText.getText().toString();
                taskDescription = descriptionEditText.getText().toString();
                String dueDate = dueDateEditText.getText().toString();
                selectTable = dropDowns.getText().toString();

                if (taskTitle.equals("")) {
                    Toast.makeText(ToDoActivity.this, "You can not title empty", Toast.LENGTH_SHORT).show();
                } else {
                    if (selectTable.equals("")) {
                        selectTable = "allTasks";
                    }
                    dropDownMenu();
                    i = db.updateDatabase(selectTable, id, taskTitle, taskDescription, dueDate, line4, line5, navigation_clicked);
                    if (i) {
                        Toast.makeText(ToDoActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                        updateUI(selectTable);
                        binding.bucketName.setText("ALL TASKS");

                    } else {
                        Toast.makeText(ToDoActivity.this, "It doesn't  belong to " + selectTable, Toast.LENGTH_SHORT).show();
                    }
                }

                if (!dueDate.equals("")) {
                    setReminder(selectTable);
                }


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
                calendar = Calendar.getInstance();
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
                                            @RequiresApi(api = Build.VERSION_CODES.S)
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                // When the user sets the time, update the due date EditText view
                                                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                                dueDateEditText.setText(dateFormat.format(calendar.getTime()));

                                                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                                // Create a new Intent to trigger the alarm
                                                taskTitle = titleEditText.getText().toString();
                                                taskDescription = descriptionEditText.getText().toString();
                                                dueDateBroadcast = dateFormat.format(calendar.getTime());
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

    private void dropDownMenu() {
        String items[] = new String[]{
                "allTasks",
                "officeWork",
                "houseWork",
                "learning",
                "extraCurr"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ToDoActivity.this,
                R.layout.drop_down_text,
                items
        );

        dropDowns.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        taskAdapter.notifyDataSetChanged();
        // Updating the widget whenever adding a new TODOs
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, My_Widget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        My_Widget m = new My_Widget();
        m.onUpdate(context, appWidgetManager, appWidgetIds);


        ///fixed notification
//        ArrayList<String> dates = new ArrayList<>();
//        t = db.getInfo("allTasks");
//        while (t.moveToNext()) {
//            if (!t.getString(3).equals("")) {
//                dates.add(t.getString(3));
//            }
//        }
//        Collections.sort(dates);
//        currentDate = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        currentDate1 = dateFormat.format(currentDate);
//
//        // Find the index of the next upcoming date
//        index = -1;
//        for (int i = 0; i < dates.size(); i++) {
//            if (dates.get(i).compareTo(currentDate1) > 0) {
//                index = i;
//                break;
//            }
//        }
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Create the notification channel for Android Oreo and higher
//            NotificationChannel channel = new NotificationChannel("channel_id", "CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription("CHANNEL_DESCRIPTION");
//            channel.enableLights(true);
//            channel.setLightColor(Color.GREEN);
//            channel.setSound(null,null);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//
//        // Create a notification with the details of the next upcoming date
//        if (index != -1) {
//            nextDate = dates.get(index);
//            String message = "Next Schedule : " + nextDate.toString();
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
//                    .setContentTitle("Upcoming Date")
//                    .setContentText(message)
//                    .setSmallIcon(R.drawable.ic_add_tasks)
//                    .setPriority(NotificationCompat.PRIORITY_LOW)
//                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                    .setSound(null)
//     //               .setVisibility(NotificationCompat.VISIBILITY_SECRET)
//                    .setOngoing(true);
//
//
//
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//           }
//           notificationManager.notify(1000, builder.build());
//
//            // Schedule a timer to check for expiration of current date and update the notification
//            Timer timer = new Timer();
//            timer.scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
//                    currentDate1 = dateFormat.format(currentDate);
//                    if (currentDate1.compareTo(nextDate) > 0) {
//                       // Current date has expired, find the next upcoming date and update the notification
//                        int newIndex = -1;
//                        for (int i = index + 1; i < dates.size(); i++) {
//                            if (dates.get(i).compareTo(currentDate1) > 0) {
//                                newIndex = i;
//                                break;
//                            }
//                        }
//                        if (newIndex != -1) {
//                            String newNextDate = dates.get(newIndex);
//                            String newMessage = "Next date: " + newNextDate.toString();
//                            builder.setContentText(newMessage);
//                            if (ActivityCompat.checkSelfPermission(ToDoActivity.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                                // TODO: Consider calling
//                                //    ActivityCompat#requestPermissions
//                               // here to request the missing permissions, and then overriding
//                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                //                                          int[] grantResults)                 // to handle the case where the user grants the permission. See the documentation
//                                // for ActivityCompat#requestPermissions for more details.
//                                return;
//                            }
//                            notificationManager.notify(1000, builder.build());
//                            nextDate = newNextDate;
//                            index = newIndex;
//                        } else {
//                            // No more upcoming dates, cancel the notification
//                            notificationManager.cancel(1000);
//                            timer.cancel();
//                        }
//                    }
//                }
//            }, 0, 1000 * 60); // Check every minute for expiration of current date
//        }



    }

}







