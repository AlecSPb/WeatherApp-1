package com.claudiofus.clock2.model;

import com.claudiofus.clock2.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationResult implements JSONPopulator {
    private String address;

    public String getAddress() {
        return address;
    }

    @Override
    public void populate(JSONObject data) {
        address = data.optString(Constants.FORMATTED_ADDRESS);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();

        try {
            data.put(Constants.FORMATTED_ADDRESS, address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
