package com.tenforwardconsulting.cordova.bgloc.data;

import java.util.Date;

import android.os.SystemClock;


public class Card {
	private int created;
	private String info;
	private String location;
	private String latitude;
	private String longitude;
	private String sharing_level;
	private String location_level;
	private String user_id;
	private Bool confirm;
	
	private int id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCreated() {
		return created;
	}
	public void setCreated(int created) {
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
	public Bool getConfirm() {
		return confirm;
	}
	public void setConfirm(Bool confirm) {
		this.confirm = confirm;
	}
	
	public static Card createCard(int ts, String info, String loc, String lat, String lon, String sharLEvel, String locLevel, String userId, Bool conf) {
		Card card = new Card();
		card.setCreated(ts);
		card.setInfo(info);
		card.setLocation(loc);
		card.setLongitude(lon);
		card.setLatitude(lat);
		card.setSharing_level(sharLEvel);
		card.setLocation_level(locLevel);
		card.setUser_id(userId);
		card.setConfirm(conf);

		return card;
	}
}
