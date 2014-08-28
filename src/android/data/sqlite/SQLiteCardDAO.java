package com.tenforwardconsulting.cordova.bgloc.data.sqlite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tenforwardconsulting.cordova.bgloc.data.Card;
import com.tenforwardconsulting.cordova.bgloc.data.CardDAO;

public class SQLiteCardDAO implements CardDAO {
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String TAG = "SQLiteLocationDAO";
	private Context context;
	
	public SQLiteCardDAO(Context context) {
		this.context = context;
	}
	
	public int getContactsCount() {
        String countQuery = "SELECT count(id) countPendings FROM pending_geo WHERE user_id = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }

	public Card[] getInternetPendingCards() {
		SQLiteDatabase db = null;
		Cursor c = null;
		List<Card> all = new ArrayList<Card>();
		try {
			db = new CardOpenHelper(context).getReadableDatabase();
			c = db.query("pending_geo", null, null, null, null, null, null);
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

	public boolean persistLocation(String tableName, Card card) {
		SQLiteDatabase db = new CardOpenHelper(context).getWritableDatabase();
		db.beginTransaction();
		ContentValues values = getContentValues(card);
		long rowId = db.insert(tableName, null, values);
		Log.d(TAG, "After insert, rowId = " + rowId);
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		if (rowId > -1) {
			return true;
		} else {
			return false;
		}
	}
		
	private Card hydrate(Cursor c) {
		Card l = new Card();
		l.setId(c.getLong(c.getColumnIndex("id")));
		l.setRecordedAt(stringToDate(c.getString(c.getColumnIndex("recordedAt"))));
		l.setLatitude(c.getString(c.getColumnIndex("latitude")));
		l.setLongitude(c.getString(c.getColumnIndex("longitude")));
		l.setAccuracy(c.getString(c.getColumnIndex("accuracy")));
		l.setSpeed(c.getString(c.getColumnIndex("speed")));

		l.setCreated(ts);
		l.setInfo(info);
		l.setLocation(loc);
		l.setLongitude(lon);
		l.setLatitude(lat);
		l.setSharing_level(sharLEvel);
		l.setLocation_level(locLevel);
		l.setUser_id(userId);
		l.setConfirm(conf);
		
		return l;
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
