package com.claudiofus.clock2.listener;

import com.claudiofus.clock2.model.LocationResult;

public interface GeocodingServiceListener {
    void geocodeSuccess(LocationResult location);

    void geocodeFailure();
}
