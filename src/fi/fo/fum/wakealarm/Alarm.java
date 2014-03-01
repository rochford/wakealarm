/* 
 * Wake Alarm, Copyright 2014, Timothy Rochford
 */
/*    This file is part of Wake Alarm.

    Wake Alarm is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Wake Alarm is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Wake Alarm.  If not, see <http://www.gnu.org/licenses/>.
*/
package fi.fo.fum.wakealarm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.media.AudioManager;
import android.media.MediaPlayer;
import fi.fo.fum.wakealarm.Logger;

public class Alarm extends Activity {
	private SharedPreferences mPrefs = null;
	
	private AlarmModel mAlarmModel = null;
	private int mAlarmId = -1;

	AnimationDrawable mAnimationDrawable;

	private boolean mStarted = false;
	private WakeLock wakeLock = null;
	private MediaPlayer mPlayer;
	private int modes = PowerManager.ON_AFTER_RELEASE
			| PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;

	private TextView mTimeNow;
	private TextView mAlarmDescription;
	private Handler mHandler = null;

	final Runnable r = new Runnable() {
		public void run() {
			Calendar nowCal = Calendar.getInstance(); 
			mTimeNow.setText(Utils.longFormatTime(nowCal));
			mTimeNow.invalidate();
			mHandler.postDelayed(this, 1000);
		}
	};

	private void loadAlarmModel() {
		mAlarmModel = AlarmModel.getInstance();
		if (!mAlarmModel.hasReadAlarmData()) {
			try {
				mAlarmModel.loadState(getApplicationContext().openFileInput(
						"alarmzzz"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);

		mPrefs = getSharedPreferences("alarm", MODE_PRIVATE);

		loadAlarmModel();

		int id = this.getIntent().getIntExtra("ALARM_CODE", 99);

		Logger.log("Alarm::onCreate() start ALARM_CODE " + id);
		AlarmItem a = mAlarmModel.getByAlarmId(id);
		mAlarmDescription = (TextView) this
				.findViewById(R.id.alarmDescriptionText);
		mAlarmDescription.setText(a.getDescription());
		Logger.log("Alarm::onCreate() id=" + id + ", a.toString() = "
				+ (a != null ? a.toString() : "null"));
		mAlarmId = a.getAlarmId();

		mTimeNow = (TextView) this.findViewById(R.id.textTimeNow);
		mHandler = new Handler();

		View parent = findViewById(R.id.alarmLayout);
		AnimationDrawable aniDrawable = 
				(AnimationDrawable) getResources().getDrawable(
						R.drawable.animation_drawable);
		mAnimationDrawable = new AnimationDrawable();
		for (int i = 0; i < aniDrawable.getNumberOfFrames(); i++) {
			mAnimationDrawable.addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i));
		}
		mAnimationDrawable.setOneShot(false);
		parent.setBackgroundDrawable(mAnimationDrawable);

		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		mPlayer.setLooping(true);
		KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext()
				.getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
		keyguardLock.disableKeyguard();

		PowerManager pm = (PowerManager) getApplicationContext()
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(modes, "TAG");
		wakeLock.acquire();
		startMP();

		mHandler.postDelayed(r, 1);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.stopMP();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		mPlayer.stop();

		removeTriggeredAlarm();
		finish();
	}

	@Override
	public void onWindowFocusChanged(boolean gained) {
		super.onWindowFocusChanged(gained);
		if (gained) {
			mAnimationDrawable.start();
		}
	}

	public void stopAlarm(View view) {
		if (wakeLock != null)
			this.wakeLock.release();
		mPlayer.stop();
		removeTriggeredAlarm();

		setAlarms();
		finish();
	}

	private void removeTriggeredAlarm() {
		AlarmItem a = mAlarmModel.getByAlarmId(mAlarmId);
		Log.d("rochford", "removeTriggeredAlarm " + a.toString());
		if (mAlarmId == AlarmModel.getTestAlarmId()
				|| mAlarmId == AlarmModel.getPowerNapAlarmId()) {
			AlarmModel.removeAlarmId(mAlarmId);
		} else if (a.getRecurrence().isEmpty()) {
			// if not recurring then it can be deleted
			AlarmModel.removeAlarmId(mAlarmId);
		} else { 
			Calendar now = Calendar.getInstance();
			a.setNextAlarm(now);
			long alarmTimeValue = a.getNextAlarmMs();

			AlarmManagerClient.cancelAlarm(getApplicationContext(), a.getAlarmId());
			AlarmManagerClient.setOnetimeTimer(getApplicationContext(), alarmTimeValue,
					a.getAlarmId());
		}
		mAlarmModel.sort();
		try {
			mAlarmModel.saveState(getApplicationContext().openFileOutput(
					"alarmzzz", Context.MODE_PRIVATE));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startMP() {
		if (mStarted)
			return;

		try {
	        
			final String sound = mPrefs.getString("alarm_sound_file", "alarmtones/rooster.mp3");
			Log.d("rochford", "Alarm - alarm_sound_file: " + sound);
			mPlayer.setWakeMode(getApplicationContext(),
					PowerManager.PARTIAL_WAKE_LOCK);
			AssetFileDescriptor descriptor = getAssets().openFd(sound);
			mPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
	        descriptor.close();
			mPlayer.prepare();
			mPlayer.start();
			mStarted = true;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void stopMP() {
		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
	}

	private void setAlarms() {

		Calendar now = Calendar.getInstance();
		for (int i = 0; i < mAlarmModel.size(); i++) {
			AlarmItem a = mAlarmModel.get(i);
			a.setNextAlarm(now);
			long alarmTimeValue = a.getNextAlarmMs();

			AlarmManagerClient.cancelAlarm(getApplicationContext(),
					a.getAlarmId());
			AlarmManagerClient.setOnetimeTimer(getApplicationContext(),
					alarmTimeValue, a.getAlarmId());
		}
	}
}
