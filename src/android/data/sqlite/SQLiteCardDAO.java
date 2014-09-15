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
		//SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences pref = context.getSharedPreferences("lifesharePreferences", Context.MODE_MULTI_PROCESS);
		String user_id = pref.getString("user_id", "");
		try {
			db = new CardOpenHelper(context).getReadableDatabase();
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
		//SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences pref = context.getSharedPreferences("lifesharePreferences", Context.MODE_MULTI_PROCESS);
		String user_id = pref.getString("user_id", "");
        String countQuery = "SELECT count(id) countPendings FROM pending_geo WHERE user_id = ?";
        SQLiteDatabase db = new CardOpenHelper(context).getReadableDatabase();
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
        SharedPreferences pref = context.getSharedPreferences("lifesharePreferences", Context.MODE_MULTI_PROCESS);
        int currentId = pref.getInt("cardId", 1);

        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("cardId", currentId+1);
        edit.commit();
        
 		return currentId;
    }

	public boolean persistCard(String tableName, Card card) {
		Log.d(TAG, "AAAA");
		long rowId = -1;
		try {
			SQLiteDatabase db = new CardOpenHelper(context).getWritableDatabase();
			db.beginTransaction();
			ContentValues values = getContentValues(card);
			rowId = db.insert(tableName, null, values);
			Log.d(TAG, "After insert, rowId = " + rowId + ". Card ID: " + card.getId() + ". Table: " + tableName);
			db.setTransactionSuccessful();
			db.endTransaction();
			db.close();
		} catch (Exception e) {
        	Log.d(TAG, "ERROR "+e.toString());
		}
		Log.d(TAG, "BBBB");

		if (rowId > -1) {
			return true;
		} else {
			return false;
		}
	}

	public void deleteCard(String tableName, Card card) {
		SQLiteDatabase db = new CardOpenHelper(context).getWritableDatabase();
		db.beginTransaction();
		db.delete(tableName, "id = ?", new String[]{Integer.toString(card.getId())});
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	public void closeDB(){
		CardOpenHelper cardOH = new CardOpenHelper(context);
		cardOH.close();
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
