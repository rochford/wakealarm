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

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class IntegerPreference extends EditTextPreference {

	public IntegerPreference(Context context) {
		super(context);
	}

	public IntegerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IntegerPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected String getPersistedString(String defaultReturnValue) {
		return String.valueOf(getPersistedInt(30));
	}

	@Override
	protected boolean persistString(String value) {
		try {
			return persistInt(Integer.valueOf(value));
		} catch(NumberFormatException e) {
			return persistInt(30);
		}
	}
}
