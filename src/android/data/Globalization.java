package com.tenforwardconsulting.cordova.bgloc.data;

import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;


public class Globalization {
	private static final String TAG = "LocationUpdateService";
	private static final String NOT_TITLE = "Lifeshare Postcard";
	private static final String NOT_TEXT = "";
	private static final String INFO_ISIN = "is in";
	private static final String INFO_LUNCH = "is having lunch";
	private static final String INFO_SLEEP = "is sleeping";
	private static final String INFO_UNAVAILABLE = "unavailable";

	private String langCode;

	public Globalization(Context mContext){
		Configuration config = getResources().getConfiguration();
		Locale current = config.locale;
		langCode = current.getLanguage();
		Log.i(TAG, "Language Code: " + langCode);
	}

	public String getValue(String key){

	}
}
