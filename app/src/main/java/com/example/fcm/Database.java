package com.example.fcm;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    private static final String dbNAme="User.tasks";

    public Database(@Nullable Context context) {
        super(context, dbNAme, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String q="create table tasks(_id integer primary key autoincrement, title text, description text, dueDate text)";
        sqLiteDatabase.execSQL(q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // will drop the database if already exists
        sqLiteDatabase.execSQL("drop table if exists tasks");
        onCreate(sqLiteDatabase);
    }
    // to insert data into the database
    public long insert_data(String title,String description, String dueDate){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues c= new ContentValues();
        c.put("title",title);
        c.put("description",description);
        c.put("dueDate",dueDate);
        long rowId = db.insert("tasks", null, c);
        db.close();
        return rowId;
    }
    // how to see the inserted data from the database
    public Cursor getInfo(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from tasks ",null);
        return cursor;
    }
    // Update database(name and password using username)
//    public boolean updateData(String title,String description, String dueDate){
//        SQLiteDatabase db=this.getWritableDatabase();
//        ContentValues c= new ContentValues();
//        c.put("title",title);
//        c.put("description",description);
//        c.put("dueDate",dueDate);
//        Cursor cursor=db.rawQuery("select * from users where username=?",new String[]{username});
//        if (cursor.getCount()>0){
//            long r=db.update("users",c,"username=?",new String[]{username});
//            if (r==-1) return false;
//            else return true;
//        }
//        else return false;
//
//    }
//
//    //Delete a data
//    public boolean deleteData(String username){
//        SQLiteDatabase db=this.getWritableDatabase();
//        Cursor cursor=db.rawQuery("select * from users where username=?",new String[]{username});
//        if (cursor.getCount()>0){
//            long r=db.delete("users","username=?",new String[]{username});
//            if (r==-1) return false;
//            else return true;
//        }
//        else return false;
//    }

}