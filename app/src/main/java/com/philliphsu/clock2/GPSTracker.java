package com.philliphsu.clock2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.philliphsu.clock2.listener.GeocodingServiceListener;
import com.philliphsu.clock2.model.Channel;
import com.philliphsu.clock2.model.Condition;
import com.philliphsu.clock2.model.LocationResult;
import com.philliphsu.clock2.model.Units;
import com.philliphsu.clock2.service.GoogleMapsGeocodingService;

import org.json.JSONObject;

import static com.philliphsu.clock2.util.Constants.APOSTROPHE;
import static com.philliphsu.clock2.util.Constants.CHANNEL;
import static com.philliphsu.clock2.util.Constants.COUNT;
import static com.philliphsu.clock2.util.Constants.QUERY;
import static com.philliphsu.clock2.util.Constants.RESULTS;
import static com.philliphsu.clock2.util.Constants.YAHOO_ENDPOINT;
import static com.philliphsu.clock2.util.Constants.YAHOO_QUERY;

public class GPSTracker implements /*WeatherServiceListener,*/
        GeocodingServiceListener, LocationListener {
    private final Context mContext;
    /*private YahooWeatherService weatherService;
    private WeatherCacheService cacheService;*/
    private GoogleMapsGeocodingService geocodingService;
    /*private boolean weatherServicesHasFailed = false;*/
    private SharedPreferences preferences = null;

    public static final String
            ACTION_LOCATION_BROADCAST = GPSTracker.class.getName() + "LocationBroadcast",
            EXTRA_TEMPERATURE = "extra_temperature",
            EXTRA_CONDITION = "extra_condition",
            EXTRA_LOCATION = "extra_location";

    public GPSTracker(Context context) {
        this.mContext = context;

        // Initialize services
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        /*weatherService = new YahooWeatherService(this);
        weatherService.setTemperatureUnit(preferences.getString(
                mContext.getString(R.string.pref_temperature_unit_key), null));
        cacheService = new WeatherCacheService(mContext);*/
        geocodingService = new GoogleMapsGeocodingService(this);

        setup();
    }

    private void setup() {
        String location = null;

        if (preferences.getBoolean(mContext.getString(R.string.pref_geolocation_enabled_key), true)) {
            String locationCache = preferences.getString(mContext.getString(R.string.pref_cached_location_key), null);

            if (locationCache == null) {
                getLocation();
            } else {
                location = locationCache;
            }
        } else {
            location = preferences.getString(mContext.getString(R.string.pref_manual_location_key), null);
        }

        if (location != null) {
            //weatherService.refreshWeather(location);
            refreshWeather(location);
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)mContext, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, 0x00001);

            return;
        }

        LocationManager locationManager =
                (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Criteria locationCriteria = new Criteria();

        if (isNetworkEnabled) {
            locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        } else if (isGPSEnabled) {
            locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        }
        locationManager.requestSingleUpdate(locationCriteria, this, null);
    }

    @Override
    public void onLocationChanged(Location location) {
        geocodingService.refreshLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    /*@Override
    public void serviceSuccess(Channel channel) {
        Condition condition = channel.getItem().getCondition();
        Units units = channel.getUnits();
        String weatherTemperature = mContext.getString(
                R.string.temperature_output, condition.getTemperature(), units.getTemperature());
        String weatherCondition = condition.getDescription().toLowerCase();
        String weatherLocation = channel.getLocation();

        Log.i("GPSTracker-Condition", condition.toJSON().toString());
        Log.i("GPSTracker-Units", units.toJSON().toString());
        Log.i("GPSTracker-Temp", weatherTemperature);
        Log.i("GPSTracker-Cond", weatherCondition);
        Log.i("GPSTracker-Loc", weatherLocation);
        cacheService.save(channel);

        // send the information to ui
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(mContext.getString(R.string.pref_temperature_key), weatherTemperature);
        editor.putString(mContext.getString(R.string.pref_condition_key), weatherCondition);
        editor.putString(mContext.getString(R.string.pref_location_key), weatherLocation);
        editor.apply();
    }

    @Override
    public void serviceFailure(Exception exception) {
        // display error if this is the second failure
//        if (weatherServicesHasFailed) {
//            Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_LONG).show();
//        } else {
//            // error doing reverse geocoding, load weather data from cache
//            weatherServicesHasFailed = true;
//            // OPTIONAL: let the user know an error has occurred then fallback to the cached data
//            Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
//
//            cacheService.load(this);
//        }

        if (!weatherServicesHasFailed) {
            weatherServicesHasFailed = true;
            cacheService.load(this);
        }
    }
    */

    @Override
    public void geocodeSuccess(LocationResult location) {
        /*weatherService.refreshWeather(location.getAddress());*/
        refreshWeather(location.getAddress());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(mContext.getString(R.string.pref_cached_location_key), location.getAddress());
        editor.apply();
    }

    @Override
    public void geocodeFailure() {
        // GeoCoding failed, try loading weather data from the cache
        //TODO USE VOLLEY HERE ALSO
        //cacheService.load(this);
    }

    public void refreshWeather(String location) {
        // Create a single queue
        final Context ctx = this.mContext;
        RequestQueue queue = Volley.newRequestQueue(ctx);

        String unit = preferences.getString(
                mContext.getString(R.string.pref_temperature_unit_key), null);
        String yql = String.format(YAHOO_QUERY + unit + APOSTROPHE, location);
        String endpoint = String.format(YAHOO_ENDPOINT, Uri.encode(yql));

        JsonObjectRequest getRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject data) {
                        JSONObject queryResults = data.optJSONObject(QUERY);
                        if (queryResults != null) {
                            if (queryResults.optInt(COUNT) == 0) {
                                Toast.makeText(ctx, "ERRORE", Toast.LENGTH_LONG).show();
                            } else {
                                Channel channel = new Channel();
                                JSONObject channelJSON =
                                        queryResults.optJSONObject(RESULTS).optJSONObject(CHANNEL);
                                channel.populate(channelJSON);

                                Condition condition = channel.getItem().getCondition();
                                Units units = channel.getUnits();
                                String weatherTemperature = mContext.getString(
                                        R.string.temperature_output, condition.getTemperature(), units.getTemperature());
                                String weatherCondition = condition.getDescription().toLowerCase();
                                String weatherLocation = channel.getLocation();
                                sendBroadcastMessage(weatherTemperature, weatherCondition, weatherLocation);
                                Log.i("GPSTracker-Condition", condition.toJSON().toString());
                                Log.i("GPSTracker-Units", units.toJSON().toString());
                                Log.i("GPSTracker-Temp", weatherTemperature);
                                Log.i("GPSTracker-Cond", weatherCondition);
                                Log.i("GPSTracker-Loc", weatherLocation);
                            }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });

        // add it to the Request Queue
        queue.add(getRequest);
    }

    private void sendBroadcastMessage(String temperature,
                                      String condition,
                                      String location) {
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_TEMPERATURE, temperature);
        intent.putExtra(EXTRA_CONDITION, condition);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}