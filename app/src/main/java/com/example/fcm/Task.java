package com.example.fcm;

public class Task {
    private String title;
    private String description;
    private String dueDate;
    private long _id;
    private boolean isChecked;
    private int correspondingTableId;
    private String correspondingTable;



    public boolean isChecked() {
        return isChecked;
    }

    public Task(long _id, String title, String description, String dueDate, boolean isChecked, int correspondingTableId,  String correspondingTable) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this._id=_id;
        this.isChecked = isChecked;
        this.correspondingTableId = correspondingTableId;
        this.correspondingTable = correspondingTable;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getCorrespondingTableId() {
        return correspondingTableId;
    }

    public void setCorrespondingTableId(int correspondingTableId) {
        this.correspondingTableId = correspondingTableId;
    }

    public String getCorrespondingTable() {
        return correspondingTable;
    }

    public void setCorrespondingTable(String correspondingTable) {
        this.correspondingTable = correspondingTable;
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


