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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmManagerClient {

	public AlarmManagerClient(Context c) {
	}

	public static void cancelAlarm(Context ctx, int alarmCode) {
		AlarmManager am = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(ctx, AlarmManagerBroadcastReceiver.class);
		intent.putExtra("ALARM_CODE", alarmCode);
		PendingIntent sender = PendingIntent
				.getBroadcast(ctx, alarmCode, intent, 0);
		am.cancel(sender);
	}
	
	public static void setOnetimeTimer(Context ctx, long alarmTimeEpoc, int alarmCode) {
		AlarmManager am = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(ctx, AlarmManagerBroadcastReceiver.class);
		intent.putExtra("ALARM_CODE", alarmCode);
		PendingIntent pi = PendingIntent.getBroadcast(ctx, alarmCode,
				intent, 0);
		am.set(AlarmManager.RTC_WAKEUP, alarmTimeEpoc, pi);
	}
}
