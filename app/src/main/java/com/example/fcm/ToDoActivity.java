package com.example.fcm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.fcm.databinding.ActivityToDoBinding;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ToDoActivity extends AppCompatActivity {
    ActivityToDoBinding binding;
    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter taskAdapter;
    private String taskTitle;
    private  String taskDescription;

    EditText titleEditText;
    EditText descriptionEditText;
    EditText dueDateEditText;

    AlarmManager alarmManager;

    Database db;

    Task task;
    Cursor t;
    boolean i;
    long k;
    Boolean checked=false;
    OnItemClickListener listener;
    RecyclerView taskList;
    LinearLayoutManager layoutManager;
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityToDoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        db=new Database(this);


        taskList = findViewById(R.id.task_list);
        taskAdapter = new TaskAdapter(tasks,listener);
        taskList.setAdapter(taskAdapter);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        taskList.setLayoutManager(layoutManager);



        t = db.getInfo();
        if (t.getCount() == 0) {
            Toast.makeText(ToDoActivity.this, "No data founded", Toast.LENGTH_SHORT).show();
        } else {
            tasks.clear();
            int index = 0; // keep track of the current index
            while (t.moveToNext()) {
                task = new Task(t.getLong(0), t.getString(1), t.getString(2), t.getString(3), checked);
                tasks.add(index, task); // add the new item at the current index
                taskAdapter.notifyItemInserted(index); // notify the adapter of the new item
                index++; // increment the index for the next item
            }
        }




        binding.addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog to add a new task
                showAddTaskDialog();

            }
        });

       // FirebaseApp.initializeApp(this);

        listener = new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Task task = tasks.get(position);
                long line0=task.get_id(position);
                String line1 = task.getTitle();
                String line2 = task.getDescription();
                String line3 = task.getDueDate();
                UpdateTaskInDialog(line0,line1,line2,line3);
            }
            @Override
            public void onCheckboxClick(View view, int position, boolean isChecked) {
                Task task = tasks.get(position);
                task.setComplete(isChecked);
                Toast.makeText(ToDoActivity.this, "checked", Toast.LENGTH_SHORT).show();
                if (isChecked) {
                    // delete task from database using auto-incremented ID
                    Boolean s=db.deleteData(task.get_id(position));
                    if(s){
                        Toast.makeText(ToDoActivity.this, "done del", Toast.LENGTH_SHORT).show();
                    }
                    // remove task from list and notify adapter
                    tasks.remove(position);
                    taskAdapter.notifyItemRemoved(position);
                }
            }


        };



        taskList = findViewById(R.id.task_list);
        taskAdapter = new TaskAdapter(tasks,listener);
        taskList.setAdapter(taskAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        taskList.setLayoutManager(layoutManager);



        t = db.getInfo();
        if (t.getCount() == 0) {
            Toast.makeText(ToDoActivity.this, "No data founded", Toast.LENGTH_SHORT).show();
        } else {
            tasks.clear();
            int index = 0; // keep track of the current index
            while (t.moveToNext()) {
                task = new Task(t.getLong(0), t.getString(1), t.getString(2), t.getString(3), checked);
                tasks.add(index, task); // add the new item at the current index
                taskAdapter.notifyItemInserted(index); // notify the adapter of the new item
                index++; // increment the index for the next item
            }
        }

        navigationDrawer();

    }

    private void navigationDrawer() {

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_View);


        // Navigation Drawer------------------------------

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(ToDoActivity.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Drawer click event
        // Drawer item Click event ------
        navigationView.setNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.allTasks:
                   // Toast.makeText(CgpaBracuCalculatorActivity.this, "Your Dashboard", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                    break;

//                case R.id.allTasks:
//                    //startActivity(new Intent(CgpaBracuCalculatorActivity.this,RepeatCalculation.class));
//                    break;
//
//                case R.id.allTasks:
//                    //startActivity(new Intent(CgpaBracuCalculatorActivity.this,UpdateDashboard.class));
//                    break;
//
//
//                case R.id.officeWorks:
//                    //startActivity(new Intent(CgpaBracuCalculatorActivity.this,HowToUSe.class));
//                    break;
//
//                case R.id.officeWorks:
//                    //startActivity(new Intent(CgpaBracuCalculatorActivity.this,LearnAppDev.class));
//                    break;
//                case R.id.officeWorks:
//                    //startActivity(new Intent(CgpaBracuCalculatorActivity.this,ShareWebsite.class));
//                    break;

                case R.id.officeWorks:
                    //startActivity(new Intent(CgpaBracuCalculatorActivity.this,StartActivity.class));
                    break;

            }

            return false;
        });
        //------------------------------

        // ------------------------
        // App Bar Click Event

        binding.imageMenu.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
    }


    private void showAddTaskDialog() {
        // Create an AlertDialog builder and set the title and message
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyAlertDialogTheme);
        builder.setTitle(R.string.add_task_dialog_title);
        builder.setMessage(R.string.add_task_dialog_message);

        // Set the message text color


        // Inflate the layout for the dialog and get the EditText views
        View view = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        titleEditText = view.findViewById(R.id.edit_text_title);
        descriptionEditText = view.findViewById(R.id.edit_text_description);
        dueDateEditText = view.findViewById(R.id.edit_text_due_date);



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


                k=db.insert_data(taskTitle,taskDescription,dueDate);
                if(k!=0){
                    Toast.makeText(ToDoActivity.this, "Successfully data Inserted", Toast.LENGTH_SHORT).show();
                }
                else{Toast.makeText(ToDoActivity.this, "Insertion failed", Toast.LENGTH_SHORT).show();}


                t=db.getInfo();
                tasks.clear();
                while(t.moveToNext()){
                    task = new Task(t.getLong(0), t.getString(1), t.getString(2),t.getString(3),checked);
                    tasks.add(task);
                }
                taskAdapter.notifyDataSetChanged();


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
                                            @RequiresApi(api = Build.VERSION_CODES.S)
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                // When the user sets the time, update the due date EditText view
                                                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                                dueDateEditText.setText(dateFormat.format(calendar.getTime()));

                                                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                                // Create a new Intent to trigger the alarm
                                                taskTitle = titleEditText.getText().toString();
                                                taskDescription = descriptionEditText.getText().toString();

                                                Intent intent = new Intent(ToDoActivity.this, AlarmReceiver.class);
                                                intent.putExtra("taskTitle", taskTitle);
                                                intent.putExtra("taskDescription", taskDescription);
                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(ToDoActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);

                                                // Set the alarm
                                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

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


    private void UpdateTaskInDialog(long id, String line1, String line2, String line3) {
        // Create an AlertDialog builder and set the title and message
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyAlertDialogTheme);
        builder.setTitle(R.string.add_task_dialog_title);
        builder.setMessage(R.string.add_task_dialog_message);

        // Inflate the layout for the dialog and get the EditText views
        View view = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        titleEditText = view.findViewById(R.id.edit_text_title);
        descriptionEditText = view.findViewById(R.id.edit_text_description);
        dueDateEditText = view.findViewById(R.id.edit_text_due_date);
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

                if(taskTitle.equals("")|| taskDescription.equals("") || dueDate.equals("")){
                    Toast.makeText(ToDoActivity.this, "You can not leave any field empty", Toast.LENGTH_SHORT).show();
                }
                else{
                    i=db.updateDatabase(id,taskTitle,taskDescription,dueDate);
                    if(i){
                        Toast.makeText(ToDoActivity.this, "Successfully data Inserted", Toast.LENGTH_SHORT).show();
                    }
                    else{Toast.makeText(ToDoActivity.this, "Insertion failed", Toast.LENGTH_SHORT).show();}
                }

                t=db.getInfo();
                tasks.clear();
                if(t.getCount()==0){
                    Toast.makeText(ToDoActivity.this, "No data founded", Toast.LENGTH_SHORT).show();
                }
                while(t.moveToNext()){
                    task = new Task(t.getLong(0), t.getString(1), t.getString(2), t.getString(3),checked);
                    tasks.add(task);
                }




                // Notify the adapter that the data has changed
                taskAdapter.notifyDataSetChanged();


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
                                            @RequiresApi(api = Build.VERSION_CODES.S)
                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                // When the user sets the time, update the due date EditText view
                                                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                                dueDateEditText.setText(dateFormat.format(calendar.getTime()));

                                                alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                                // Create a new Intent to trigger the alarm
                                                taskTitle = titleEditText.getText().toString();
                                                taskDescription = descriptionEditText.getText().toString();

                                                Intent intent = new Intent(ToDoActivity.this, AlarmReceiver.class);
                                                intent.putExtra("taskTitle", taskTitle);
                                                intent.putExtra("taskDescription", taskDescription);
                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(ToDoActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);

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







