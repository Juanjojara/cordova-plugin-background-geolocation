package com.tenforwardconsulting.cordova.bgloc.data.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CardOpenHelper extends SQLiteOpenHelper {
    private static final String SQLITE_DATABASE_NAME = "dbLifeshare";
    private static final int DATABASE_VERSION = 1;

    CardOpenHelper(Context context) {
        super(context, SQLITE_DATABASE_NAME, null, DATABASE_VERSION);
    }    
}