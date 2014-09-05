package com.tenforwardconsulting.cordova.bgloc.data.sqlite;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.tenforwardconsulting.cordova.bgloc.data.Card;
import com.tenforwardconsulting.cordova.bgloc.data.CardDAO;

public class SQLiteCardDAO implements CardDAO {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String TAG = "LocationUpdateService";
	private Context context;
	
	public SQLiteCardDAO(Context context) {
		this.context = context;
	}
	
	public void internetPendingCards() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String user_id = pref.getString("user_id", "");
        String countQuery = "SELECT count(id) countPendings FROM pending_geo WHERE user_id = ?";
        SQLiteDatabase db = openDatabase("dbLifeshare.db");
        Cursor cursor = db.rawQuery(countQuery, new String[]{user_id});
		cursor.moveToFirst();
        int internetCards = cursor.getInt(cursor.getColumnIndex("countPendings"));
        cursor.close();
 		db.close();
 		SharedPreferences.Editor edit = pref.edit();
 		if (internetCards > 0){
 			edit.putBoolean("pendingInternet", true);
 		}else{
 			edit.putBoolean("pendingInternet", false);
 		}
 		Log.d(TAG, "RC = " + internetCards);
 		edit.commit();
    }

    //			INSERT EN BRIDGE
    /*public void internetPendingCards() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String user_id = pref.getString("user_id", "");
        String countQuery = "SELECT count(id) countPendings FROM pending_geo WHERE user_id = ?";
        SQLiteDatabase db = openDatabase("dbLifeshare.db");
        Cursor cursor = db.rawQuery(countQuery, new String[]{user_id});
		cursor.moveToFirst();
        int internetCards = cursor.getInt(cursor.getColumnIndex("countPendings"));
        cursor.close();
 		db.close();
 		SharedPreferences.Editor edit = pref.edit();
 		if (internetCards > 0){
 			edit.putBoolean("pendingInternet", true);
 		}else{
 			edit.putBoolean("pendingInternet", false);
 		}
 		Log.d(TAG, "RC = " + internetCards);
 		edit.commit();
    }*/

    private SQLiteDatabase openDatabase(String dbname) {
        //if (this.getDatabase(dbname) != null) {
	    //// TODO should wait for the db thread(s) to stop (!!)
        //    this.closeDatabase(dbname);
        //}

        File dbfile = context.getDatabasePath(dbname);

        if (!dbfile.exists()) {
            dbfile.getParentFile().mkdirs();
        }

        Log.v("info", "Open sqlite db: " + dbfile.getAbsolutePath());

        SQLiteDatabase mydb = SQLiteDatabase.openOrCreateDatabase(dbfile, null);

        return mydb;
    }

	public boolean persistCard(String tableName, Card card) {
		Log.d(TAG, "AAAA");
        SQLiteDatabase db = openDatabase("dbLifeshare.db");
        Log.d(TAG, "BBBB");
		//SQLiteDatabase db = new CardOpenHelper(context).getWritableDatabase();
		db.beginTransaction();
		Log.d(TAG, "CCCC");
		ContentValues values = getContentValues(card);
		Log.d(TAG, "DDDD");
		long rowId = db.insert(tableName, null, values);
		Log.d(TAG, "EEEE");
		Log.d(TAG, "After insert, rowId = " + rowId);
		db.setTransactionSuccessful();
		Log.d(TAG, "FFFF");
		db.endTransaction();
		Log.d(TAG, "GGGG");
		db.close();
		
		if (rowId > -1) {
			return true;
		} else {
			return false;
		}
	}
		
	private ContentValues getContentValues(Card card) {
		ContentValues values = new ContentValues();
		values.put("id", card.getId());
		values.put("created", card.getCreated());
		values.put("info", card.getInfo());
		values.put("location", card.getLocation());
		values.put("latitude", card.getLatitude());
		values.put("longitude", card.getLongitude());
		values.put("sharing_level",  card.getSharing_level());
		values.put("location_level", card.getLocation_level());
		values.put("user_id",  card.getUser_id());
		values.put("confirm", card.getConfirm());
		return values;
	}
}
