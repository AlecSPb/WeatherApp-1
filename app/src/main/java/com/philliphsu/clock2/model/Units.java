package com.philliphsu.clock2.model;

import org.json.JSONException;
import org.json.JSONObject;

import static com.philliphsu.clock2.util.Constants.TEMPERATURE;

public class Units implements JSONPopulator {
    private String temperature;

    public String getTemperature() {
        return temperature;
    }

    @Override
    public void populate(JSONObject data) {
        temperature = data.optString("temperature");
    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();

        try {
            data.put(TEMPERATURE, temperature);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
