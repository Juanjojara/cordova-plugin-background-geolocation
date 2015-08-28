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
    public static final String INFO_ISNEAR = "info_isnear";
	public static final String INFO_UNAVAILABLE = "info_unavailable";

    public static final String PT_AIRPORT = "pt_airport";
    public static final String PT_AMUSEMENT_PARK = "pt_amusement_park";
    public static final String PT_AQUARIUM = "pt_aquarium";
    public static final String PT_ART_GALLERY = "pt_art_gallery";
    public static final String PT_BAKERY = "pt_bakery";
    public static final String PT_BUS_STATION = "pt_bus_station";
    public static final String PT_CAFE = "pt_cafe";
    public static final String PT_CAMPGROUND = "pt_campground";
    public static final String PT_CHURCH = "pt_church";
    public static final String PT_CITY_HALL = "pt_city_hall";
    public static final String PT_EMBASSY = "pt_embassy";
    public static final String PT_FOOD = "pt_food";
    public static final String PT_GROCERY_OR_SUPERMARKET = "pt_grocery_or_supermarket";
    public static final String PT_GYM = "pt_gym";
    public static final String PT_HEALTH = "pt_health";
    public static final String PT_HINDU_TEMPLE = "pt_hindu_temple";
    public static final String PT_LIBRARY = "pt_library";
    public static final String PT_LOCAL_GOVERNMENT_OFFICE = "pt_local_government_office";
    public static final String PT_LODGING = "pt_lodging";
    public static final String PT_MOSQUE = "pt_mosque";
    public static final String PT_MOVIE_THEATER = "pt_movie_theater";
    public static final String PT_MUSEUM = "pt_museum";
    public static final String PT_PARK = "pt_park";
    public static final String PT_PLACE_OF_WORSHIP = "pt_place_of_worship";
    public static final String PT_POST_OFFICE = "pt_post_office";
    public static final String PT_RESTAURANT = "pt_restaurant";
    public static final String PT_SCHOOL = "pt_school";
    public static final String PT_SHOPPING_MALL = "pt_shopping_mall";
    public static final String PT_SPA = "pt_spa";
    public static final String PT_STADIUM = "pt_stadium";
    public static final String PT_SUBWAY_STATION = "pt_subway_station";
    public static final String PT_SYNAGOGUE = "pt_synagogue";
    public static final String PT_TRAIN_STATION = "pt_train_station";
    public static final String PT_UNIVERSITY = "pt_university";
    public static final String PT_ZOO = "pt_zoo";

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
                result = getValueIT(key);
                break;
            case "ru":
                result = getValueRU(key);
                break;
            case "es":
                result = getValueES(key);
                break;
            case "pl":
                result = getValuePL(key);
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
            case INFO_ISNEAR:
                result = "is near the";
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
            case INFO_ISNEAR:
                result = "e’ vicino a";
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
            case INFO_ISNEAR:
                result = "está cerca de";
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
            case INFO_ISNEAR:
                result = "рядом с";
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

    private String getValuePL(String key){
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
            case INFO_ISNEAR:
                result = "jest w pobliżu";
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

    public String getValuePlaceType(String key){
        String result = "";
        switch (langCode.toLowerCase()) {
            case "it":
                result = getValuePlaceTypeIT(key);
                break;
            case "ru":
                result = getValuePlaceTypeRU(key);
                break;
            case "es":
                result = getValuePlaceTypeES(key);
                break;
            case "pl":
                result = getValuePlaceTypePL(key);
                break;
            default: 
                result = getValuePlaceTypeEN(key);
                break;
        }
        return result;
    }

    private String getValuePlaceTypeEN(String key){
        String result = "";
        switch (key.toLowerCase()) {
            case PT_AIRPORT:
                result = "airport";
                break;
            case PT_AMUSEMENT_PARK:
                result = "amusement park";
                break;
            case PT_AQUARIUM:
                result = "aquarium";
                break;
            case PT_ART_GALLERY:
                result = "art gallery";
                break;
            case PT_BAKERY:
                result = "bakery";
                break;
            case PT_BUS_STATION:
                result = "bus station";
                break;
            case PT_CAFE:
                result = "cafe";
                break;
            case PT_CAMPGROUND:
                result = "campground";
                break;
            case PT_CHURCH:
                result = "church";
                break;
            case PT_CITY_HALL:
                result = "city hall";
                break;
            case PT_EMBASSY:
                result = "embassy";
                break;
            case PT_FOOD:
                result = "food";
                break;
            case PT_GROCERY_OR_SUPERMARKET:
                result = "supermarket";
                break;
            case PT_GYM:
                result = "gym";
                break;
            case PT_HEALTH:
                result = "health";
                break;
            case PT_HINDU_TEMPLE:
                result = "hindu temple";
                break;
            case PT_LIBRARY:
                result = "library";
                break;
            case PT_LOCAL_GOVERNMENT_OFFICE:
                result = "local government office";
                break;
            case PT_LODGING:
                result = "lodging";
                break;
            case PT_MOSQUE:
                result = "mosque";
                break;
            case PT_MOVIE_THEATER:
                result = "movie theater";
                break;
            case PT_MUSEUM:
                result = "museum";
                break;
            case PT_PARK:
                result = "park";
                break;
            case PT_PLACE_OF_WORSHIP:
                result = "place of worship";
                break;
            case PT_POST_OFFICE:
                result = "post office";
                break;
            case PT_RESTAURANT:
                result = "restaurant";
                break;
            case PT_SCHOOL:
                result = "school";
                break;
            case PT_SHOPPING_MALL:
                result = "shopping mall";
                break;
            case PT_SPA:
                result = "spa";
                break;
            case PT_STADIUM:
                result = "stadium";
                break;
            case PT_SUBWAY_STATION:
                result = "subway station";
                break;
            case PT_SYNAGOGUE:
                result = "synagogue";
                break;
            case PT_TRAIN_STATION:
                result = "train station";
                break;
            case PT_UNIVERSITY:
                result = "university";
                break;
            case PT_ZOO:
                result = "zoo";
                break;
            default: 
                result = "unavailable";
                break;
        }
        return result;
    }

    private String getValuePlaceTypeES(String key){
        String result = "";
        switch (key.toLowerCase()) {
            case PT_AIRPORT:
                result = "aeropuerto";
                break;
            case PT_AMUSEMENT_PARK:
                result = "parque de atracciones";
                break;
            case PT_AQUARIUM:
                result = "acuario";
                break;
            case PT_ART_GALLERY:
                result = "galería de arte";
                break;
            case PT_BAKERY:
                result = "panadería";
                break;
            case PT_BUS_STATION:
                result = "estación de autobuses";
                break;
            case PT_CAFE:
                result = "café";
                break;
            case PT_CAMPGROUND:
                result = "camping";
                break;
            case PT_CHURCH:
                result = "iglesia";
                break;
            case PT_CITY_HALL:
                result = "ayuntamiento";
                break;
            case PT_EMBASSY:
                result = "embajada";
                break;
            case PT_FOOD:
                result = "comida";
                break;
            case PT_GROCERY_OR_SUPERMARKET:
                result = "supermercado";
                break;
            case PT_GYM:
                result = "gimnasio";
                break;
            case PT_HEALTH:
                result = "salud";
                break;
            case PT_HINDU_TEMPLE:
                result = "templo hindú";
                break;
            case PT_LIBRARY:
                result = "biblioteca";
                break;
            case PT_LOCAL_GOVERNMENT_OFFICE:
                result = "oficina del gobierno local";
                break;
            case PT_LODGING:
                result = "alojamiento";
                break;
            case PT_MOSQUE:
                result = "mezquita";
                break;
            case PT_MOVIE_THEATER:
                result = "cine";
                break;
            case PT_MUSEUM:
                result = "museo";
                break;
            case PT_PARK:
                result = "parque";
                break;
            case PT_PLACE_OF_WORSHIP:
                result = "lugar de adoración";
                break;
            case PT_POST_OFFICE:
                result = "oficina postal";
                break;
            case PT_RESTAURANT:
                result = "restaurante";
                break;
            case PT_SCHOOL:
                result = "escuela";
                break;
            case PT_SHOPPING_MALL:
                result = "centro comercial";
                break;
            case PT_SPA:
                result = "spa";
                break;
            case PT_STADIUM:
                result = "estadio";
                break;
            case PT_SUBWAY_STATION:
                result = "estación de metro";
                break;
            case PT_SYNAGOGUE:
                result = "sinagoga";
                break;
            case PT_TRAIN_STATION:
                result = "estación de tren";
                break;
            case PT_UNIVERSITY:
                result = "universidad";
                break;
            case PT_ZOO:
                result = "zoológico";
                break;
            default: 
                result = "No disponible";
                break;
        }
        return result;
    }

    private String getValuePlaceTypeIT(String key){
        String result = "";
        switch (key.toLowerCase()) {
            case PT_AIRPORT:
                result = "aeroporto";
                break;
            case PT_AMUSEMENT_PARK:
                result = "parco divertimento";
                break;
            case PT_AQUARIUM:
                result = "acquario";
                break;
            case PT_ART_GALLERY:
                result = "galleria d'arte";
                break;
            case PT_BAKERY:
                result = "panificio";
                break;
            case PT_BUS_STATION:
                result = "stazione degli autobus";
                break;
            case PT_CAFE:
                result = "caffè";
                break;
            case PT_CAMPGROUND:
                result = "campeggio";
                break;
            case PT_CHURCH:
                result = "chiesa";
                break;
            case PT_CITY_HALL:
                result = "municipio";
                break;
            case PT_EMBASSY:
                result = "ambasciata";
                break;
            case PT_FOOD:
                result = "cibo";
                break;
            case PT_GROCERY_OR_SUPERMARKET:
                result = "supermercato";
                break;
            case PT_GYM:
                result = "palestra";
                break;
            case PT_HEALTH:
                result = "salute";
                break;
            case PT_HINDU_TEMPLE:
                result = "tempio indù";
                break;
            case PT_LIBRARY:
                result = "biblioteca";
                break;
            case PT_LOCAL_GOVERNMENT_OFFICE:
                result = "ufficio del governo locale";
                break;
            case PT_LODGING:
                result = "alloggio";
                break;
            case PT_MOSQUE:
                result = "moschea";
                break;
            case PT_MOVIE_THEATER:
                result = "cinema";
                break;
            case PT_MUSEUM:
                result = "museo";
                break;
            case PT_PARK:
                result = "parco";
                break;
            case PT_PLACE_OF_WORSHIP:
                result = "luogo di culto";
                break;
            case PT_POST_OFFICE:
                result = "ufficio postale";
                break;
            case PT_RESTAURANT:
                result = "ristorante";
                break;
            case PT_SCHOOL:
                result = "scuola";
                break;
            case PT_SHOPPING_MALL:
                result = "centro commerciale";
                break;
            case PT_SPA:
                result = "terme";
                break;
            case PT_STADIUM:
                result = "stadio";
                break;
            case PT_SUBWAY_STATION:
                result = "stazione della metropolitana";
                break;
            case PT_SYNAGOGUE:
                result = "sinagoga";
                break;
            case PT_TRAIN_STATION:
                result = "stazione ferroviaria";
                break;
            case PT_UNIVERSITY:
                result = "università";
                break;
            case PT_ZOO:
                result = "zoologico";
                break;
            default: 
                result = "Non disponibile";
                break;
        }
        return result;
    }

    private String getValuePlaceTypePL(String key){
        String result = "";
        switch (key.toLowerCase()) {
            case PT_AIRPORT:
                result = "lotnisko";
                break;
            case PT_AMUSEMENT_PARK:
                result = "park rozrywki";
                break;
            case PT_AQUARIUM:
                result = "akwarium";
                break;
            case PT_ART_GALLERY:
                result = "galeria Sztuki";
                break;
            case PT_BAKERY:
                result = "piekarnia";
                break;
            case PT_BUS_STATION:
                result = "przystanek autobusowy";
                break;
            case PT_CAFE:
                result = "kawiarnia";
                break;
            case PT_CAMPGROUND:
                result = "obozowisko";
                break;
            case PT_CHURCH:
                result = "kościół";
                break;
            case PT_CITY_HALL:
                result = "ratusz";
                break;
            case PT_EMBASSY:
                result = "ambasada";
                break;
            case PT_FOOD:
                result = "jedzenie";
                break;
            case PT_GROCERY_OR_SUPERMARKET:
                result = "supermarket";
                break;
            case PT_GYM:
                result = "siłownia";
                break;
            case PT_HEALTH:
                result = "zdrowie";
                break;
            case PT_HINDU_TEMPLE:
                result = "mandir";
                break;
            case PT_LIBRARY:
                result = "biblioteka";
                break;
            case PT_LOCAL_GOVERNMENT_OFFICE:
                result = "lokalne biuro rząd";
                break;
            case PT_LODGING:
                result = "noclegi";
                break;
            case PT_MOSQUE:
                result = "meczet";
                break;
            case PT_MOVIE_THEATER:
                result = "kino";
                break;
            case PT_MUSEUM:
                result = "muzeum";
                break;
            case PT_PARK:
                result = "park";
                break;
            case PT_PLACE_OF_WORSHIP:
                result = "miejsce kultu";
                break;
            case PT_POST_OFFICE:
                result = "poczta";
                break;
            case PT_RESTAURANT:
                result = "restauracja";
                break;
            case PT_SCHOOL:
                result = "szkoła";
                break;
            case PT_SHOPPING_MALL:
                result = "centrum handlowe";
                break;
            case PT_SPA:
                result = "spa";
                break;
            case PT_STADIUM:
                result = "stadion";
                break;
            case PT_SUBWAY_STATION:
                result = "stacja metra";
                break;
            case PT_SYNAGOGUE:
                result = "synagoga";
                break;
            case PT_TRAIN_STATION:
                result = "dworzec kolejowy";
                break;
            case PT_UNIVERSITY:
                result = "uniwersytet";
                break;
            case PT_ZOO:
                result = "zoologiczny";
                break;
            default: 
                result = "niedostępny";
                break;
        }
        return result;
    }

    private String getValuePlaceTypeRU(String key){
        String result = "";
        switch (key.toLowerCase()) {
            case PT_AIRPORT:
                result = "аэропорт";
                break;
            case PT_AMUSEMENT_PARK:
                result = "парк развлечений";
                break;
            case PT_AQUARIUM:
                result = "океанариум";
                break;
            case PT_ART_GALLERY:
                result = "картинная галерея";
                break;
            case PT_BAKERY:
                result = "пекарня";
                break;
            case PT_BUS_STATION:
                result = "автовокзал";
                break;
            case PT_CAFE:
                result = "кафе";
                break;
            case PT_CAMPGROUND:
                result = "палаточный лагерь";
                break;
            case PT_CHURCH:
                result = "церковь";
                break;
            case PT_CITY_HALL:
                result = "городская администрация";
                break;
            case PT_EMBASSY:
                result = "посольство";
                break;
            case PT_FOOD:
                result = "еда";
                break;
            case PT_GROCERY_OR_SUPERMARKET:
                result = "супермаркет";
                break;
            case PT_GYM:
                result = "спортзал";
                break;
            case PT_HEALTH:
                result = "здоровье";
                break;
            case PT_HINDU_TEMPLE:
                result = "индуистский храм";
                break;
            case PT_LIBRARY:
                result = "библиотека";
                break;
            case PT_LOCAL_GOVERNMENT_OFFICE:
                result = "Муниципальное учреждение";
                break;
            case PT_LODGING:
                result = "жилье";
                break;
            case PT_MOSQUE:
                result = "мечеть";
                break;
            case PT_MOVIE_THEATER:
                result = "кинотеатр";
                break;
            case PT_MUSEUM:
                result = "музей";
                break;
            case PT_PARK:
                result = "парк";
                break;
            case PT_PLACE_OF_WORSHIP:
                result = "Место поклонения";
                break;
            case PT_POST_OFFICE:
                result = "почта";
                break;
            case PT_RESTAURANT:
                result = "ресторан";
                break;
            case PT_SCHOOL:
                result = "школа";
                break;
            case PT_SHOPPING_MALL:
                result = "торговый центр";
                break;
            case PT_SPA:
                result = "спа";
                break;
            case PT_STADIUM:
                result = "стадион";
                break;
            case PT_SUBWAY_STATION:
                result = "станция метро";
                break;
            case PT_SYNAGOGUE:
                result = "синагога";
                break;
            case PT_TRAIN_STATION:
                result = "железнодорожная станция";
                break;
            case PT_UNIVERSITY:
                result = "университет";
                break;
            case PT_ZOO:
                result = "зоопарк";
                break;
            default: 
                result = "недоступен";
                break;
        }
        return result;
    }
}
