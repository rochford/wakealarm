/* 
 * Wake Alarm, Copyright 2014, Timothy Rochford
 */
/*    This file is part of Wake Alarm.

    Wake Alarm is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Wake Alarm.  If not, see <http://www.gnu.org/licenses/>.
*/
package fi.fo.fum.wakealarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class Utils {

	private Utils() {
	}

	public static void launchMarket(Context c) {
		Uri uri = Uri.parse("market://details?id=" + c.getPackageName());
		// debug only Uri uri =
		//Uri.parse("https://market.android.com/details?id=" + getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			c.startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(c, "XXX" /* R.string.couldnt_launch_market */,
					Toast.LENGTH_LONG).show();
		}
	}

	public static String longFormatTime(Calendar time) {
		SimpleDateFormat f = new SimpleDateFormat("EEE, MMM d, h:mm a");
		f.setCalendar(time);

		return f.format(time.getTime());
	}

	public static String shortFormatTime(Calendar time) {
		SimpleDateFormat f = new SimpleDateFormat("EEE, h:mm a");
		f.setCalendar(time);

		return f.format(time.getTime());
	}

}