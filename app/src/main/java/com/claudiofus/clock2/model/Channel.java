package com.claudiofus.clock2.model;

import com.claudiofus.clock2.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class Channel implements JSONPopulator {
    private Units units;
    private Item item;
    private String location;

    public Units getUnits() {
        return units;
    }

    public Item getItem() {
        return item;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public void populate(JSONObject data) {

        units = new Units();
        item = new Item();
        units.populate(data.optJSONObject(Constants.UNITS));
        item.populate(data.optJSONObject(Constants.ITEM));

        JSONObject locationData = data.optJSONObject(Constants.LOCATION);

        String region = locationData.optString(Constants.REGION);
        String country = locationData.optString(Constants.COUNTRY);

        location = String.format(Constants.STRING_FORMAT, locationData.optString(Constants.CITY),
                (region.length() != 0 ? region : country));
    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();

        try {
            data.put(Constants.UNITS, units.toJSON());
            data.put(Constants.ITEM, item.toJSON());
            data.put(Constants.REQUEST_LOCATION, location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
