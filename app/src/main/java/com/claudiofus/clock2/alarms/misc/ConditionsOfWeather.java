package com.claudiofus.clock2.alarms.misc;

import com.claudiofus.clock2.util.Constants;

import java.util.ArrayList;

public final class ConditionsOfWeather {

    private static final String TAG = "ConditionsOfWeather";
    private static final ArrayList<String> WEATHER_COND = Constants.getWeatherCondition();

    public static final String SUNNY  = WEATHER_COND.get(0);
    public static final String RAINY  = WEATHER_COND.get(1);
    public static final String CLOUDY = WEATHER_COND.get(2);
    public static final String SNOWY  = WEATHER_COND.get(3);
    public static final String FOGGY  = WEATHER_COND.get(4);
    public static final String WINDY  = WEATHER_COND.get(5);
    public static final int NUM_WEATHER_CONDITIONS = 6;
}