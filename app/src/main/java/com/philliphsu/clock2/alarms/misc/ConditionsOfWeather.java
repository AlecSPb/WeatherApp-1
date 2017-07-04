package com.philliphsu.clock2.alarms.misc;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public final class ConditionsOfWeather {
    private static final String TAG = "ConditionsOfWeather";

    private static final String SUNNY  = "sunny";
    private static final String RAINY  = "rainy";
    private static final String CLOUDY = "cloudy";
    private static final String SNOWY  = "snowy";
    private static final String FOGGY  = "foggy";
    private static final String WINDY  = "windy";
    public static final int NUM_WEATHER_CONDITIONS = 6;

    private static LinkedHashMap<String, String> CONDITIONS;

    public static LinkedHashMap<String, String> getInstance() {
        CONDITIONS = new LinkedHashMap<>(NUM_WEATHER_CONDITIONS);
        CONDITIONS.put(SUNNY, null);
        CONDITIONS.put(RAINY, null);
        CONDITIONS.put(CLOUDY, null);
        CONDITIONS.put(SNOWY, null);
        CONDITIONS.put(FOGGY, null);
        CONDITIONS.put(WINDY, null);

        Log.d(TAG, CONDITIONS.toString());
        return CONDITIONS;
    }

    public static String getConditionValue(String condition) throws IllegalStateException {
        if (CONDITIONS.containsKey(condition)) {
            return CONDITIONS.get(condition);
        } else {
            throw new IllegalStateException("Condition" + condition + " is not included in:" + CONDITIONS.keySet());
        }
    }

    public static LinkedHashMap<String, String> getConditions () {
        return CONDITIONS;
    }

    public static void setConditionValue (String condition, String value) throws IllegalStateException {
        if (CONDITIONS.containsKey(condition)) {
            CONDITIONS.put(condition, value);
        } else {
            throw new IllegalStateException("Condition" + condition + " is not included in:" + CONDITIONS.keySet());
        }
    }

    public static void removeConditionValue (String condition) throws IllegalStateException {
        if (CONDITIONS.containsKey(condition)) {
            CONDITIONS.put(condition, null);
        } else {
            throw new IllegalStateException("Condition" + condition + " is not included in:" + CONDITIONS.keySet());
        }
    }

    public static String getLabel(int position) {
        return new ArrayList<>(CONDITIONS.keySet()).get(position);
    }

    @Override
    public String toString() {
        return "ConditionsOfWeather{Conditions=" + CONDITIONS + "}";
    }
}