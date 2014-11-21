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
		Configuration config = mContext.getResources().getConfiguration();
		Locale current = config.locale;
		langCode = current.getLanguage();
		Log.i(TAG, "Language Code: " + langCode);
	}

	public String getValue(String key){
		String result = "";
		switch (langCode.toLowerCase()) {
			case "it":
                result = "Lifeshare Postcard";
                break;
            case "ru":
                result = "Lifeshare Postcard";
                break;
            case "es":
                result = "Lifeshare Postcard";
                break;
            default: 
                result = getValueEN(key);
                break;
		}
		return result;
	}

	private String getValueEN(String key){
		String result = "";
		switch (key.toLowerCase()) {
            case "not_title":
                result = "Lifeshare Postcard";
                break;
            case "info_isin":
                result = "Lifeshare Postcard";
                break;
            case "info_lunch":
                result = "Lifeshare Postcard";
                break;
            case "info_sleep":
                result = "Lifeshare Postcard";
                break;
            case "info_unavailable":
                result = "Lifeshare Postcard";
                break;
            default: 
                result = "";
                break;
        }
		return result;
	}

	private String getValueIT(String key){
		String result = "";
		switch (key.toLowerCase()) {
            case "not_title":
                result = "Lifeshare Cartolina";
                break;
            case "info_isin":
                result = "e’ a";
                break;
            case "info_lunch":
                result = "sta pranzando";
                break;
            case "info_sleep":
                result = "sta dormendo";
                break;
            case "info_unavailable":
                result = "Non disponibile";
                break;
            default: 
                result = "";
                break;
        }
		return result;
	}

	private String getValueES(String key){
		String result = "";
		switch (key.toLowerCase()) {
            case "not_title":
                result = "Lifeshare Postal";
                break;
            case "info_isin":
                result = "está en";
                break;
            case "info_lunch":
                result = "está almorzando";
                break;
            case "info_sleep":
                result = "está durmiendo";
                break;
            case "info_unavailable":
                result = "No disponible";
                break;
            default: 
                result = "";
                break;
        }
		return result;
	}

	private String getValueRU(String key){
		String result = "";
		switch (key.toLowerCase()) {
            case "not_title":
                result = "Lifeshare открытка";
                break;
            case "info_isin":
                result = "в";
                break;
            case "info_lunch":
                result = "обедаю";
                break;
            case "info_sleep":
                result = "сплю";
                break;
            case "info_unavailable":
                result = "недоступен";
                break;
            default: 
                result = "";
                break;
        }
		return result;
	}
}
