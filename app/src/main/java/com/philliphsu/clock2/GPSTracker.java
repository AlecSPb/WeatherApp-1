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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import com.philliphsu.clock2.listener.GeocodingServiceListener;
import com.philliphsu.clock2.listener.WeatherServiceListener;
import com.philliphsu.clock2.model.*;
import com.philliphsu.clock2.service.GoogleMapsGeocodingService;
import com.philliphsu.clock2.service.WeatherCacheService;
import com.philliphsu.clock2.service.YahooWeatherService;
import com.philliphsu.clock2.util.Constants;

import butterknife.Bind;

public class GPSTracker implements WeatherServiceListener,
        GeocodingServiceListener, LocationListener {
    private final Context mContext;
    private YahooWeatherService weatherService;
    private WeatherCacheService cacheService;
    private GoogleMapsGeocodingService geocodingService;
    private boolean weatherServicesHasFailed = false;
    private SharedPreferences preferences = null;
    @Bind(R.id.temperatureTextView) TextView temperatureTv;
    @Bind(R.id.conditionTextView) TextView conditionTv;
    @Bind(R.id.locationTextView) TextView locationTv;

    public static final String
            ACTION_LOCATION_BROADCAST = GPSTracker.class.getName() + "LocationBroadcast";

    public GPSTracker(Context context) {
        this.mContext = context;

        // Initialize services
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        weatherService = new YahooWeatherService(this);
        weatherService.setTemperatureUnit(preferences.getString(
                mContext.getString(R.string.pref_temperature_unit_key), null));
        cacheService = new WeatherCacheService(mContext);
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
            weatherService.refreshWeather(location);
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

    @Override
    public void serviceSuccess(Channel channel) {
        Condition condition = channel.getItem().getCondition();
        Units units = channel.getUnits();
        String weatherTemperature = mContext.getString(
                R.string.temperature_output, condition.getTemperature(), units.getTemperature());
        String weatherCondition = condition.getDescription();
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

    @Override
    public void geocodeSuccess(LocationResult location) {
        weatherService.refreshWeather(location.getAddress());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(mContext.getString(R.string.pref_cached_location_key), location.getAddress());
        editor.apply();
    }

    @Override
    public void geocodeFailure() {
        // GeoCoding failed, try loading weather data from the cache
        cacheService.load(this);
    }
}