package com.tenforwardconsulting.cordova.bgloc.data;

import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;


public class Globalization {
	public static final String TAG = "LocationUpdateService";
	public static final String NOT_TITLE = "not_title";
	public static final String NOT_BTN_CONFIRM = "not_btn_confirm";
	public static final String NOT_BTN_DISCARD = "not_btn_discard";
	public static final String INFO_ISIN = "info_isin";
	public static final String INFO_LUNCH = "info_lunch";
	public static final String INFO_SLEEP = "info_sleep";
	public static final String INFO_UNAVAILABLE = "info_unavailable";

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
                result = getValueIT(key);;
                break;
            case "ru":
                result = getValueRU(key);;
                break;
            case "es":
                result = getValueES(key);;
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
            case NOT_TITLE:
                result = "Lifeshare Postcard";
                break;
            case INFO_ISIN:
                result = "is in";
                break;
            case INFO_LUNCH:
                result = "is having lunch";
                break;
            case INFO_SLEEP:
                result = "is sleeping";
                break;
            case INFO_UNAVAILABLE:
                result = "unavailable";
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
