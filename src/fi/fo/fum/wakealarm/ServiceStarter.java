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
import java.util.TimeZone;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceStarter extends BroadcastReceiver {
	private AlarmModel mAlarmModel = null;

	private void loadAlarmModel(Context context) {
		Logger.log("loadAlarmModel start");
		mAlarmModel = AlarmModel.getInstance();
		if (!mAlarmModel.hasReadAlarmData()) {
			try {

				mAlarmModel.loadState(context.openFileInput("alarmzzz"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Logger.log("loadAlarmModel end");
		}
	}

	@Override
	public void onReceive(Context context, Intent in) {
		Logger.log("onReceive start");

		loadAlarmModel(context);

		TimeZone currentTZ = TimeZone.getDefault();
		Calendar now = Calendar.getInstance();
		for (int i = 0; i < mAlarmModel.size(); i++) {

			AlarmItem a = mAlarmModel.get(i);
			a.setTimeZone(currentTZ);
			Logger.log("onReceive " + a.toString());

			// if the alarm is in the past
			if (a.getAlarmId() == AlarmModel.getPowerNapAlarmId() &&
					a.getCalendar().before(now)) {
				// how to delete it ? mAlarmModel.remove(a);
				continue;
			}

			a.setNextAlarm(now);
			Logger.log("onReceive XXX" + a.toString());
			long alarmTimeValue = a.getNextAlarmMs();

			AlarmManagerClient.cancelAlarm(context, a.getAlarmId());
			AlarmManagerClient.setOnetimeTimer(context, alarmTimeValue,
					a.getAlarmId());
		}
		Intent i = new Intent(
				"com.kona.graduallightalarm.AlarmManagerBroadcastReceiver");
		// i.setClass(context, AlarmManagerBroadcastReceiver.class);
		context.startService(i);
		Logger.log("onReceive end");
	}
}
