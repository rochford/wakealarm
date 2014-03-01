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

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;

public class OptionsActivity extends Activity {
	private SharedPreferences mPrefs = null;

//	private static final int TEST_ALARM = 1;
	private static final int SELECT_SOUND = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		mPrefs = getSharedPreferences("alarm", MODE_PRIVATE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String s = data.getExtras().getString("clicked_file");
			Log.d("rochford", "onActivityResult - clicked_file " + s);
			
			Editor ed = mPrefs.edit();
			ed.putString("alarm_sound_file", s);
			ed.commit();
		}
	}

	public void startAlarm(View v) {
		AlarmItem test = AlarmModel.newTestAlarm();
		AlarmModel mAlarmModel = AlarmModel.getInstance();
		try {
			mAlarmModel.saveState(getApplicationContext().openFileOutput(
					"alarmzzz", Context.MODE_PRIVATE));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Intent i = new Intent(this, Alarm.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("ALARM_CODE", test.getAlarmId());

		startActivity(i);
	}
	
	public void startSettings(View v) {
		Intent i = new Intent(this, SettingsActivity.class);
		startActivity(i);
	}

	public void startAbout(View v) {
		Intent i = new Intent(this, AboutActivity.class);
		startActivity(i);
	}

	public void startHelp(View v) {
		Intent i = new Intent(this, HelpActivity.class);
		startActivity(i);
	}

	public void selectSound(View view) {
		String musicDir = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/bluetooth/";
		Intent i = new Intent(this, ListFiles.class);
		i.putExtra("directory", musicDir);
		startActivityForResult(i, SELECT_SOUND);
	}

}
