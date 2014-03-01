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

import java.io.Serializable;
import java.util.Calendar;
import java.util.Set;
import java.util.TimeZone;

public class AlarmItem implements Serializable, Comparable<AlarmItem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3245549327723176964L;

	public enum Recurrence {
		SUNDAY(1), MONDAY(2), TUESDAY(3), WEDNESDAY(4), THURSDAY(5), FRIDAY(6), SATURDAY(
				7);
		private final int mDow;

		Recurrence(int dow) {
			mDow = dow;
		}

		public int getDow() {
			return mDow;
		}
	}

	// used in the PendingIntents
	private final int mAlarmId;
	private Calendar mNextAlarm;
	private boolean mActive;
	private Set<Recurrence> mIntervalFlags;
	private String mDescription = "";

	public AlarmItem(Calendar now, int hours, int minutes,
			Set<Recurrence> recurrenceFlags, int alarmIntentId) {
		if (hours > 23)
			throw new IndexOutOfBoundsException("Hours too big");
		if (minutes > 59)
			throw new IndexOutOfBoundsException("Minutes too big");
		mNextAlarm = (Calendar) now.clone();

		mNextAlarm.clear(Calendar.HOUR_OF_DAY);
		mNextAlarm.clear(Calendar.MINUTE);

		mNextAlarm.set(Calendar.HOUR_OF_DAY, hours);
		mNextAlarm.set(Calendar.MINUTE, minutes);
		mAlarmId = alarmIntentId;
		mActive = true;
		mIntervalFlags = recurrenceFlags;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + mNextAlarm.hashCode();
		if (mActive)
			result = 31 * result + 1;
		result = 31 * result + mAlarmId;
		result = 31 * result + mIntervalFlags.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof AlarmItem))
			return false;
		AlarmItem i = (AlarmItem) o;
		return i.getCalendar() == mNextAlarm
				// TODO: are these needed for logical equals?
				&& i.mActive == mActive && i.mAlarmId == mAlarmId
				&& i.mIntervalFlags == mIntervalFlags;
	}

	@Override
	public String toString() {
		return "AlarmId= " + mAlarmId + ",Active=" + mActive + ","
				+ mNextAlarm.toString() + ", flags=" + mIntervalFlags
				+ ", description=" + mDescription;
	}

	public String getDescription() {
		return this.mDescription;
	}

	public AlarmItem setDescription(String desc) {
		mDescription = desc;
		return this;
	}

	public Set<Recurrence> getRecurrence() {
		return this.mIntervalFlags;
	}

	public AlarmItem setRecurrence(Set<Recurrence> intervalFlags) {
		mIntervalFlags = intervalFlags;
		return this;
	}

	/*
	 * public boolean getActive() { return this.mActive; }
	 * 
	 * public AlarmItem setActive(boolean act) { mActive = act; return this; }
	 */
	public int getAlarmId() {
		return this.mAlarmId;
	}

	public Calendar getCalendar() {
		Calendar ret = (Calendar)mNextAlarm.clone();
		return ret;
	}

	public TimeZone getTimeZone() {
		return this.mNextAlarm.getTimeZone();
	}

	public AlarmItem setTimeZone(TimeZone timezone) {
		int hrs = mNextAlarm.get(Calendar.HOUR_OF_DAY);
		this.mNextAlarm.setTimeZone(timezone);
		mNextAlarm.set(Calendar.HOUR_OF_DAY, hrs);
		return this;
	}

	public int getHours() {
		return this.mNextAlarm.get(Calendar.HOUR_OF_DAY);
	}

	public int getMinutes() {
		return this.mNextAlarm.get(Calendar.MINUTE);
	}

	private void setSingleAlarm(Calendar currentTime) {
		if (mNextAlarm.compareTo(currentTime) == -1 ) {
			Logger.log("rolling DATE");
			mNextAlarm.roll(Calendar.DAY_OF_MONTH, 1);
		}
	}

	public void setRecurringAlarm(Calendar currentTime) {
		Logger.log("setRecurring");

		Calendar nearestAlarm = (Calendar)mNextAlarm.clone();
		nearestAlarm.roll(Calendar.YEAR, 10);
		for (Recurrence i : mIntervalFlags) {
			Logger.log(i.toString());
			// advance it
			Calendar temp = (Calendar)mNextAlarm.clone();
			temp.clear(Calendar.DAY_OF_WEEK);
			temp.set(Calendar.DAY_OF_WEEK, i.getDow());
			// if in the past, roll it by 1 week
			if (temp.compareTo(currentTime) == -1 ) {
				temp.roll(Calendar.WEEK_OF_YEAR, 1);
			}
			if (temp.before(nearestAlarm)) {
				nearestAlarm = temp; // (Calendar)temp.clone();
			}
		}
		Logger.log("Result is " + nearestAlarm.toString());
		mNextAlarm = nearestAlarm;
	}

	public void setNextAlarm(Calendar currentTime) {

		// mNextAlarm.clear(Calendar.AM_PM);
		mNextAlarm.clear(Calendar.SECOND);
		mNextAlarm.set(Calendar.SECOND, 0);

		if (mIntervalFlags.isEmpty()) {
			setSingleAlarm(currentTime);
		} else {
			setRecurringAlarm(currentTime);
		}
		Logger.log("Alarm Id " + this.mAlarmId + " set to "
						+ mNextAlarm.toString());
	}

	public long getNextAlarmMs() {
		return this.mNextAlarm.getTimeInMillis();
	}

	public AlarmItem setHours(int hours) {
		if (hours > 23)
			throw new IndexOutOfBoundsException("Hours too big");
		mNextAlarm.set(Calendar.HOUR_OF_DAY, hours);
		return this;
	}

	public AlarmItem setMinutes(int minutes) {
		if (minutes > 59)
			throw new IndexOutOfBoundsException("Minutes too big");
		mNextAlarm.set(Calendar.MINUTE, minutes);
		return this;
	}

	@Override
	public int compareTo(AlarmItem another) {
		// inactive alarms are lower in list
		/*
		 * if (this.mActive && another.getActive() == false) return -1; else
		 */if (!this.mActive && another.mActive)
			return 1;
		return mNextAlarm.compareTo(another.getCalendar());
	}

}
