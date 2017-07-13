package com.claudiofus.clock2.service;

import android.content.Context;
import android.location.Location;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.claudiofus.clock2.listener.GeocodingServiceListener;
import com.claudiofus.clock2.model.LocationResult;
import com.claudiofus.clock2.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoogleMapsGeocodingService {
    private final GeocodingServiceListener listener;
    private Exception error;

    public GoogleMapsGeocodingService(GeocodingServiceListener listener) {
        this.listener = listener;
    }

    public void refreshLocation(Context mContext, final Location location){
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String endpoint = String.format(Constants.GOOGLE_MAPS_ENDPOINT,
                location.getLatitude(), location.getLongitude(), Constants.API_KEY);
        JsonObjectRequest getRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject data) {
                        JSONArray results = data.optJSONArray(Constants.RESULTS);
                        if (results.length() == 0) {
                            error = new ReverseGeolocationException(Constants.GEOCODE_ERROR
                                    + location.getLatitude() + Constants.COMMA + location.getLongitude());
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
                        new ReverseGeolocationException(Constants.GEOCODE_ERROR
                                + location.getLatitude() + Constants.COMMA + location.getLongitude());
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
