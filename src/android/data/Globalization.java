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
            case "po":
                result = getValuePO(key);;
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
            case NOT_BTN_CONFIRM:
                result = "Confirm";
                break;
            case NOT_BTN_DISCARD:
                result = "Discard";
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
            case NOT_TITLE:
                result = "Lifeshare Cartolina";
                break;
            case NOT_BTN_CONFIRM:
                result = "Confermare";
                break;
            case NOT_BTN_DISCARD:
                result = "Scartare";
                break;
            case INFO_ISIN:
                result = "e’ a";
                break;
            case INFO_LUNCH:
                result = "sta pranzando";
                break;
            case INFO_SLEEP:
                result = "sta dormendo";
                break;
            case INFO_UNAVAILABLE:
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
            case NOT_TITLE:
                result = "Lifeshare Postal";
                break;
            case NOT_BTN_CONFIRM:
                result = "Confirmar";
                break;
            case NOT_BTN_DISCARD:
                result = "Descartar";
                break;
            case INFO_ISIN:
                result = "está en";
                break;
            case INFO_LUNCH:
                result = "está almorzando";
                break;
            case INFO_SLEEP:
                result = "está durmiendo";
                break;
            case INFO_UNAVAILABLE:
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
            case NOT_TITLE:
                result = "Lifeshare открытка";
                break;
            case NOT_BTN_CONFIRM:
                result = "подтвердить";
                break;
            case NOT_BTN_DISCARD:
                result = "отбрасывать";
                break;
            case INFO_ISIN:
                result = "в";
                break;
            case INFO_LUNCH:
                result = "обедаю";
                break;
            case INFO_SLEEP:
                result = "сплю";
                break;
            case INFO_UNAVAILABLE:
                result = "недоступен";
                break;
            default: 
                result = "";
                break;
        }
		return result;
	}

    private String getValuePO(String key){
        String result = "";
        switch (key.toLowerCase()) {
            case NOT_TITLE:
                result = "Lifeshare pocztówka";
                break;
            case NOT_BTN_CONFIRM:
                result = "potwierdzać";
                break;
            case NOT_BTN_DISCARD:
                result = "odrzucać";
                break;
            case INFO_ISIN:
                result = "jest w";
                break;
            case INFO_LUNCH:
                result = "je obiad";
                break;
            case INFO_SLEEP:
                result = "śpi";
                break;
            case INFO_UNAVAILABLE:
                result = "niedostępny";
                break;
            default: 
                result = "";
                break;
        }
        return result;
    }
}
