package com.example.fcm;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    private static final String dbNAme="User.tasks";
    SQLiteDatabase db;
    ContentValues c;
    long rowId;
    Cursor cursor;

    public Database(@Nullable Context context) {
        super(context, dbNAme, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String q1="create table allTasks(_id integer primary key autoincrement, title text, description text, dueDate text)";
        sqLiteDatabase.execSQL(q1);
        String q2="create table officeWork(_id integer primary key autoincrement, title text, description text, dueDate text)";
        sqLiteDatabase.execSQL(q2);
        String q3="create table houseWork(_id integer primary key autoincrement, title text, description text, dueDate text)";
        sqLiteDatabase.execSQL(q3);
        String q4="create table learning(_id integer primary key autoincrement, title text, description text, dueDate text)";
        sqLiteDatabase.execSQL(q4);
        String q5="create table extraCurr(_id integer primary key autoincrement, title text, description text, dueDate text)";
        sqLiteDatabase.execSQL(q5);
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
        switch(selectTable){
            case "office":
                rowId = db.insert("officeWork", null, c);
                db.insert("allTasks", null, c);
                db.close();
                return rowId;

            case "house":
                rowId = db.insert("houseWork", null, c);
                db.insert("allTasks", null, c);
                db.close();
                return rowId;
            case "learning":
                rowId = db.insert("learning", null, c);
                db.insert("allTasks", null, c);
                db.close();
                return rowId;
            case "extra":
                rowId = db.insert("extraCurr", null, c);
                db.insert("allTasks", null, c);
                db.close();
                return rowId;
            default:
                rowId = db.insert("allTasks", null, c);
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

            default:
                cursor = db.rawQuery("select * from allTasks ", null);
                break;
        }

        // close database connection after using the cursor
        return cursor;
    }
//     Update database(name and password using username)
    public boolean updateDatabase(long _id, String title, String description, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        c.put("title", title);
        c.put("description", description);
        c.put("dueDate", dueDate);

        // Specify the selection criteria as the _id field
        String selection = "_id=?";
        String[] selectionArgs = new String[]{String.valueOf(_id)};

        // Update the row with the specified _id
        int numRowsAffected = db.update("allTasks", c, selection, selectionArgs);

        return numRowsAffected > 0;
}
    //Delete a data
    public boolean deleteData(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE _id=?", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0){
            long r = db.delete("users", "_id=?", new String[]{String.valueOf(id)});
            if (r == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }


}