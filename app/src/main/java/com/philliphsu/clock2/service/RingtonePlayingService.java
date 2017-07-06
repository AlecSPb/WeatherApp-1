package com.philliphsu.clock2.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.philliphsu.clock2.GPSTracker;
import com.philliphsu.clock2.MainActivity;
import com.philliphsu.clock2.R;
import com.philliphsu.clock2.listener.GeocodingServiceListener;
import com.philliphsu.clock2.model.LocationResult;

import static com.philliphsu.clock2.util.Constants.ALARM_OFF2;
import static com.philliphsu.clock2.util.Constants.ALARM_ON;
import static com.philliphsu.clock2.util.Constants.ALARM_TO_OFF;
import static com.philliphsu.clock2.util.Constants.CLICK_ME;
import static com.philliphsu.clock2.util.Constants.NOTIFICATION_ID;
import static com.philliphsu.clock2.util.Constants.STATE;
import static com.philliphsu.clock2.util.Constants.VOLUME_INCREASE_DELAY;
import static com.philliphsu.clock2.util.Constants.VOLUME_INCREASE_STEP;

public class RingtonePlayingService extends Service implements
        /*WeatherServiceListener,*/ GeocodingServiceListener, LocationListener {

    private int startId;
    private MediaPlayer mediaPlayer;
    private Ringtone defaultRingtone;
    private boolean isRunning;
    /*private YahooWeatherService weatherService;*/
    private SharedPreferences preferences;
    private WeatherCacheService cacheService;
    /*private boolean weatherServicesHasFailed = false;*/
    private GoogleMapsGeocodingService geocodingService;
    private Vibrator vibrator;
    private float mVolumeLevel;
    private Handler mHandler = new Handler();
    private int maxVolume;
    private String weatherCondition;

    private Runnable mVolumeRunnable = new Runnable() {
        @Override
        public void run() {
            // increase volume level until reach max value
            if (mediaPlayer != null && mVolumeLevel < maxVolume) {
                mVolumeLevel += VOLUME_INCREASE_STEP;
                mediaPlayer.setVolume(mVolumeLevel, mVolumeLevel);
                // set next increase in 600ms
                mHandler.postDelayed(mVolumeRunnable, VOLUME_INCREASE_DELAY);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        /*weatherService = new YahooWeatherService(this);
        weatherService.setTemperatureUnit(preferences.getString(getString(R.string.pref_temperature_unit_key), null));*/
        cacheService = new WeatherCacheService(this);
        geocodingService = new GoogleMapsGeocodingService(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        /*String location = null;

        if (preferences.getBoolean(getString(R.string.pref_geolocation_enabled_key), true)) {
            String locationCache = preferences.getString(getString(R.string.pref_cached_location_key), null);

            if (locationCache == null) {
                getWeatherFromCurrentLocation();
            } else {
                location = locationCache;
            }
        } else {
            location = preferences.getString(getString(R.string.pref_manual_location_key), null);
        }

        if (location != null) {
            weatherService.refreshWeather(location);
        }*/

        // fetch the extra string from the alarm on/alarm off values
        Bundle bundle = intent.getExtras();
        String state = bundle != null && bundle.containsKey(STATE) ? bundle.getString(STATE) : null;
//        TODO confrontare "preferences" del bundle con "weatherCondition"
//        ArrayList<String> preference = null;
//        if (bundle != null && bundle.containsKey(PREFERENCES)) {
//            preference = bundle.getStringArrayList(PREFERENCES);
//        }

        NotificationManager notifyManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Intent intentMainActivity = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntentMainActivity = PendingIntent.getActivity(this, 0,
                intentMainActivity, 0);

        Notification notificationPopup = new Notification.Builder(this)
                .setContentTitle(ALARM_TO_OFF)
                .setContentText(CLICK_ME)
                .setSmallIcon(R.drawable.ic_clock_24dp)
                .setContentIntent(pendingIntentMainActivity)
                .setAutoCancel(true)
                .build();

        // this converts the extra strings from the intent
        // to start IDs, values 0 or 1
        if (state != null) {
            switch (state) {
                case ALARM_ON:
                    startId = 1;
                    break;
                case ALARM_OFF2:
                    startId = 0;
                    Log.e("Start ID is ", state);
                    break;
                default:
                    startId = 0;
                    break;
            }
        }

        // if there is no music playing, and the user pressed "alarm on"
        // music should start playing
        if (!this.isRunning && startId == 1) {
            Log.e("there is no music, ", "and you want start");

            this.isRunning = true;
            this.startId = 0;

            // set up the start command for the notification
            notifyManager.notify(NOTIFICATION_ID, notificationPopup);

            // Check if there's vibration enabled
            if (preferences.getBoolean(getString(R.string.pref_vibration_enabled_key), false)) {
                long[] pattern = { 1000, 100 };
                vibrator.vibrate(pattern, 0);
            }

            AudioManager audioManager =
                    (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
            Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                    this.getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
            mediaPlayer = MediaPlayer.create(getApplicationContext(), defaultRingtoneUri);

            // Check if there's gradual awakening enabled
            if (mediaPlayer != null) {
                if (preferences.getBoolean(getString(R.string.pref_gradual_awakening_key), false)) {
                    mHandler.postDelayed(mVolumeRunnable, VOLUME_INCREASE_DELAY);
                } else {
                    mediaPlayer.setVolume(maxVolume, maxVolume);
                }
                mediaPlayer.start();
            }
        }

        // if there is music playing, and the user pressed "alarm off"
        // music should stop playing
        else if (this.isRunning && startId == 0) {
            Log.e("there is music, ", "and you want end");

            // stop the ringtone
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            vibrator.cancel();

            this.isRunning = false;
            this.startId = 0;
        }

        // these are if the user presses random buttons just to bug-proof the app
        // if there is no music playing, and the user pressed "alarm off", do nothing
        else if (!this.isRunning) {
            Log.e("there is no music, ", "and you want end");

            this.isRunning = false;
            this.startId = 0;
        }

        // if there is music playing and the user pressed "alarm on", do nothing
        else {
            Log.e("there is music, ", "and you want start");

            this.isRunning = true;
            this.startId = 1;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning = false;
    }

    private void getWeatherFromCurrentLocation() {
        Context ctx = getApplicationContext();
        int accessFine = ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION);
        int accessCoarse = ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (accessFine == PackageManager.PERMISSION_GRANTED
                && accessCoarse == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

//    @Override
//    public void serviceSuccess(Channel channel) {
//        Condition condition = channel.getItem().getCondition();
//        weatherCondition = condition.getDescription();
//
//        cacheService.save(channel);
//    }
//
//    @Override
//    public void serviceFailure(Exception exception) {
//        // display error if this is the second failure
//        if (weatherServicesHasFailed) {
//            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
//        } else {
//            // error doing reverse geocoding, load weather data from cache
//            weatherServicesHasFailed = true;
//            // OPTIONAL: let the user know an error has occurred then fallback to the cached data
//            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
//
//            cacheService.load(this);
//        }
//    }

    @Override
    public void geocodeSuccess(LocationResult location) {
//        weatherService.refreshWeather(location.getAddress());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.pref_cached_location_key), location.getAddress());
        editor.apply();

        new GPSTracker(getApplicationContext());
    }

    @Override
    public void geocodeFailure() {
        // GeoCoding failed, try loading weather data from the cache
        //TODO USE VOLLEY HERE ALSO
//        cacheService.load(this);
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
}
