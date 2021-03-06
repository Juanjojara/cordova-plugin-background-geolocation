package com.tenforwardconsulting.cordova.bgloc.data;

import android.content.Context;

import com.tenforwardconsulting.cordova.bgloc.data.sqlite.SQLiteLocationDAO;
import com.tenforwardconsulting.cordova.bgloc.data.sqlite.SQLiteCardDAO;

public abstract class DAOFactory {
	public static LocationDAO createLocationDAO(Context context) {
		//Very basic for now
		return new SQLiteLocationDAO(context);
	}

	public static CardDAO createCardDAO(Context context) {
		//Very basic for now
		return new SQLiteCardDAO(context);
	}
}
