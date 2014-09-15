package com.tenforwardconsulting.cordova.bgloc.data;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.os.SystemClock;
import android.util.Log;

import com.tenforwardconsulting.cordova.bgloc.data.CardDAO;
import com.tenforwardconsulting.cordova.bgloc.data.sqlite.SQLiteCardDAO;

public class Card {
	private static final String TAG = "LocationUpdateService";

	private long created;
	private String info;
	private String location;
	private String latitude;
	private String longitude;
	private String sharing_level;
	private String location_level;
	private String user_id;
	private String confirm;
	
	private int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getCreated() {
		return created;
	}
	public void setCreated(long created) {
		this.created = created;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getSharing_level() {
		return sharing_level;
	}
	public void setSharing_level(String sharing_level) {
		this.sharing_level = sharing_level;
	}
	public String getLocation_level() {
		return location_level;
	}
	public void setLocation_level(String location_level) {
		this.location_level = location_level;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getConfirm() {
		return confirm;
	}
	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
	
	public static Card createCard(android.location.Location originalLocation, Context context, String userId, int cardId) {
		Card card = new Card();
		//SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences pref = context.getSharedPreferences("lifesharePreferences", Context.MODE_MULTI_PROCESS);
		card.setId(cardId);
		card.setCreated(new Date().getTime());
		card.setInfo("");
		card.setLocation("");
		card.setLongitude(String.valueOf(originalLocation.getLongitude()));
		card.setLatitude(String.valueOf(originalLocation.getLatitude()));
		card.setSharing_level(pref.getString("sharing_setting", ""));
		Log.i(TAG, "Sha Set: " + pref.getString("sharing_setting", ""));
		card.setLocation_level(pref.getString("location_setting", ""));
		Log.i(TAG, "Loc Set: " + pref.getString("location_setting", ""));
		card.setUser_id(userId);
		String confirmationDlg = "false";
        if (!pref.getString("sharing_setting", "").equals("automatic"))
        	confirmationDlg = "true";
		card.setConfirm(confirmationDlg);
		Log.i(TAG, "Conf Set: " + confirmationDlg);
		return card;
	}
}
