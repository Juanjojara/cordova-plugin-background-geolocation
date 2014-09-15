package com.tenforwardconsulting.cordova.bgloc.data.sqlite;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CardOpenHelper extends SQLiteOpenHelper {
    private static final String SQLITE_DATABASE_NAME = "dbLifeshare.db";
    private static final int DATABASE_VERSION = 1;

    private static final String PENDING_GEO_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS pending_geo (id INTEGER PRIMARY KEY, created, info, location, latitude, longitude, sharing_level, location_level, user_id, confirm)";
    private static final String PENDING_CONFIRM_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS pending_confirm (id INTEGER PRIMARY KEY, created, info, location, latitude, longitude, sharing_level, location_level, user_id, confirm)";
    private static final String PENDING_INTERNET_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS pending_internet (id INTEGER PRIMARY KEY, created, info, location, latitude, longitude, sharing_level, location_level, user_id, confirm)";
    private static final String SHARED_CARDS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS shared_cards (id INTEGER PRIMARY KEY, created, info, location, latitude, longitude, sharing_level, location_level, user_id, confirm)";
    private static final String REJECTED_CARDS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS rejected_cards (id INTEGER PRIMARY KEY, created, info, location, latitude, longitude, sharing_level, location_level, user_id, confirm)";

    //tx.executeSql('CREATE TABLE IF NOT EXISTS    pending_geo (id INTEGER PRIMARY KEY, created, info, location, latitude, longitude, sharing_level, location_level, user_id, confirm)');
    //tx.executeSql('CREATE TABLE IF NOT EXISTS  pending_confirm (id INTEGER PRIMARY KEY, created, info, location, latitude, longitude, sharing_level, location_level, user_id, confirm)');
    //tx.executeSql('CREATE TABLE IF NOT EXISTS pending_internet (id INTEGER PRIMARY KEY, created, info, location, latitude, longitude, sharing_level, location_level, user_id, confirm)');
    //tx.executeSql('CREATE TABLE IF NOT EXISTS     shared_cards (id INTEGER PRIMARY KEY, created, info, location, latitude, longitude, sharing_level, location_level, user_id, confirm)', [], dbManagement.updateLastSharedList);
    //tx.executeSql('CREATE TABLE IF NOT EXISTS   rejected_cards (id INTEGER PRIMARY KEY, created, info, location, latitude, longitude, sharing_level, location_level, user_id, confirm)');

    CardOpenHelper(Context context) {
        super(context, SQLITE_DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PENDING_GEO_TABLE_CREATE);
        db.execSQL(PENDING_CONFIRM_TABLE_CREATE);
        db.execSQL(PENDING_INTERNET_TABLE_CREATE);
        db.execSQL(SHARED_CARDS_TABLE_CREATE);
        db.execSQL(REJECTED_CARDS_TABLE_CREATE);
        //Log.d(this.getClass().getName(), LOCATION_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        
    }
}