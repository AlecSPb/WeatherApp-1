package com.philliphsu.clock2.util;

import android.content.Context;

import com.philliphsu.clock2.R;

import java.util.ArrayList;

public class Constants {
    private static ArrayList<String> WEATHER_CONDITION = new ArrayList<>();

    public static final String TIME_FORMAT = "HH:mm";
    public static final String COMMA = ",";
    public static final String APOSTROPHE = "'";

    // CHANNEL
    public static final String UNITS = "units";
    public static final String ITEM = "item";
    public static final String LOCATION = "location";
    public static final String REGION = "region";
    public static final String COUNTRY = "country";
    public static final String CITY = "city";
    public static final String REQUEST_LOCATION = "requestLocation";
    public static final String STRING_FORMAT = "%s, %s";

    // CONDITION
    public static final String CODE = "code";
    public static final String TEMP = "temp";
    public static final String HIGH = "high";
    public static final String LOW = "low";
    public static final String TEXT = "text";
    public static final String DAY = "day";

    // ITEM
    public static final String CONDITION = "condition";

    // LOCATIONRESULT
    public static final String FORMATTED_ADDRESS = "formatted_address";

    // UNITS
    public static final String TEMPERATURE = "temperature";

    // SERVICES
    public static final String RESULTS = "results";
    public static final String GEOCODE_ERROR = "Could not reverse geocode ";
    public static final String API_KEY = "";
    public static final String QUERY = "query";
    public static final String COUNT = "count";
    public static final String CHANNEL = "channel";
    public static final String GOOGLE_MAPS_ENDPOINT = "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s";
    public static final String YAHOO_QUERY = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") and u='";
    public static final String YAHOO_ENDPOINT = "https://query.yahooapis.com/v1/public/yql?q=%s&format=json";
    public static final String CACHED_WEATHER_FILE = "weather.data";
    public static final float  VOLUME_INCREASE_STEP = 0.20f;
    public static final int    VOLUME_INCREASE_DELAY = 500;

    public static void setWeatherConditions(Context ctx) {
        WEATHER_CONDITION.add(ctx.getResources().getString(R.string.sunny));
        WEATHER_CONDITION.add(ctx.getResources().getString(R.string.rainy));
        WEATHER_CONDITION.add(ctx.getResources().getString(R.string.windy));
        WEATHER_CONDITION.add(ctx.getResources().getString(R.string.cloudy));
        WEATHER_CONDITION.add(ctx.getResources().getString(R.string.snowy));
        WEATHER_CONDITION.add(ctx.getResources().getString(R.string.foggy));
    }

    public static ArrayList<String> getWeatherCondition() {
        return WEATHER_CONDITION;
    }
}
