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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
/*		if (intent == null || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
			return;*/
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
		// Acquire the lock
		wl.acquire();

		// start activity
		Intent i = new Intent(context.getApplicationContext(),
				Alarm.class);
		i.addFlags(/* Intent.FLAG_ACTIVITY_CLEAR_TOP | */ Intent.FLAG_ACTIVITY_NEW_TASK);
		//i.putExtra("audioFile", intent.getExtras().getString("audioFile"));
		int id = intent.getIntExtra("ALARM_CODE", 93);
		Logger.log("onReceive - ALARM_CODE " + id);
		i.putExtra("ALARM_CODE", intent.getIntExtra("ALARM_CODE", 93));
		context.startActivity(i);
		// Release the lock
		wl.release();
	}
}