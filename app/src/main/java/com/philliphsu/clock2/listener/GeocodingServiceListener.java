package com.philliphsu.clock2.listener;

import com.philliphsu.clock2.model.LocationResult;

public interface GeocodingServiceListener {
    void geocodeSuccess(LocationResult location);

    void geocodeFailure();
}
