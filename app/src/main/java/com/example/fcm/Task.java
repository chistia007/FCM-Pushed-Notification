package com.example.fcm;

public class Task {
    private String title;
    private String description;
    private String dueDate;
    private long _id;
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Task(long _id, String title, String description, String dueDate, boolean isChecked) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this._id=_id;
        this.isChecked = isChecked;
    }

    public long get_id(int position) {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setComplete(boolean checked) {
        isChecked = checked;
    }

    // getters and setters for title, description, and dueDate
}


