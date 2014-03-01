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

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ListView;

public class AlarmMain extends Activity {

	private SharedPreferences mPrefs = null;
	private AlarmModel mAlarmModel = null;
	private AlarmAdapter mAlarmAdapter = null;
	private ListView mListView = null;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_screen);
		Logger.log("onCreate");
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		mListView = (ListView) this.findViewById(R.id.alarmListView);
		mListView.setEmptyView(this.findViewById(R.id.emptyView));
		loadAlarmModel();

		mAlarmAdapter = new AlarmAdapter(this, getResources());
		mListView.setAdapter(mAlarmAdapter);
	}

	@Override
	public void onPause() {
		super.onPause();
		Logger.log("onPause");
		try {
			mAlarmModel.saveState(getApplicationContext().openFileOutput(
					"alarmzzz", Context.MODE_PRIVATE));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!mAlarmModel.hasReadAlarmData()) {
			try {
				mAlarmModel.loadState(getApplicationContext().openFileInput(
						"alarmzzz"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mAlarmAdapter.notifyDataSetChanged();
		Logger.log("onResume");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Logger.log("onDestroy");
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	public void startEditAlarmActivity(View v) {
		if (this.mAlarmModel.alarmCount() + 1 > AlarmModel.getMaxAlarms()) {
			AlertDialog d = new AlertDialog.Builder(this).create();
			d.setMessage(getResources().getString(R.string.max_alarms));
			d.setButton(DialogInterface.BUTTON1, getResources().getString(R.string.ok),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			d.show();

		} else {
			Intent i = new Intent(getApplicationContext(), EditAlarmActivity.class);
			startActivity(i);
		}
	}

	private void newPowerNap(int napTime) {

		Calendar nap = Calendar.getInstance();
		nap.add(Calendar.MINUTE, napTime);
		Logger.log(nap.toString());

		AlarmItem a = AlarmModel.newPowerNap(Calendar.getInstance(), nap);
		a.setNextAlarm(Calendar.getInstance());

		long alarmTimeValue = a.getNextAlarmMs();
		AlarmManagerClient.cancelAlarm(getApplicationContext(),
				AlarmModel.getPowerNapAlarmId());
		AlarmManagerClient.setOnetimeTimer(getApplicationContext(),
				alarmTimeValue, AlarmModel.getPowerNapAlarmId());
		mAlarmModel.sort();
		try {
			mAlarmModel.saveState(getApplicationContext().openFileOutput(
					"alarmzzz", Context.MODE_PRIVATE));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mAlarmAdapter.notifyDataSetChanged();
	}

	public void newPowerNapDialog() {
		int napShort = mPrefs.getInt("nap_short", 20);
		int napLong = mPrefs.getInt("nap_long", 40);

		if (napShort > napLong) {
			int tmp = napShort;
			napShort = napLong;
			napLong = tmp;
			mPrefs.edit().putString("nap_short", String.valueOf(napShort))
					.commit();
			mPrefs.edit().putString("nap_long", String.valueOf(napLong))
					.commit();
		}

		final int finalShort = napShort;
		final int finalLong = napLong;

		AlertDialog d = new AlertDialog.Builder(this).create();
		d.setMessage(this.getResources().getString(R.string.powernap_length));
		d.setButton(DialogInterface.BUTTON1, String.valueOf(finalShort),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						newPowerNap(finalShort);
					}
				});
		d.setButton(DialogInterface.BUTTON2, String.valueOf(finalLong),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						newPowerNap(finalLong);
					}
				});
		d.show();
	}

	public void startCountDownTimerDialog(View v) {

		// if power nap already exists then ask if user wants to replace it
		AlarmItem old = mAlarmModel.getByAlarmId(AlarmModel
				.getPowerNapAlarmId());

		if (old != null) {
			final int index = mAlarmModel.indexOf(old);
			replacePowerNapDialog(index);
		} else {
			newPowerNapDialog();
		}
	}

	public void startSettings(View v) {
		Intent i = new Intent(getApplicationContext(), OptionsActivity.class);
		startActivity(i);
	}

	public void replacePowerNapDialog(final int position) {
		AlertDialog d = new AlertDialog.Builder(this).create();
		String cancel = this.getResources().getString(R.string.cancel);
		String dontcancel = this.getResources().getString(R.string.dontcancel);
		String cancelPowerNap = this.getResources().getString(
				R.string.replacepowernap);
		d.setMessage(cancelPowerNap);
		d.setButton(DialogInterface.BUTTON_POSITIVE, cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mAlarmAdapter.cancelRepeatingTimer(AlarmModel
								.getPowerNapAlarmId());
						mAlarmModel.remove(position);
						mAlarmAdapter.notifyDataSetChanged();
						dialog.dismiss();
						newPowerNapDialog();
					}
				});
		d.setButton(DialogInterface.BUTTON_NEGATIVE, dontcancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		d.show();
	}

	public void cancelPowerNapDialog(final int position) {
		AlertDialog d = new AlertDialog.Builder(this).create();
		String cancel = this.getResources().getString(R.string.cancel);
		String dontcancel = this.getResources().getString(R.string.dontcancel);
		String cancelPowerNap = this.getResources().getString(
				R.string.cancelpowernap);
		d.setMessage(cancelPowerNap);
		d.setButton(DialogInterface.BUTTON_POSITIVE, cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mAlarmAdapter.cancelRepeatingTimer(AlarmModel
								.getPowerNapAlarmId());
						mAlarmModel.remove(position);
						mAlarmAdapter.notifyDataSetChanged();
						dialog.dismiss();
					}
				});
		d.setButton(DialogInterface.BUTTON_NEGATIVE, dontcancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		d.show();
	}

}