package com.philliphsu.clock2.service;

import android.content.Context;
import android.location.Location;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.philliphsu.clock2.listener.GeocodingServiceListener;
import com.philliphsu.clock2.model.LocationResult;

import org.json.JSONArray;
import org.json.JSONObject;

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

    public void refreshLocation(Context mContext, final Location location){
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String endpoint = String.format(GOOGLE_MAPS_ENDPOINT,
                location.getLatitude(), location.getLongitude(), API_KEY);
        JsonObjectRequest getRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject data) {
                        JSONArray results = data.optJSONArray(RESULTS);
                        if (results.length() == 0) {
                            error = new ReverseGeolocationException(GEOCODE_ERROR
                                    + location.getLatitude() + COMMA + location.getLongitude());
                        }
                        LocationResult locationResult = new LocationResult();
                        locationResult.populate(results.optJSONObject(0));
                        if (error != null) {
                            listener.geocodeFailure();
                        } else {
                            listener.geocodeSuccess(locationResult);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        new ReverseGeolocationException(GEOCODE_ERROR
                                + location.getLatitude() + COMMA + location.getLongitude());
                    }
                });
        queue.add(getRequest);
    }

    private class ReverseGeolocationException extends Exception {
        ReverseGeolocationException(String detailMessage) {
            super(detailMessage);
        }
    }
}
