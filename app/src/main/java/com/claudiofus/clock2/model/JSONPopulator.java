package com.claudiofus.clock2.model;

import org.json.JSONObject;

interface JSONPopulator {
    void populate(JSONObject data);

    JSONObject toJSON();
}
