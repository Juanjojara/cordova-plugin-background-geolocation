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
	
	public Card[] geoPendingCards() {
		SQLiteDatabase db = null;
		Cursor c = null;
		List<Card> all = new ArrayList<Card>();
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		String user_id = pref.getString("user_id", "");
		try {
			db = openDatabase("dbLifeshare.db");
			c = db.query("pending_geo", new String[]{"id", "created", "info", "location", "latitude", "longitude", "sharing_level", "location_level", "user_id", "confirm"}, "user_id = ?", new String[]{user_id}, null, null, "id");
			while (c.moveToNext()) {
				all.add(hydrate(c));
			}
		} finally {
			if (c != null) {
				c.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return all.toArray(new Card[all.size()]);
	}

	public void geoCards() {
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

    //			INSERT IN BRIDGE
    public int getCardId() {
        SQLiteDatabase db = openDatabase("dbLifeshare.db");
        //GET CURRENT ID
        String idQuery = "SELECT id FROM bridge";
        Cursor cursor = db.rawQuery(idQuery, null);
		cursor.moveToFirst();
        int currentId = cursor.getInt(cursor.getColumnIndex("id"));
        cursor.close();
        //UPDATE ID
        ContentValues args = new ContentValues();
		args.put("id", currentId+1);
        int updRows = db.update("bridge", args, null, null);
 		db.close();
 		Log.i(TAG, "ID = " + (currentId+1) + " Rows affected = " + updRows);

 		return currentId;
    }

    private SQLiteDatabase openDatabase(String dbname) {
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
		//SQLiteDatabase db = new CardOpenHelper(context).getWritableDatabase();
		db.beginTransaction();
		ContentValues values = getContentValues(card);
		long rowId = db.insert(tableName, null, values);
		Log.d(TAG, "After insert, rowId = " + rowId);
		db.setTransactionSuccessful();
		db.endTransaction();
        Log.d(TAG, "BBBB");
		db.close();
		
		if (rowId > -1) {
			return true;
		} else {
			return false;
		}
	}

	public void deleteCard(String tableName, Card card) {
		//SQLiteDatabase db = new LocationOpenHelper(context).getWritableDatabase();
		SQLiteDatabase db = openDatabase("dbLifeshare.db");
		db.beginTransaction();
		db.delete(tableName, "id = ?", new String[]{Integer.toString(card.getId())});
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	private Card hydrate(Cursor c) {
		Card card = new Card();

		card.setId(c.getInt(c.getColumnIndex("id")));
		card.setCreated(c.getLong(c.getColumnIndex("created")));
		card.setInfo(c.getString(c.getColumnIndex("info")));
		card.setLocation(c.getString(c.getColumnIndex("location")));
		card.setLatitude(c.getString(c.getColumnIndex("latitude")));
		card.setLongitude(c.getString(c.getColumnIndex("longitude")));
		card.setSharing_level(c.getString(c.getColumnIndex("sharing_level")));
		card.setLocation_level(c.getString(c.getColumnIndex("location_level")));
		card.setUser_id(c.getString(c.getColumnIndex("user_id")));
		card.setConfirm(c.getString(c.getColumnIndex("confirm")));
		
		return card;
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
