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

package com.claudiofus.clock2.ringtone.playback;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.claudiofus.clock2.R;
import com.claudiofus.clock2.util.Constants;

import java.io.IOException;

/**
 * A MediaPlayer configured to play a ringtone in a loop.
 */
public final class RingtoneLoop {

    private final Context mContext;
    private final AudioManager mAudioManager;
    private final Uri mUri;

    private float mVolumeLevel;
    private int maxVolume;
    private MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();
    private boolean gradualAwakening = false;

    private Runnable mVolumeRunnable = new Runnable() {
        @Override
        public void run() {
            // increase volume level until reach max value
            if (mMediaPlayer != null && mVolumeLevel < maxVolume) {
                mVolumeLevel += Constants.VOLUME_INCREASE_STEP;
                mMediaPlayer.setVolume(mVolumeLevel, mVolumeLevel);
                // set next increase in 600ms
                mHandler.postDelayed(mVolumeRunnable, Constants.VOLUME_INCREASE_DELAY);
            }
        }
    };

    public RingtoneLoop(Context context, Uri uri) {
        mContext = context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mUri = uri;
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        gradualAwakening = PreferenceManager.getDefaultSharedPreferences(mContext).
                getBoolean(mContext.getString(R.string.pref_gradual_awakening_key), false);
    }

    public void play() {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mContext, mUri);
            if (mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                // "Must call this method before prepare() or prepareAsync() in order
                // for the target stream type to become effective thereafter."
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                // There is prepare() and prepareAsync().
                // "For files, it is OK to call prepare(), which blocks until
                // MediaPlayer is ready for playback."
                mMediaPlayer.prepare();
                if (gradualAwakening) {
                    mHandler.postDelayed(mVolumeRunnable, Constants.VOLUME_INCREASE_DELAY);
                } else {
                    mMediaPlayer.setVolume(maxVolume, maxVolume);
                }
                mMediaPlayer.start();
            }
        } catch (SecurityException | IOException e) {
            destroyLocalPlayer();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            destroyLocalPlayer();
        }
    }

    private void destroyLocalPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

}
