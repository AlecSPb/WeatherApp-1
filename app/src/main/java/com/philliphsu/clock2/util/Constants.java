package com.philliphsu.clock2.util;

import android.content.Context;

import com.philliphsu.clock2.R;

public class Constants {
    private static  String[] WEATHER_CONDITION;
    public static String COND_TIME_MAP = "condTime";

    public static void setWeatherConditions(Context ctx) {
        WEATHER_CONDITION = new String[] {
                ctx.getResources().getString(R.string.rainy),
                ctx.getResources().getString(R.string.cloudy),
                ctx.getResources().getString(R.string.snowy),
                ctx.getResources().getString(R.string.foggy),
                ctx.getResources().getString(R.string.windy)};
    }

    public static String[] getWeatherCondition() {
        return WEATHER_CONDITION;
    }

    public static final String TIME_FORMAT = "HH:mm";
    public static final String COMMA = ",";
    public static final String APOSTROPHE = "'";
    public static final String COLON = ":";
    public static final String MIDNIGHT = "00:00";

    public static final int    NOTIFICATION_ID = 0;

    public static final String ALARM_SETTED = "Alarm set to: ";
    public static final String ALARM_ON = "alarm on";
    public static final String ALARM_OFF = "Alarm off!";
    public static final String EXTRA = "extra";

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
    public static final String MOBILE_DATA_ENABLED = "setMobileDataEnabled";
    public static final String STATE = "state";
    public static final String PREFERENCES = "preferences";
    public static final String ALARM_TO_OFF = "An alarm is going off!";
    public static final String CLICK_ME = "Click me!";
    public static final String ALARM_OFF2 = "alarm off";
    public static final String CELSIUS = "c";
    public static final String FAHRENHEIT = "f";
    public static final String QUERY = "query";
    public static final String COUNT = "count";
    public static final String NO_WEATHER_INFO = "No weather information found for ";
    public static final String CHANNEL = "channel";
    public static final int    GET_WEATHER_FROM_CURRENT_LOCATION = 0x00001;
    public static final String WEATHER_ALERT_LIST_KEY = "weather_alert_list_key";
    public static final String WEATHER_ALERT_LIST_CAT = "weather_alert_list_cat";
    public static final String GOOGLE_MAPS_ENDPOINT = "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s";
    public static final String YAHOO_QUERY = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\") and u='";
    public static final String YAHOO_ENDPOINT = "https://query.yahooapis.com/v1/public/yql?q=%s&format=json";
    public static final String CACHED_WEATHER_FILE = "weather.data";
    public static final float  VOLUME_INCREASE_STEP = 0.05f;
    public static final int    VOLUME_INCREASE_DELAY = 600;
    public static final int    ZERO = 0;

    // UTILS
    public static final String SET = "Set";
    public static final String CANCEL = "Cancel";

    public static final String DUMMY_VALUE = "0";
}
