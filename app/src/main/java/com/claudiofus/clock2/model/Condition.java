package com.claudiofus.clock2.model;

import com.claudiofus.clock2.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class Condition implements JSONPopulator {
    private int code;
    private int temperature;
    private int highTemperature;
    private int lowTemperature;
    private String description;
    private String day;

    public int getCode() {
        return code;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public void populate(JSONObject data) {
        code = data.optInt(Constants.CODE);
        temperature = data.optInt(Constants.TEMP);
        highTemperature = data.optInt(Constants.HIGH);
        lowTemperature = data.optInt(Constants.LOW);
        description = data.optString(Constants.TEXT);
        day = data.optString(Constants.DAY);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();

        try {
            data.put(Constants.CODE, code);
            data.put(Constants.TEMP, temperature);
            data.put(Constants.HIGH, highTemperature);
            data.put(Constants.LOW, lowTemperature);
            data.put(Constants.TEXT, description);
            data.put(Constants.DAY, day);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
