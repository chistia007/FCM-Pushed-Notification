package com.example.fcm;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Database extends SQLiteOpenHelper {
    private static final String dbNAme="User.tasks";
    SQLiteDatabase db;
    ContentValues c;
    long rowId;
    Cursor cursor;
    int numRowsAffected;

    public Database(@Nullable Context context) {
        super(context, dbNAme, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String q1="create table allTasks(_id integer primary key autoincrement, title text, description text, dueDate text, correspondingTableId int,correspondingTable Text)";
        sqLiteDatabase.execSQL(q1);
        String q2="create table officeWork(_id integer primary key autoincrement, title text, description text, dueDate text, correspondingTableId int,correspondingTable Text)";
        sqLiteDatabase.execSQL(q2);
        String q3="create table houseWork(_id integer primary key autoincrement, title text, description text, dueDate text, correspondingTableId int,correspondingTable Text)";
        sqLiteDatabase.execSQL(q3);
        String q4="create table learning(_id integer primary key autoincrement, title text, description text, dueDate text, correspondingTableId int,correspondingTable Text)";
        sqLiteDatabase.execSQL(q4);
        String q5="create table extraCurr(_id integer primary key autoincrement, title text, description text, dueDate text, correspondingTableId int,correspondingTable Text)";
        sqLiteDatabase.execSQL(q5);
        String q6="create table doneTasks(_id integer primary key autoincrement, title text, description text, dueDate text,correspondingTable Text)";
        sqLiteDatabase.execSQL(q6);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // will drop the database if already exists
        sqLiteDatabase.execSQL("drop table if exists tasks");
        onCreate(sqLiteDatabase);
    }
    // to insert data into the database
    public long insert_data(String selectTable,String title,String description, String dueDate){
        db=this.getWritableDatabase();
        c= new ContentValues();
        c.put("title",title);
        c.put("description",description);
        c.put("dueDate",dueDate);
        int id = getMaxRowNumber(selectTable)+1;
        c.put("correspondingTableId",id);
        c.put("correspondingTable",selectTable);
        rowId = db.insert("allTasks", null, c);
        c.clear();
        c.put("title",title);
        c.put("description",description);
        c.put("dueDate",dueDate);
        c.put("correspondingTableId",rowId);  // remember here i am passing the id of allTasks
        c.put("correspondingTable",selectTable); // here i will passing it own tale name so that when i click on officeWork, i know it's office works table from inItemclick

        switch(selectTable){
            case "officeWork":
                rowId = db.insert("officeWork", null, c);
                db.close();
                return rowId;

            case "houseWork":
                rowId = db.insert("houseWork", null, c);
                db.close();
                return rowId;
            case "learning":
                rowId = db.insert("learning", null, c);
                db.close();
                return rowId;
            case "extraCurr":
                rowId = db.insert("extraCurr", null, c);
                db.close();
                return rowId;
            default:
                db.close();
                return rowId;


        }
    }
    // how to see the inserted data from the database
    public Cursor getInfo(String selectTable) {
        db = this.getWritableDatabase();
        switch (selectTable) {
            case "office":
                cursor = db.rawQuery("select * from officeWork ", null);
                break;

            case "house":
                cursor = db.rawQuery("select * from houseWork ", null);
                break;

            case "learning":
                cursor = db.rawQuery("select * from learning ", null);
                break;

            case "extra":
                cursor = db.rawQuery("select * from extraCurr ", null);
                break;
            case "doneTasks":
                cursor = db.rawQuery("select * from doneTasks ", null);
                break;

            default:
                cursor = db.rawQuery("select * from allTasks ", null);
                break;
        }

        // close database connection after using the cursor
        return cursor;
    }
//     Update database(name and password using username)
    public boolean updateDatabase(String selectTable, long _id, String title, String description, String dueDate,int corrTabID,String corrTabName, boolean navigation_clicked) {
        db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put("title", title);
        c.put("description", description);
        c.put("dueDate", dueDate);
        String selection = null;
        String[] selectionArgs = new String[0];
        String selection1 = null;
        String[] selectionArgs1 = new String[0];
        // Specify the selection criteria as the _id field
        if (navigation_clicked){
            selection = "_id=?";
            selectionArgs = new String[]{String.valueOf(_id)};
            selection1 = "_id=?";
            selectionArgs1 = new String[]{String.valueOf(corrTabID)};
        }
        else{
            selection1 = "_id=?";
            selectionArgs1 = new String[]{String.valueOf(_id)};
            selection = "_id=?";
            selectionArgs = new String[]{String.valueOf(corrTabID)};
        }


        if (corrTabName.equals("allTasks")&& selectTable.equals("allTasks")){
            int numRowsAffected1 = db.update("allTasks", c, selection, selectionArgs);
            return numRowsAffected1 > 0;
        }
        else if (corrTabName.equals("officeWork")&& (selectTable.equals("officeWork")||selectTable.equals("allTasks"))){
            int numRowsAffected1 = db.update("officeWork", c, selection1, selectionArgs1);
            int numRowsAffected2 = db.update("allTasks", c, selection, selectionArgs);
            return numRowsAffected2 > 0;
        }
        else if (corrTabName.equals("houseWork")&& (selectTable.equals("houseWork")||selectTable.equals("allTasks"))){
            int numRowsAffected1 = db.update("houseWork", c, selection1, selectionArgs1);
            int numRowsAffected2 = db.update("allTasks", c, selection, selectionArgs);
            return numRowsAffected2 > 0;
        }
        else if (corrTabName.equals("learning")&& (selectTable.equals("learning")||selectTable.equals("allTasks"))){
            int numRowsAffected1 = db.update("learning", c, selection1, selectionArgs1);
            int numRowsAffected2 = db.update("allTasks", c, selection, selectionArgs);
            return numRowsAffected2 > 0;
        }
        else if (corrTabName.equals("extraCurr")&& (selectTable.equals("extraCurr")||selectTable.equals("allTasks"))){
            int numRowsAffected1 = db.update("extraCurr", c, selection1, selectionArgs1);
            int numRowsAffected2 = db.update("allTasks", c, selection, selectionArgs);
            return numRowsAffected2 > 0;
        }
        else{return false;}

    }
    //Delete a data
    public boolean deleteData(long id, String title, String description, String dueDate,int corrTabID,String corrTabName, boolean navigation_clicked){
        db = this.getWritableDatabase();
        c=new ContentValues();
        c.put("title",title);
        c.put("description",description);
        c.put("dueDate",dueDate);
        c.put("correspondingTable",corrTabName);
        db.insert("doneTasks", null, c);
        long r = 0;
        long r1=0;
        long id1;
        int id2;
        Log.d("555555555555555555", "deleteData: "+navigation_clicked + id + " " + corrTabID+ " "+ corrTabName);
        // Specify the selection criteria as the _id field
        if (navigation_clicked){
            id1=(int) id;
            id2=corrTabID;
        }
        else{
            id1=corrTabID;
            id2= (int) id;
        }

        //Delete operation
        try {
            r = db.delete("allTasks", "_id=?", new String[]{String.valueOf(id1)});
            r1= db.delete(""+corrTabName, "_id=?", new String[]{String.valueOf(id2)});
            // Perform database operations...
        } catch (Exception e) {
            Log.e("TAG", "Error occurred while performing database operations: " + e.getMessage());
        }

        Log.d("444444444444", "deleteData: "+navigation_clicked + id1 + " " + id2+ " "+ corrTabName);


        if (r == -1 || r1==-1) {
            return false;
        }
        else {
            return true;
        }


    }

    public int getMaxRowNumber(String tableName) {
        int maxRow = 0;

        if (tableName == null || tableName.isEmpty()) {
            return maxRow;  // Return 0 if tableName is null or empty
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(rowid) FROM " + tableName, null);

        if (cursor.moveToFirst()) {
            maxRow = cursor.getInt(0);
        }

//        cursor.close();
//        db.close();

        return maxRow;
    }

    public void doneTaskDelete(long id){
        db = this.getWritableDatabase();
        try {
            db.delete("doneTasks", "_id=?", new String[]{String.valueOf(id)});
            // Perform database operations...
        } catch (Exception e) {
            Log.e("TAG", "Error occurred while performing database operations: " + e.getMessage());
        }
    }


}