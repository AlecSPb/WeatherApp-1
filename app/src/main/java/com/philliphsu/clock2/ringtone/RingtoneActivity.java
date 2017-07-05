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

package com.philliphsu.clock2.ringtone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philliphsu.clock2.BaseActivity;
import com.philliphsu.clock2.GPSTracker;
import com.philliphsu.clock2.MainActivity;
import com.philliphsu.clock2.R;
import com.philliphsu.clock2.alarms.Alarm;
import com.philliphsu.clock2.alarms.data.AlarmCursor;
import com.philliphsu.clock2.alarms.misc.AlarmController;
import com.philliphsu.clock2.ringtone.playback.RingtoneService;
import com.philliphsu.clock2.util.Constants;
import com.philliphsu.clock2.util.LocalBroadcastHelper;
import com.philliphsu.clock2.util.ParcelableUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.philliphsu.clock2.util.TimeFormatUtils.getHour;
import static com.philliphsu.clock2.util.TimeFormatUtils.getMinutes;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public abstract class RingtoneActivity<T extends Parcelable> extends BaseActivity {
    private static final String TAG = "RingtoneActivity";

    // Shared with RingtoneService
    public static final String ACTION_FINISH = "com.philliphsu.clock2.ringtone.action.FINISH";
    public static final String EXTRA_RINGING_OBJECT = "com.philliphsu.clock2.ringtone.extra.RINGING_OBJECT";
    public static final String ACTION_SHOW_SILENCED = "com.philliphsu.clock2.ringtone.action.SHOW_SILENCED";

    private static boolean sIsAlive = false;
    private T mRingingObject;

    @Bind(R.id.title) TextView mHeaderTitle;
    @Bind(R.id.auto_silenced_container) LinearLayout mAutoSilencedContainer;
    @Bind(R.id.auto_silenced_text) TextView mAutoSilencedText;
    @Bind(R.id.ok) Button mOkButton;
    @Bind(R.id.buttons_container) LinearLayout mButtonsContainer;
    @Bind(R.id.btn_text_left) TextView mLeftButton;
    @Bind(R.id.btn_text_right) TextView mRightButton;

    protected abstract Class<? extends RingtoneService> getRingtoneServiceClass();

    protected abstract CharSequence getHeaderTitle();

    /**
     * Subclasses are responsible for adding their content view
     * to the provided parent container.
     */
    protected abstract void getHeaderContent(ViewGroup parent);

    @DrawableRes
    protected int getAutoSilencedDrawable() {
        return R.drawable.ic_error_outline_96dp;
    }

    @StringRes
    protected abstract int getAutoSilencedText();

    @StringRes
    protected abstract int getLeftButtonText();

    @StringRes
    protected abstract int getRightButtonText();

    @DrawableRes
    protected abstract int getLeftButtonDrawable();

    @DrawableRes
    protected abstract int getRightButtonDrawable();

    @OnClick(R.id.btn_left)
    protected abstract void onLeftButtonClick();

    @OnClick(R.id.btn_right)
    protected abstract void onRightButtonClick();

    /**
     * @return An implementation of {@link android.os.Parcelable.Creator} that can create
     *         an instance of the {@link #mRingingObject ringing object}.
     */
    // TODO: Make abstract when we override this in all RingtoneActivities.
    protected Parcelable.Creator<T> getParcelableCreator() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        final byte[] bytes = getIntent().getByteArrayExtra(EXTRA_RINGING_OBJECT);
        if (bytes == null) {
            throw new IllegalStateException("Cannot start RingtoneActivity without a ringing object");
        }
        mRingingObject = ParcelableUtil.unmarshall(bytes, getParcelableCreator());
        sIsAlive = true;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        mHeaderTitle.setText(getHeaderTitle()); // TOneverDO: call before assigning mRingingObject
        getHeaderContent((LinearLayout) findViewById(R.id.header));
        mAutoSilencedText.setCompoundDrawablesWithIntrinsicBounds(0, getAutoSilencedDrawable(), 0, 0);
        mAutoSilencedText.setText(getAutoSilencedText());
        mLeftButton.setText(getLeftButtonText());
        mRightButton.setText(getRightButtonText());
        mLeftButton.setCompoundDrawablesWithIntrinsicBounds(0, getLeftButtonDrawable(), 0, 0);
        mRightButton.setCompoundDrawablesWithIntrinsicBounds(0, getRightButtonDrawable(), 0, 0);

        Intent intent = new Intent(this, getRingtoneServiceClass())
                .putExtra(EXTRA_RINGING_OBJECT, ParcelableUtil.marshall(mRingingObject));

        // Loop over condition to check if anyone has time setted
        final Alarm oldAlarm = ParcelableUtil.unmarshall(bytes, Alarm.CREATOR);
        AlarmController alarmCtrl = new AlarmController(getApplicationContext(), null);
        AlarmCursor cursor = alarmCtrl.getItem(getApplicationContext(), oldAlarm);
        final LinkedHashMap<String,String> condTime = cursor.getItem().getWeatherConditions();
        boolean flag = false;
        for (String time : condTime.values()){
            // If one has time setted, get weather information
            if (time != null){
                new GPSTracker(getApplicationContext());
                flag = true;
                break;
            }
        }

        // If at least one condition has time, continue check
        // Else ring now
        if (flag) {
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String condition = intent.getStringExtra(GPSTracker.EXTRA_CONDITION);
                            Intent intentRing = new Intent(context, getRingtoneServiceClass())
                                    .putExtra(EXTRA_RINGING_OBJECT, ParcelableUtil.marshall(mRingingObject));
                            // Check if current condition of weather is the same setted
                            if (condTime.containsKey(condition)) {
                                String time = condTime.get(condition);
                                String currentTime = new SimpleDateFormat(Constants.TIME_FORMAT, Locale.getDefault()).format(new Date());

                                // Check if time is now, if so ring now
                                // Else schedule with time found
                                if (currentTime.equals(time)) {
                                    context.startService(intentRing);
                                } else {
                                    Alarm newAlarm = Alarm.builder()
                                            .hour(getHour(time))
                                            .minutes(getMinutes(time))
                                            .label(oldAlarm.label())
                                            .ringtone(oldAlarm.ringtone())
                                            .vibrates(oldAlarm.vibrates())
                                            .build();
                                    oldAlarm.copyMutableFieldsTo(newAlarm);
                                    AlarmController alarmController = new AlarmController(context, null);
                                    alarmController.scheduleAlarm(newAlarm, false);
                                    Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
                                    intentMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intentMain);
                                }
                            } else {
                                context.startService(intentRing);
                            }
                        }
                    }, new IntentFilter(GPSTracker.ACTION_LOCATION_BROADCAST)
            );
        } else {
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: Do we need this anymore? I think this broadcast was only sent from
        // EditAlarmActivity?
        LocalBroadcastHelper.registerReceiver(this, mFinishReceiver, ACTION_FINISH);
        LocalBroadcastHelper.registerReceiver(this, mOnAutoSilenceReceiver, ACTION_SHOW_SILENCED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: Do we need this anymore? I think this broadcast was only sent from
        // EditAlarmActivity?
        LocalBroadcastHelper.unregisterReceiver(this, mFinishReceiver);
        LocalBroadcastHelper.unregisterReceiver(this, mOnAutoSilenceReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //super.onNewIntent(intent); // Not needed since no fragments hosted?
        // Notifies alarm missed and stops the service
        LocalBroadcastHelper.sendBroadcast(this, RingtoneService.ACTION_NOTIFY_MISSED);
        finish();
        startActivity(intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Set the content to appear under the system bars so that the
            // content doesn't resize when the system bars hide and show.
            // The system bars will remain hidden on user interaction;
            // however, they can be revealed using swipe gestures along
            // the region where they normally appear.
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;

            // Make status bar translucent, which automatically adds
            // SYSTEM_UI_FLAG_LAYOUT_STABLE and SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            // Looks too light on the current background..
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public void onBackPressed() {
        // Capture the back press and return. We want to limit the user's options for leaving
        // this activity as much as possible.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sIsAlive = false;
    }

    @Override
    @OnClick(R.id.ok)
    public void finish() {
        super.finish();
    }

    @Override
    protected final int layoutResId() {
        return R.layout.activity_ringtone;
    }

    @Override
    protected final int menuResId() {
        return 0;
    }

    @Override
    protected final boolean isDisplayHomeUpEnabled() {
        return false;
    }

    public static boolean isAlive() {
        return sIsAlive;
    }

    /**
     * Exposed to subclasses so they can force us to stop the
     * ringtone and finish us.
     */
    protected final void stopAndFinish() {
        stopService(new Intent(this, getRingtoneServiceClass()));
        finish();
    }

    protected final T getRingingObject() {
        return mRingingObject;
    }

    protected void showAutoSilenced() {
        mAutoSilencedContainer.setVisibility(View.VISIBLE);
        mButtonsContainer.setVisibility(View.GONE);
    }

    // TODO: Do we need this anymore? I think this broadcast was only sent from
    // EditAlarmActivity?
    private final BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopAndFinish();
        }
    };

    private final BroadcastReceiver mOnAutoSilenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAutoSilenced();
        }
    };
}