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

package com.philliphsu.clock2.util;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.text.format.DateFormat.getTimeFormat;

/**
 * Created by Phillip Hsu on 6/3/2016.
 */
public final class TimeFormatUtils {

    private TimeFormatUtils() {}

    public static String formatTime(Context context, long millis) {
        return getTimeFormat(context).format(new Date(millis));
    }

    public static String formatTime(Context context, int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        return formatTime(context, cal.getTimeInMillis());
    }

    public static int getHour(String time) {
        String[] timeArr = time.split(":");
        return Integer.parseInt(timeArr[0].trim());
    }

    public static int getMinutes(String time) {
        if (time.contains("AM") || time.contains("PM")){
            time = time.replace("AM","").replace("PM","");
        }
        String[] timeArr = time.split(":");
        return Integer.parseInt(timeArr[1].trim());
    }

    public static String timeFromMillis(long millis){
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;
        return String.format(Locale.getDefault(),"%02d:%02d:%02d", hour, minute, second);
    }
}
