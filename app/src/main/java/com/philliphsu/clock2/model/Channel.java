package com.philliphsu.clock2.model;

import org.json.JSONException;
import org.json.JSONObject;

import static com.philliphsu.clock2.util.Constants.CITY;
import static com.philliphsu.clock2.util.Constants.COUNTRY;
import static com.philliphsu.clock2.util.Constants.ITEM;
import static com.philliphsu.clock2.util.Constants.LOCATION;
import static com.philliphsu.clock2.util.Constants.REGION;
import static com.philliphsu.clock2.util.Constants.REQUEST_LOCATION;
import static com.philliphsu.clock2.util.Constants.STRING_FORMAT;
import static com.philliphsu.clock2.util.Constants.UNITS;

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
        units.populate(data.optJSONObject(UNITS));
        item.populate(data.optJSONObject(ITEM));

        JSONObject locationData = data.optJSONObject(LOCATION);

        String region = locationData.optString(REGION);
        String country = locationData.optString(COUNTRY);

        location = String.format(STRING_FORMAT, locationData.optString(CITY),
                (region.length() != 0 ? region : country));
    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();

        try {
            data.put(UNITS, units.toJSON());
            data.put(ITEM, item.toJSON());
            data.put(REQUEST_LOCATION, location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
