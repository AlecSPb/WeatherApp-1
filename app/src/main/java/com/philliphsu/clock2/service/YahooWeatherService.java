package com.philliphsu.clock2.service;

import android.net.Uri;
import android.os.AsyncTask;

import com.philliphsu.clock2.listener.WeatherServiceListener;
import com.philliphsu.clock2.model.Channel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static com.philliphsu.clock2.util.Constants.APOSTROPHE;
import static com.philliphsu.clock2.util.Constants.CELSIUS;
import static com.philliphsu.clock2.util.Constants.CHANNEL;
import static com.philliphsu.clock2.util.Constants.COUNT;
import static com.philliphsu.clock2.util.Constants.FAHRENHEIT;
import static com.philliphsu.clock2.util.Constants.NO_WEATHER_INFO;
import static com.philliphsu.clock2.util.Constants.QUERY;
import static com.philliphsu.clock2.util.Constants.RESULTS;
import static com.philliphsu.clock2.util.Constants.YAHOO_ENDPOINT;
import static com.philliphsu.clock2.util.Constants.YAHOO_QUERY;


public class YahooWeatherService {
    private final WeatherServiceListener listener;
    private Exception error;
    private String temperatureUnit = CELSIUS.toUpperCase();

    public YahooWeatherService(WeatherServiceListener listener) {
        this.listener = listener;
    }

    private String getTemperatureUnit() {
        return temperatureUnit;
    }

    public void setTemperatureUnit(String temperatureUnit) {
        this.temperatureUnit = temperatureUnit;
    }

    public void refreshWeather(String location) {
        new AsyncTask<String, Void, Channel>() {
            @Override
            protected Channel doInBackground(String[] locations) {
                String location = locations[0];
                Channel channel = new Channel();
                String unit = getTemperatureUnit() != null
                        && getTemperatureUnit().equalsIgnoreCase(FAHRENHEIT) ? FAHRENHEIT : CELSIUS;
                String yql = String.format(YAHOO_QUERY + unit + APOSTROPHE, location);
                String endpoint = String.format(YAHOO_ENDPOINT, Uri.encode(yql));

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
                    JSONObject queryResults = data.optJSONObject(QUERY);
                    if (queryResults != null) {
                        if (queryResults.optInt(COUNT) == 0) {
                            error = new LocationWeatherException(
                                    NO_WEATHER_INFO + location);
                            return null;
                        }
                        JSONObject channelJSON =
                                queryResults.optJSONObject(RESULTS).optJSONObject(CHANNEL);
                        channel.populate(channelJSON);
                        return channel;
                    }
                } catch (Exception e) {
                    error = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Channel channel) {
                if (channel == null && error != null) {
                    listener.serviceFailure(error);
                } else {
                    listener.serviceSuccess(channel);
                }
            }
        }.execute(location);
    }

    private class LocationWeatherException extends Exception {
        LocationWeatherException(String detailMessage) {
            super(detailMessage);
        }
    }
}
