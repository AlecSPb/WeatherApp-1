/*
 * Copyright 2017 Phillip Hsu
 *
 * This file is part of ClockPlus.
 *
 * ClockPlus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ClockPlus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ClockPlus.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.philliphsu.clock2.model;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.philliphsu.clock2.util.Constants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by Phillip Hsu on 5/30/2016.
 */
public final class WeatherConditions {
    private static final String TAG = "WeatherConditions";
    public static final int NUM_CONDITIONS  = 5;

    public static final LinkedHashMap<String, String> CONDITIONS = new LinkedHashMap<>();
    private static WeatherConditions sInstance;

    @VisibleForTesting
    private WeatherConditions() {
        for (String cond : Constants.getWeatherCondition()){
            CONDITIONS.put(cond, "");
        }
    }

    public static WeatherConditions getInstance() {
        if (sInstance == null) {
            sInstance = new WeatherConditions();
        }
        Log.d(TAG, sInstance.toString());
        return sInstance;
    }

    public String getLabel(int position) {
        return new ArrayList<>(CONDITIONS.keySet()).get(position);
    }

    public Set<String> getKeySet() {
        return CONDITIONS.keySet();
    }

    @Override
    public String toString() {
        String out = "";
        for (String cond: CONDITIONS.keySet()){
            out += "key: \"" + cond + "\" value: \"" + CONDITIONS.get(cond) + "\" - ";
        }
        return "WeatherConditions{" + "CONDITIONS=" + out + "}";
    }
}
