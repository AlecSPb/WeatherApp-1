package com.philliphsu.clock2.service;

import android.location.Location;
import android.os.AsyncTask;

import com.philliphsu.clock2.listener.GeocodingServiceListener;
import com.philliphsu.clock2.model.LocationResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static com.philliphsu.clock2.util.Constants.API_KEY;
import static com.philliphsu.clock2.util.Constants.COMMA;
import static com.philliphsu.clock2.util.Constants.GEOCODE_ERROR;
import static com.philliphsu.clock2.util.Constants.GOOGLE_MAPS_ENDPOINT;
import static com.philliphsu.clock2.util.Constants.RESULTS;

public class GoogleMapsGeocodingService {
    private final GeocodingServiceListener listener;
    private Exception error;

    public GoogleMapsGeocodingService(GeocodingServiceListener listener) {
        this.listener = listener;
    }

    public void refreshLocation(Location location) {
        new AsyncTask<Location, Void, LocationResult>() {
            @Override
            protected LocationResult doInBackground(Location... locations) {

                Location location = locations[0];

                String endpoint = String.format(GOOGLE_MAPS_ENDPOINT,
                        location.getLatitude(), location.getLongitude(), API_KEY);

                try {
                    URL url = new URL(endpoint);

                    URLConnection connection = url.openConnection();
                    connection.setUseCaches(false);

                    InputStream inputStream = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();

                    JSONObject data = new JSONObject(result.toString());
                    JSONArray results = data.optJSONArray(RESULTS);

                    if (results.length() == 0) {
                        error = new ReverseGeolocationException(GEOCODE_ERROR
                                + location.getLatitude() + COMMA + location.getLongitude());
                        return null;
                    }
                    LocationResult locationResult = new LocationResult();
                    locationResult.populate(results.optJSONObject(0));

                    return locationResult;
                } catch (Exception e) {
                    error = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(LocationResult location) {

                if (location == null && error != null) {
                    listener.geocodeFailure();
                } else {
                    listener.geocodeSuccess(location);
                }
            }
        }.execute(location);
    }

    private class ReverseGeolocationException extends Exception {
        ReverseGeolocationException(String detailMessage) {
            super(detailMessage);
        }
    }
}
