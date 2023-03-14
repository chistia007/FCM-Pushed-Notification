package com.example.fcm;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.animation.AnimatableView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private static OnItemClickListener listener;
    public TaskAdapter(List<Task> tasks,  OnItemClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Task task = tasks.get(position);
        holder.taskNameTextView.setText(task.getTitle());
        holder.taskDesc.setText(task.getDescription());
        holder.dueTimeTextView.setText(task.getDueDate());
        holder.checkBox.setChecked(task.isChecked());

        //Here, we set an OnClickListener on the item view, and call the onItemClick method of the listener when the view is clicked.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, position);
            }
        });

        //Here, we set an OnClickListener on the checkbox, and call the onItemClick method of the listener when the checkbox is clicked.
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setComplete(holder.checkBox.isChecked());
            }
        });



    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void addTask(Task task) {
        tasks.add(task);
        notifyItemInserted(tasks.size() - 1);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView taskNameTextView;
        TextView dueTimeTextView;

        TextView taskDesc;

        CheckBox checkBox;


        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.task_name);
            dueTimeTextView = itemView.findViewById(R.id.due_time);
            taskDesc= itemView.findViewById(R.id.task_desc);
            checkBox = itemView.findViewById(R.id.task_checkbox);


        }

    }
}

