package com.philliphsu.clock2.model;

import org.json.JSONObject;

interface JSONPopulator {
    void populate(JSONObject data);

    JSONObject toJSON();
}
