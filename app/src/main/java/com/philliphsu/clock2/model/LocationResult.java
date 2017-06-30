package com.philliphsu.clock2.model;

import org.json.JSONException;
import org.json.JSONObject;

import static com.philliphsu.clock2.util.Constants.FORMATTED_ADDRESS;

public class LocationResult implements JSONPopulator {
    private String address;

    public String getAddress() {
        return address;
    }

    @Override
    public void populate(JSONObject data) {
        address = data.optString(FORMATTED_ADDRESS);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();

        try {
            data.put(FORMATTED_ADDRESS, address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
