package com.philliphsu.clock2.model;

import org.json.JSONException;
import org.json.JSONObject;

import static com.philliphsu.clock2.util.Constants.CODE;
import static com.philliphsu.clock2.util.Constants.DAY;
import static com.philliphsu.clock2.util.Constants.HIGH;
import static com.philliphsu.clock2.util.Constants.LOW;
import static com.philliphsu.clock2.util.Constants.TEMP;
import static com.philliphsu.clock2.util.Constants.TEXT;

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
        code = data.optInt(CODE);
        temperature = data.optInt(TEMP);
        highTemperature = data.optInt(HIGH);
        lowTemperature = data.optInt(LOW);
        description = data.optString(TEXT);
        day = data.optString(DAY);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();

        try {
            data.put(CODE, code);
            data.put(TEMP, temperature);
            data.put(HIGH, highTemperature);
            data.put(LOW, lowTemperature);
            data.put(TEXT, description);
            data.put(DAY, day);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
