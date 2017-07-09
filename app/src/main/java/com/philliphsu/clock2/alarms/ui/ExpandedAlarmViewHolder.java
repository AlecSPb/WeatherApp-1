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

package com.philliphsu.clock2.alarms.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.philliphsu.clock2.GPSTracker;
import com.philliphsu.clock2.R;
import com.philliphsu.clock2.alarms.Alarm;
import com.philliphsu.clock2.alarms.misc.AlarmController;
import com.philliphsu.clock2.alarms.misc.DaysOfWeek;
import com.philliphsu.clock2.dialogs.RingtonePickerDialog;
import com.philliphsu.clock2.dialogs.RingtonePickerDialogController;
import com.philliphsu.clock2.dialogs.TimePickerDialogController;
import com.philliphsu.clock2.list.OnListItemInteractionListener;
import com.philliphsu.clock2.timepickers.Utils;
import com.philliphsu.clock2.util.FragmentTagUtils;
import com.philliphsu.clock2.util.TimeFormatUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Phillip Hsu on 7/31/2016.
 */
public class ExpandedAlarmViewHolder extends BaseAlarmViewHolder {
    private static final String TAG = "ExpandedAlarmViewHolder";

    @Bind(R.id.ok) Button mOk;
    @Bind(R.id.delete) Button mDelete;
    @Bind(R.id.ringtone) Button mRingtone;
    @Bind(R.id.vibrate) TempCheckableImageButton mVibrate;
    @Bind({R.id.day0, R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6})
    ToggleButton[] mDays;
    @Bind({R.id.sunny, R.id.rain, R.id.cloud, R.id.snow, R.id.fog, R.id.wind})
    ToggleButton[] mWeatherCondition;
    @Bind(R.id.temperatureTextView) TextView temperature;
    @Bind(R.id.conditionTextView) TextView condition;
    @Bind(R.id.locationTextView) TextView location;
    @Bind({R.id.sunnyTime, R.id.rainTime, R.id.cloudTime, R.id.snowTime, R.id.fogTime, R.id.windTime})
    TextView[] mWeatherTime;
    @Bind(R.id.weatherConditionsTime) LinearLayout weatherConditionsTime;

    private final ColorStateList mDayToggleColors;
    private final ColorStateList mVibrateColors;
    private final ColorStateList mWeatherCondToggleColors;
    private final RingtonePickerDialogController mRingtonePickerController;

    public ExpandedAlarmViewHolder(ViewGroup parent, final OnListItemInteractionListener<Alarm> listener,
                                   AlarmController controller) {
        super(parent, R.layout.item_expanded_alarm, listener, controller);
        // Manually bind listeners, or else you'd need to write a getter for the
        // OnListItemInteractionListener in the BaseViewHolder for use in method binding.
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onListItemDeleted(getAlarm());
            }
        });
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since changes are persisted as soon as they are made, there's really
                // nothing we have to persist here. Let the listener know we should
                // collapse this VH.
                // While this works, it also makes an update to the DB and thus reschedules
                // the alarm, so the snackbar will show up as well. We want to avoid that..
//                listener.onListItemUpdate(getAlarm(), getAdapterPosition());
                // TODO: This only works because we know what the implementation looks like..
                // This is bad because we just made the proper function of this dependent
                // on the implementation.
                listener.onListItemClick(getAlarm(), getAdapterPosition());
            }
        });

        // https://code.google.com/p/android/issues/detail?id=177282
        // https://stackoverflow.com/questions/15673449/is-it-confirmed-that-i-cannot-use-themed-color-attribute-in-color-state-list-res
        // Programmatically create the ColorStateList for our day toggles using themed color
        // attributes, "since prior to M you can't create a themed ColorStateList from XML but you
        // can from code." (quote from google)
        // The first array level is analogous to an XML node defining an item with a state list.
        // The second level lists all the states considered by the item from the first level.
        // An empty list of states represents the default stateless item.
        int[][] states = {
                /*item 1*/{/*states*/android.R.attr.state_checked},
                /*item 2*/{/*states*/}
        };
        // TODO: Phase out Utils.getColorFromThemeAttr because it doesn't work for text colors.
        // WHereas getTextColorFromThemeAttr works for both regular colors and text colors.
        int[] dayToggleColors = {
                /*item 1*/Utils.getTextColorFromThemeAttr(getContext(), R.attr.colorAccent),
                /*item 2*/Utils.getTextColorFromThemeAttr(getContext(), android.R.attr.textColorHint)
        };
        int[] vibrateColors = {
                /*item 1*/Utils.getTextColorFromThemeAttr(getContext(), R.attr.colorAccent),
                /*item 2*/Utils.getTextColorFromThemeAttr(getContext(), R.attr.themedIconTint)
        };
        int[] weatherCondToggleColors = {
                /*item 1*/Utils.getTextColorFromThemeAttr(getContext(), R.attr.colorAccent),
                /*item 2*/Utils.getTextColorFromThemeAttr(getContext(), android.R.attr.textColorHint)
        };
        mDayToggleColors = new ColorStateList(states, dayToggleColors);
        mVibrateColors = new ColorStateList(states, vibrateColors);
        mWeatherCondToggleColors = new ColorStateList(states, weatherCondToggleColors);

        mRingtonePickerController = new RingtonePickerDialogController(mFragmentManager,
                new RingtonePickerDialog.OnRingtoneSelectedListener() {
                    @Override
                    public void onRingtoneSelected(Uri ringtoneUri) {
                        Log.d(TAG, "Selected ringtone: " + ringtoneUri.toString());
                        final Alarm oldAlarm = getAlarm();
                        Alarm newAlarm = oldAlarm.toBuilder()
                                .ringtone(ringtoneUri.toString())
                                .build();
                        oldAlarm.copyMutableFieldsTo(newAlarm);
                        persistUpdatedAlarm(newAlarm, false);
                    }
                }
        );

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)getContext(), new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            }, 2);
        }
    }

    @Override
    public void onBind(Alarm alarm) {
        super.onBind(alarm);
        mRingtonePickerController.tryRestoreCallback(makeTag(R.id.ringtone));
        bindDays(alarm);
        bindRingtone();
        bindVibrate(alarm.vibrates());
        bindWeatherCondition(alarm);
    }

    @Override
    protected void bindLabel(boolean visible, String label) {
        super.bindLabel(true, label);
    }

    @OnClick(R.id.ringtone)
    void showRingtonePickerDialog() {
//        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
//        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
//                .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
//                // The ringtone to show as selected when the dialog is opened
//                .putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, getSelectedRingtoneUri())
//                // Whether to show "Default" item in the list
//                .putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
//        // The ringtone that plays when default option is selected
//        //.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, DEFAULT_TONE);
//        // TODO: This is VERY BAD. Use a Controller/Presenter instead.
//        // The result will be delivered to MainActivity, and then delegated to AlarmsFragment.
//        ((Activity) getContext()).startActivityForResult(intent, AlarmsFragment.REQUEST_PICK_RINGTONE);

        mRingtonePickerController.show(getSelectedRingtoneUri(), makeTag(R.id.ringtone));
    }

    @OnClick(R.id.vibrate)
    void onVibrateToggled() {
        final boolean checked = mVibrate.isChecked();
        if (checked) {
            Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(300);
        }
        final Alarm oldAlarm = getAlarm();
        Alarm newAlarm = oldAlarm.toBuilder()
                .vibrates(checked)
                .build();
        oldAlarm.copyMutableFieldsTo(newAlarm);
        persistUpdatedAlarm(newAlarm, false);
    }

    @OnClick({ R.id.day0, R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6 })
    void onDayToggled(ToggleButton view) {
        final Alarm oldAlarm = getAlarm();
        Alarm newAlarm = oldAlarm.toBuilder().build();
        oldAlarm.copyMutableFieldsTo(newAlarm);
        // ---------------------------------------------------------------------------------
        // TOneverDO: precede copyMutableFieldsTo()
        int position = ((ViewGroup) view.getParent()).indexOfChild(view);
        int weekDayAtPosition = DaysOfWeek.getInstance(getContext()).weekDayAt(position);
        Log.d(TAG, "Day toggle #" + position + " checked changed. This is weekday #"
                + weekDayAtPosition + " relative to a week starting on Sunday");
        newAlarm.setRecurring(weekDayAtPosition, view.isChecked());
        // ---------------------------------------------------------------------------------
        persistUpdatedAlarm(newAlarm, true);
    }

    @OnClick({R.id.sunny, R.id.rain, R.id.cloud, R.id.snow, R.id.fog, R.id.wind})
    void onWeatherCondToggled(ToggleButton view) {
        for (TextView tv : mWeatherTime){
            tv.setText(getContext().getString(R.string.not_set));
        }

        final int position = ((ViewGroup) view.getParent()).indexOfChild(view);
        final String conditionAtPosition = getAlarm().getLabel(position);
        final Alarm oldAlarm = getAlarm();

        // If I'm activating a weather condition the timepicker will appear
        if (view.isChecked()) {
            final String selectedCond = (String) view.getTextOn();
            AppCompatActivity activity = (AppCompatActivity) getContext();
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            TimePickerDialogController mTimePickerDialogController = new TimePickerDialogController(
                    fragmentManager, getContext(), new BottomSheetTimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(ViewGroup viewGroup, int hourOfDay, int minute) {
                    String formattedTime = TimeFormatUtils.formatTime(getContext(), hourOfDay, minute);
                    oldAlarm.setWeatherCondition(selectedCond, formattedTime);
                    persistUpdatedAlarm(oldAlarm, false);

                    mWeatherTime[position].setText(formattedTime);
                    weatherConditionsTime.setVisibility(View.VISIBLE);
                }
            });
            mTimePickerDialogController.show(0, 0, null);
        } else {
            oldAlarm.removeWeatherCondition(conditionAtPosition);
            mWeatherTime[position].setText(getContext().getString(R.string.not_set));
            checkWeatherTimeVisibility();
            persistUpdatedAlarm(oldAlarm, false);
        }
    }

    private void bindDays(Alarm alarm) {
        for (int i = 0; i < mDays.length; i++) {
            mDays[i].setTextColor(mDayToggleColors);
            int weekDay = DaysOfWeek.getInstance(getContext()).weekDayAt(i);
            String label = DaysOfWeek.getLabel(weekDay);
            mDays[i].setTextOn(label);
            mDays[i].setTextOff(label);
            mDays[i].setChecked(alarm.isRecurring(weekDay));
        }
    }

    private void bindRingtone() {
        int iconTint = Utils.getTextColorFromThemeAttr(getContext(), R.attr.themedIconTint);

        Drawable ringtoneIcon = mRingtone.getCompoundDrawablesRelative()[0/*start*/];
        ringtoneIcon = DrawableCompat.wrap(ringtoneIcon.mutate());
        DrawableCompat.setTint(ringtoneIcon, iconTint);
        mRingtone.setCompoundDrawablesRelativeWithIntrinsicBounds(ringtoneIcon, null, null, null);

        String title = RingtoneManager.getRingtone(getContext(),
                getSelectedRingtoneUri()).getTitle(getContext());
        mRingtone.setText(title);
    }

    private void bindVibrate(boolean vibrates) {
        Utils.setTintList(mVibrate, mVibrate.getDrawable(), mVibrateColors);
        mVibrate.setChecked(vibrates);
    }

    private void bindWeatherCondition(Alarm alarm) {
        for (TextView tv : mWeatherTime){
            tv.setText(getContext().getString(R.string.not_set));
        }

        for (int i = 0; i < mWeatherCondition.length; i++) {
            mWeatherCondition[i].setTextColor(mWeatherCondToggleColors);
            String label = alarm.getLabel(i);
            mWeatherCondition[i].setTextOn(label);
            mWeatherCondition[i].setTextOff(label);
            if (alarm.getWeatherCondition(label) != null){
                mWeatherCondition[i].setChecked(true);
                mWeatherTime[i].setText(alarm.getWeatherCondition(label));
            } else {
                mWeatherCondition[i].setChecked(false);
            }
        }

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (temperature != null)
                            temperature.setText(intent.getStringExtra(GPSTracker.EXTRA_TEMPERATURE));
                        if (condition != null)
                            condition.setText(intent.getStringExtra(GPSTracker.EXTRA_CONDITION));
                        if (location != null)
                            location.setText(intent.getStringExtra(GPSTracker.EXTRA_LOCATION));
                    }
                }, new IntentFilter(GPSTracker.ACTION_LOCATION_BROADCAST)
        );

        checkWeatherTimeVisibility();
    }

    private void checkWeatherTimeVisibility() {
        boolean showPanel = false;
        for (ToggleButton toggleBtn : mWeatherCondition) {
            if (toggleBtn.isChecked()){
                showPanel = true;
                break;
            }
        }
        weatherConditionsTime.setVisibility(showPanel ? View.VISIBLE : View.INVISIBLE);
    }

    private Uri getSelectedRingtoneUri() {
        // If showing an item for "Default" (@see EXTRA_RINGTONE_SHOW_DEFAULT), this can be one
        // of DEFAULT_RINGTONE_URI, DEFAULT_NOTIFICATION_URI, or DEFAULT_ALARM_ALERT_URI to have the
        // "Default" item checked.
        //
        // Otherwise, use RingtoneManager.getActualDefaultRingtoneUri() to get the "actual sound URI".
        //
        // Do not use RingtoneManager.getDefaultUri(), because that just returns one of
        // DEFAULT_RINGTONE_URI, DEFAULT_NOTIFICATION_URI, or DEFAULT_ALARM_ALERT_URI
        // depending on the type requested (i.e. what the docs calls "symbolic URI
        // which will resolved to the actual sound when played").
        String ringtone = getAlarm().ringtone();
        return ringtone.isEmpty() ?
                RingtoneManager.getActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_ALARM)
                : Uri.parse(ringtone);
    }

    private String makeTag(@IdRes int viewId) {
        return FragmentTagUtils.makeTag(ExpandedAlarmViewHolder.class, viewId, getItemId());
    }
}
