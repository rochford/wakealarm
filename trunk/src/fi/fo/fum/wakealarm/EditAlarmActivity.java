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
import java.util.Calendar;
import java.util.EnumSet;
import fi.fo.fum.wakealarm.AlarmItem.Recurrence;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

public class EditAlarmActivity extends Activity {

	private AlarmModel mAlarmModel = null;
	
	private SharedPreferences mPrefs = null;
	private final String PREFS_ALARM_DEFAULT_HOURS = "edit_alarm_hours";
	private final String PREFS_ALARM_DEFAULT_MINS = "edit_alarm_mins";
	private AlarmItem mEditAlarm = null;
	private EditText mEditText;
	private DigitView mDigitView = null;
	private ToggleButton mMonday, mTuesday, mWednesday, mThursday, mFriday,
			mSaturday, mSunday;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_alarm);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		mAlarmModel = AlarmModel.getInstance();

		mEditText = (EditText) this.findViewById(R.id.alarmDescription);
		mDigitView = (DigitView) this.findViewById(R.id.digitview);
		mMonday = (ToggleButton) this.findViewById(R.id.radioMon);
		mTuesday = (ToggleButton) this.findViewById(R.id.radioTue);
		mWednesday = (ToggleButton) this.findViewById(R.id.radioWed);
		mThursday = (ToggleButton) this.findViewById(R.id.radioThu);
		mFriday = (ToggleButton) this.findViewById(R.id.radioFri);
		mSaturday = (ToggleButton) this.findViewById(R.id.radioSat);
		mSunday = (ToggleButton) this.findViewById(R.id.radioSun);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			int index = b.getInt("com.kona.graduallightalarm.alarmindex");
			mEditAlarm = mAlarmModel.get(index);
			Logger.log("Received - " + mEditAlarm.toString());
			mEditText.setText(mEditAlarm.getDescription());
			mDigitView.setTime(mEditAlarm.getHours(), mEditAlarm.getMinutes());

			mSunday.setChecked(mEditAlarm.getRecurrence().contains(
					Recurrence.SUNDAY));
			mMonday.setChecked(mEditAlarm.getRecurrence().contains(
					Recurrence.MONDAY));
			mTuesday.setChecked(mEditAlarm.getRecurrence().contains(
					Recurrence.TUESDAY));
			mWednesday.setChecked(mEditAlarm.getRecurrence().contains(
					Recurrence.WEDNESDAY));
			mThursday.setChecked(mEditAlarm.getRecurrence().contains(
					Recurrence.THURSDAY));
			mFriday.setChecked(mEditAlarm.getRecurrence().contains(
					Recurrence.FRIDAY));
			mSaturday.setChecked(mEditAlarm.getRecurrence().contains(
					Recurrence.SATURDAY));
		} else {
			int hrs = mPrefs.getInt(PREFS_ALARM_DEFAULT_HOURS, 7);
			int mins = mPrefs.getInt(PREFS_ALARM_DEFAULT_MINS, 0);
			mDigitView.setTime(hrs, mins);
		}	
	}

	public void setAlarm(View v) {
		if (mEditAlarm == null)
			newAlarmItem();
		else
			updateAlarm();
		finish();
	}

	private void updateAlarm() {
		try {
			EnumSet<Recurrence> flags = EnumSet.noneOf(Recurrence.class);

			if (mMonday.isChecked())
				flags.add(AlarmItem.Recurrence.MONDAY);
			if (mTuesday.isChecked())
				flags.add(AlarmItem.Recurrence.TUESDAY);
			if (mWednesday.isChecked())
				flags.add(AlarmItem.Recurrence.WEDNESDAY);
			if (mThursday.isChecked())
				flags.add(AlarmItem.Recurrence.THURSDAY);
			if (mFriday.isChecked())
				flags.add(AlarmItem.Recurrence.FRIDAY);
			if (mSaturday.isChecked())
				flags.add(AlarmItem.Recurrence.SATURDAY);
			if (mSunday.isChecked())
				flags.add(AlarmItem.Recurrence.SUNDAY);

			mEditAlarm.setHours(mDigitView.getHours())
					.setMinutes(mDigitView.getMinutes())
					.setDescription(mEditText.getText().toString())
					.setRecurrence(flags);
			Logger.log("update alarm item: " + mEditAlarm.toString());
			mAlarmModel.remove(mEditAlarm);
			final int index = mAlarmModel.add(mEditAlarm);
			Logger.log("index: " + index);
			onetimeTimer(index);
			mAlarmModel.sort();

			mAlarmModel.saveState(getApplicationContext().openFileOutput(
					"alarmzzz", Context.MODE_PRIVATE));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void deleteAlarm(View v) {
		if (mEditAlarm != null) {
			AlarmManagerClient.cancelAlarm(getApplicationContext(),
					mEditAlarm.getAlarmId());
			mAlarmModel.remove(mEditAlarm);
			mAlarmModel.sort();
			try {
				mAlarmModel.saveState(getApplicationContext().openFileOutput(
						"alarmzzz", Context.MODE_PRIVATE));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mEditAlarm = null;
		}
		finish();
	}

	private void newAlarmItem() {
		try {
			EnumSet<Recurrence> flags = EnumSet.noneOf(Recurrence.class);

			if (mMonday.isChecked())
				flags.add(AlarmItem.Recurrence.MONDAY);
			if (mTuesday.isChecked())
				flags.add(AlarmItem.Recurrence.TUESDAY);
			if (mWednesday.isChecked())
				flags.add(AlarmItem.Recurrence.WEDNESDAY);
			if (mThursday.isChecked())
				flags.add(AlarmItem.Recurrence.THURSDAY);
			if (mFriday.isChecked())
				flags.add(AlarmItem.Recurrence.FRIDAY);
			if (mSaturday.isChecked())
				flags.add(AlarmItem.Recurrence.SATURDAY);
			if (mSunday.isChecked())
				flags.add(AlarmItem.Recurrence.SUNDAY);

			AlarmItem a = AlarmModel.newAlarmItem(mDigitView.getHours(),
					mDigitView.getMinutes(), flags).setDescription(
					mEditText.getText().toString());
			Logger.log("newAlarmItem: " + a.toString());
			final int index = mAlarmModel.add(a);
			Logger.log("index: " + index);
			onetimeTimer(index);
			mAlarmModel.sort();
			mAlarmModel.saveState(getApplicationContext().openFileOutput(
					"alarmzzz", Context.MODE_PRIVATE));
			
			mPrefs.edit().putInt(PREFS_ALARM_DEFAULT_HOURS, mDigitView.getHours()).commit();
			mPrefs.edit().putInt(PREFS_ALARM_DEFAULT_MINS, mDigitView.getMinutes()).commit();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onetimeTimer(int index) {
		final AlarmItem alarm = mAlarmModel.get(index);
		alarm.setNextAlarm(Calendar.getInstance());

		long alarmTimeValue = alarm.getNextAlarmMs();

		AlarmManagerClient.cancelAlarm(getApplicationContext(),
				alarm.getAlarmId());
		AlarmManagerClient.setOnetimeTimer(getApplicationContext(),
				alarmTimeValue, alarm.getAlarmId());
	}
}
