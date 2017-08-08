package com.claudiofus.clock2.alarms.misc;

import com.claudiofus.clock2.util.Constants;

import java.util.ArrayList;

public final class ConditionsOfWeather {

    private static final String TAG = "ConditionsOfWeather";
    private static ArrayList<String> WEATHER_COND;

//    public static final String SUNNY  = WEATHER_COND.get(0);
//    public static final String RAINY  = WEATHER_COND.get(1);
//    public static final String CLOUDY = WEATHER_COND.get(2);
//    public static final String SNOWY  = WEATHER_COND.get(3);
//    public static final String FOGGY  = WEATHER_COND.get(4);
//    public static final String WINDY  = WEATHER_COND.get(5);
    public static String SUNNY  = null;
    public static String RAINY  = null;
    public static String CLOUDY = null;
    public static String SNOWY  = null;
    public static String FOGGY  = null;
    public static String WINDY  = null;
    public static final int NUM_WEATHER_CONDITIONS = 6;

    public ConditionsOfWeather() {
        WEATHER_COND = Constants.getWeatherCondition();
        setSUNNY();
        setRAINY();
        setCLOUDY();
        setSNOWY();
        setFOGGY();
        setWINDY();
    }

    public static String getSUNNY() {
        return SUNNY;
    }

    public static String getRAINY() {
        return RAINY;
    }

    public static String getCLOUDY() {
        return CLOUDY;
    }

    public static String getSNOWY() {
        return SNOWY;
    }

    public static String getFOGGY() {
        return FOGGY;
    }

    public static String getWINDY() {
        return WINDY;
    }

    public static void setSUNNY() {
        if (WEATHER_COND.size() > 0 && WEATHER_COND.get(0) != null)
            SUNNY = WEATHER_COND.get(0);
    }

    public static void setRAINY() {
        if (WEATHER_COND.size() > 0 && WEATHER_COND.get(1) != null)
            RAINY = WEATHER_COND.get(1);
    }

    public static void setCLOUDY() {
        if (WEATHER_COND.size() > 0 && WEATHER_COND.get(2) != null)
            CLOUDY = WEATHER_COND.get(2);
    }

    public static void setSNOWY() {
        if (WEATHER_COND.size() > 0 && WEATHER_COND.get(3) != null)
            SNOWY = WEATHER_COND.get(3);
    }

    public static void setFOGGY() {
        if (WEATHER_COND.size() > 0 && WEATHER_COND.get(4) != null)
            FOGGY = WEATHER_COND.get(4);
    }

    public static void setWINDY() {
        if (WEATHER_COND.size() > 0 && WEATHER_COND.get(5) != null)
            WINDY = WEATHER_COND.get(5);
    }
}