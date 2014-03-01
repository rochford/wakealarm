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
package fi.fo.fum.wakealarm.test;

import java.util.Calendar;
import java.util.EnumSet;
import java.util.Set;

import junit.framework.TestCase;

import fi.fo.fum.wakealarm.AlarmItem;
import fi.fo.fum.wakealarm.AlarmItem.Recurrence;

public class AlarmItemTestRecurring extends TestCase {

	private static int alarmIntentId = 2;
	private static Calendar MON_13_44_6TH_JAN_2014 = Calendar.getInstance();
	private static Calendar MON_13_45_6TH_JAN_2014 = Calendar.getInstance();
	private static Calendar SAT_18_00_11TH_JAN_2014 = Calendar.getInstance();

	private static Set<Recurrence> WEEKENDS = EnumSet.of(Recurrence.SATURDAY,
			Recurrence.SUNDAY);
/*	private static Set<Recurrence> WORKDAYS = EnumSet.of(Recurrence.MONDAY,
			Recurrence.TUESDAY, Recurrence.WEDNESDAY, Recurrence.THURSDAY,
			Recurrence.FRIDAY);
*/
	public AlarmItemTestRecurring(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		MON_13_44_6TH_JAN_2014.clear(Calendar.HOUR_OF_DAY);
		MON_13_44_6TH_JAN_2014.clear(Calendar.MINUTE);
		MON_13_44_6TH_JAN_2014.clear(Calendar.DAY_OF_WEEK);
		MON_13_44_6TH_JAN_2014.clear(Calendar.DAY_OF_MONTH);
		MON_13_44_6TH_JAN_2014.clear(Calendar.MONTH);
		MON_13_44_6TH_JAN_2014.clear(Calendar.YEAR);

		MON_13_44_6TH_JAN_2014.set(Calendar.HOUR_OF_DAY, 13);
		MON_13_44_6TH_JAN_2014.set(Calendar.MINUTE, 44);
		MON_13_44_6TH_JAN_2014.set(Calendar.DAY_OF_WEEK, 2); // Monday
		MON_13_44_6TH_JAN_2014.set(Calendar.MONTH, Calendar.JANUARY); // Jan
		MON_13_44_6TH_JAN_2014.set(Calendar.DAY_OF_MONTH, 6); // 6th
		MON_13_44_6TH_JAN_2014.set(Calendar.YEAR, 2014); // 2014

		MON_13_45_6TH_JAN_2014.clear(Calendar.HOUR_OF_DAY);
		MON_13_45_6TH_JAN_2014.clear(Calendar.MINUTE);
		MON_13_45_6TH_JAN_2014.clear(Calendar.DAY_OF_WEEK);
		MON_13_45_6TH_JAN_2014.clear(Calendar.DAY_OF_MONTH);
		MON_13_45_6TH_JAN_2014.clear(Calendar.MONTH);
		MON_13_45_6TH_JAN_2014.clear(Calendar.YEAR);

		MON_13_45_6TH_JAN_2014.set(Calendar.HOUR_OF_DAY, 13);
		MON_13_45_6TH_JAN_2014.set(Calendar.MINUTE, 45);
		MON_13_45_6TH_JAN_2014.set(Calendar.DAY_OF_WEEK, 2); // Monday
		MON_13_45_6TH_JAN_2014.set(Calendar.MONTH, Calendar.JANUARY); // Jan
		MON_13_45_6TH_JAN_2014.set(Calendar.DAY_OF_MONTH, 6); // 6th
		MON_13_45_6TH_JAN_2014.set(Calendar.YEAR, 2014); // 2014

		SAT_18_00_11TH_JAN_2014.clear(Calendar.HOUR_OF_DAY);
		SAT_18_00_11TH_JAN_2014.clear(Calendar.MINUTE);
		SAT_18_00_11TH_JAN_2014.clear(Calendar.DAY_OF_WEEK);
		SAT_18_00_11TH_JAN_2014.clear(Calendar.DAY_OF_MONTH);
		SAT_18_00_11TH_JAN_2014.clear(Calendar.MONTH);
		SAT_18_00_11TH_JAN_2014.clear(Calendar.YEAR);

		SAT_18_00_11TH_JAN_2014.set(Calendar.HOUR_OF_DAY, 18);
		SAT_18_00_11TH_JAN_2014.set(Calendar.MINUTE, 00);
		SAT_18_00_11TH_JAN_2014.set(Calendar.DAY_OF_WEEK, 6); // Monday
		SAT_18_00_11TH_JAN_2014.set(Calendar.MONTH, Calendar.JANUARY); // Jan
		SAT_18_00_11TH_JAN_2014.set(Calendar.DAY_OF_MONTH, 11); // 11th
		SAT_18_00_11TH_JAN_2014.set(Calendar.YEAR, 2014); // 2014
}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRecurringAlarmToday() {
		// 1 hour in past
		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014,
				MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY),
				MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE),
				EnumSet.of(Recurrence.MONDAY), alarmIntentId++);
		a.setNextAlarm(MON_13_44_6TH_JAN_2014);
		Calendar actual = a.getCalendar();
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY),
				actual.get(Calendar.HOUR_OF_DAY));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE),
				actual.get(Calendar.MINUTE));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_MONTH) + 7,
				actual.get(Calendar.DAY_OF_MONTH));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_WEEK),
				actual.get(Calendar.DAY_OF_WEEK));

		a.setNextAlarm(MON_13_45_6TH_JAN_2014);
		actual = a.getCalendar();
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY),
				actual.get(Calendar.HOUR_OF_DAY));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE),
				actual.get(Calendar.MINUTE));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_MONTH) + 7,
				actual.get(Calendar.DAY_OF_MONTH));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_WEEK),
				actual.get(Calendar.DAY_OF_WEEK));
}
	
	public void testRecurringAlarmTodayInPast() {
		// 1 hour in past
		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014,
				MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY) - 1,
				MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE) + 2,
				EnumSet.of(Recurrence.MONDAY), alarmIntentId++);
		a.setNextAlarm(MON_13_44_6TH_JAN_2014);
		Calendar actual = a.getCalendar();
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY) - 1,
				actual.get(Calendar.HOUR_OF_DAY));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE) + 2,
				actual.get(Calendar.MINUTE));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_MONTH) + 7,
				actual.get(Calendar.DAY_OF_MONTH));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_WEEK),
				actual.get(Calendar.DAY_OF_WEEK));
	}

	public void testRecurringAlarmTodayInFuture() {
		// 1 hour in future
		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014,
				MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY) + 1,
				MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE),
				EnumSet.of(Recurrence.MONDAY), alarmIntentId++);
		a.setNextAlarm(MON_13_44_6TH_JAN_2014);
		Calendar actual = a.getCalendar();
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY) + 1,
				actual.get(Calendar.HOUR_OF_DAY));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE),
				actual.get(Calendar.MINUTE));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_MONTH),
				actual.get(Calendar.DAY_OF_MONTH));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_WEEK),
				actual.get(Calendar.DAY_OF_WEEK));
	}

	public void testRecurringAlarmTomorrow() {
		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014,
				MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY) - 1,
				MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE) + 2,
				EnumSet.of(Recurrence.TUESDAY), alarmIntentId++);
		a.setNextAlarm(MON_13_44_6TH_JAN_2014);
		Calendar actual = a.getCalendar();
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY) - 1,
				actual.get(Calendar.HOUR_OF_DAY));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE) + 2,
				actual.get(Calendar.MINUTE));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_MONTH) + 1,
				actual.get(Calendar.DAY_OF_MONTH));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_WEEK) + 1,
				actual.get(Calendar.DAY_OF_WEEK));
	}

	public void testRecurringAlarmOnSunday() {
		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014,
				MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY),
				MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE),
				EnumSet.of(Recurrence.SUNDAY), alarmIntentId++);
		a.setNextAlarm(MON_13_44_6TH_JAN_2014);
		Calendar actual = a.getCalendar();
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY),
				actual.get(Calendar.HOUR_OF_DAY));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE),
				actual.get(Calendar.MINUTE));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_MONTH) + 6,
				actual.get(Calendar.DAY_OF_MONTH));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_WEEK) - 1,
				actual.get(Calendar.DAY_OF_WEEK));
	}

	public void testRecurringAlarmOnWeekends() {
		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014,
				MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY) + 1,
				MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE),
				WEEKENDS, alarmIntentId++);
		a.setNextAlarm(MON_13_44_6TH_JAN_2014);
		Calendar actual = a.getCalendar();
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY) + 1,
				actual.get(Calendar.HOUR_OF_DAY));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE),
				actual.get(Calendar.MINUTE));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_MONTH) + 5,
				actual.get(Calendar.DAY_OF_MONTH));
		assertEquals(Calendar.SATURDAY,
				actual.get(Calendar.DAY_OF_WEEK));
		
		// Now set the date to be Saturday evening
		a.setNextAlarm(SAT_18_00_11TH_JAN_2014);
		actual = a.getCalendar();
		assertEquals(14,
				actual.get(Calendar.HOUR_OF_DAY));
		assertEquals(44,
				actual.get(Calendar.MINUTE));
		assertEquals(SAT_18_00_11TH_JAN_2014.get(Calendar.DAY_OF_MONTH) + 1,
				actual.get(Calendar.DAY_OF_MONTH));
		assertEquals(Calendar.SUNDAY,
				actual.get(Calendar.DAY_OF_WEEK));
	}
}
