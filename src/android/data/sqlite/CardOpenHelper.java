package com.tenforwardconsulting.cordova.bgloc.data.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CardOpenHelper extends SQLiteOpenHelper {
    private static final String SQLITE_DATABASE_NAME = "dbLifeshare.db";
    private static final int DATABASE_VERSION = 1;

    CardOpenHelper(Context context) {
        super(context, SQLITE_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(LOCATION_TABLE_CREATE);
        //Log.d(this.getClass().getName(), LOCATION_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        
    }
}