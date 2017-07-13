package com.claudiofus.clock2.model;

import com.claudiofus.clock2.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

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
            data.put(Constants.TEMPERATURE, temperature);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
