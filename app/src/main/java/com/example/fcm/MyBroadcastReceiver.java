package com.example.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Firestone", "onReceive: received");
        String action = intent.getAction();
        Log.d("second", "onReceive: received");

        if (action != null && action.equals("ACTION_DONE")) {

            Log.d("tjird", "onReceive: received");

            String tableName = intent.getStringExtra("tableName");
            int id = intent.getIntExtra("id",-1);

                Log.d("fourth", "onReceive: received");
                Database mDatabase = new Database(context);
                SQLiteDatabase db = mDatabase.getWritableDatabase();
                int rowsDeleted = db.delete("allTasks", "_id=?", new String[]{String.valueOf(id)});
                if (rowsDeleted > 0) {
                    Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show();
                }
                mDatabase.close();
        }
    }
}
