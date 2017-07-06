package com.philliphsu.clock2.model;

import java.util.ArrayList;
import java.util.Arrays;

import static com.philliphsu.clock2.util.Constants.getWeatherCondition;

public class WeatherMapper {
    private Integer[] sunny  = {31,32,33,34,36,44};
    private Integer[] rainy  = {0,1,2,3,4,5,6,7,8,9,10,11,12,17,19,35,37,38,39,40,45,47};
    private Integer[] cloudy = {26,27,28,29,30};
    private Integer[] snowy  = {13,14,15,16,18,25,41,42,43,46};
    private Integer[] foggy  = {20,21,22};
    private Integer[] windy  = {23,24};

    private ArrayList<Integer> sunnyList  = new ArrayList<>(Arrays.asList(sunny));
    private ArrayList<Integer> rainyList  = new ArrayList<>(Arrays.asList(rainy));
    private ArrayList<Integer> cloudyList = new ArrayList<>(Arrays.asList(cloudy));
    private ArrayList<Integer> snowyList  = new ArrayList<>(Arrays.asList(snowy));
    private ArrayList<Integer> foggyList  = new ArrayList<>(Arrays.asList(foggy));
    private ArrayList<Integer> windyList  = new ArrayList<>(Arrays.asList(windy));

    public String getConditionFromCode(int code){
        if (sunnyList.contains(code)){
            return getWeatherCondition().get(0);
        } else if (rainyList.contains(code)){
            return getWeatherCondition().get(1);
        } else if (cloudyList.contains(code)){
            return getWeatherCondition().get(2);
        } else if (snowyList.contains(code)) {
            return getWeatherCondition().get(3);
        } else if (foggyList.contains(code)) {
            return getWeatherCondition().get(4);
        } else if (windyList.contains(code)) {
            return getWeatherCondition().get(5);
        }
        return "";
    }
}