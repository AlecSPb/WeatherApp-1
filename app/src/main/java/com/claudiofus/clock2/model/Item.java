package com.claudiofus.clock2.model;

import com.claudiofus.clock2.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class Item implements JSONPopulator {
    private Condition condition;
    private Condition[] forecast;

    public Condition getCondition() {
        return condition;
    }
/*
    public Condition[] getForecast() {
        return forecast;
    }
*/
    @Override
    public void populate(JSONObject data) {
        condition = new Condition();
        condition.populate(data.optJSONObject(Constants.CONDITION));
/*
        JSONArray forecastData = data.optJSONArray("forecast");
        forecast = new Condition[forecastData.length()];

        for (int i = 0; i < forecastData.length(); i++) {
            forecast[i] = new Condition();
            try {
                forecast[i].populate(forecastData.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } */
    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();
        try {
            data.put(Constants.CONDITION, condition.toJSON());
//            data.put("forecast", new JSONArray(forecast));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
