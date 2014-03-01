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
import java.util.TimeZone;

import junit.framework.TestCase;
import android.util.Log;

import fi.fo.fum.wakealarm.AlarmItem;
import fi.fo.fum.wakealarm.AlarmItem.Recurrence;

public class AlarmItemTest extends TestCase {

	private static int alarmIntentId = 2;
	private static Calendar MON_13_44_6TH_JAN_2014 = Calendar.getInstance();

	public AlarmItemTest(String name) {
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

		TimeZone hki = TimeZone.getTimeZone("Europe/Helsinki");
		MON_13_44_6TH_JAN_2014.setTimeZone(hki);
		MON_13_44_6TH_JAN_2014.set(Calendar.HOUR_OF_DAY, 13);
		MON_13_44_6TH_JAN_2014.set(Calendar.MINUTE, 44);
		MON_13_44_6TH_JAN_2014.set(Calendar.DAY_OF_WEEK, 2); // Monday
		MON_13_44_6TH_JAN_2014.set(Calendar.MONTH, Calendar.JANUARY); // Jan
		MON_13_44_6TH_JAN_2014.set(Calendar.DAY_OF_MONTH, 6); // 6th
		MON_13_44_6TH_JAN_2014.set(Calendar.YEAR, 2014); // 2014
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreation() {
		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014, 22, 10, EnumSet.noneOf(Recurrence.class), alarmIntentId++);
		assertEquals(22, a.getHours());
		assertEquals(10, a.getMinutes());
	}

	public void testEquals() {
		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014, 22, 10, EnumSet.noneOf(Recurrence.class), alarmIntentId++);
		AlarmItem b = new AlarmItem(MON_13_44_6TH_JAN_2014, 22, 10, EnumSet.noneOf(Recurrence.class), alarmIntentId++);
		
		boolean eq = a.equals(a);
		assertEquals(true, eq);
		eq = a.equals(b);
		assertEquals(false, eq);
		
		AlarmItem x = new AlarmItem(MON_13_44_6TH_JAN_2014, 22, 10, EnumSet.noneOf(Recurrence.class), 10);
		AlarmItem y = new AlarmItem(MON_13_44_6TH_JAN_2014, 22, 10, EnumSet.noneOf(Recurrence.class), 10);
		eq = x.equals(y);
		assertEquals(false, eq);
	}
	
	public void testNextAlarmOnceInFuture() {

		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014, MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY) + 1, 
				MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE) + 2,
									EnumSet.noneOf(Recurrence.class),
									alarmIntentId++);
		a.setNextAlarm(MON_13_44_6TH_JAN_2014);
		Calendar actual = a.getCalendar();
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY) + 1, actual.get(Calendar.HOUR_OF_DAY));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE) + 2, actual.get(Calendar.MINUTE));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_MONTH), actual.get(Calendar.DAY_OF_MONTH));
	}

	public void testNextAlarmRolledToTomorrow() {
		// 1 hour in past
		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014, MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY) - 1, 
				MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE) + 2,
									EnumSet.noneOf(Recurrence.class),
									alarmIntentId++);
		a.setNextAlarm(MON_13_44_6TH_JAN_2014);
		Calendar actual = a.getCalendar();
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY) - 1, actual.get(Calendar.HOUR_OF_DAY));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE) + 2, actual.get(Calendar.MINUTE));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_MONTH) + 1, actual.get(Calendar.DAY_OF_MONTH));
		
		// check theat the alarm is not rolled again
		a.setNextAlarm(MON_13_44_6TH_JAN_2014);
		actual = a.getCalendar();
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.HOUR_OF_DAY) - 1, actual.get(Calendar.HOUR_OF_DAY));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE) + 2, actual.get(Calendar.MINUTE));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_MONTH) + 1, actual.get(Calendar.DAY_OF_MONTH));
	}

	public void testMidnightCrossover() {
		// tomorrow
		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014, 0, 
				MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE),
									EnumSet.noneOf(Recurrence.class),
									alarmIntentId++);
		a.setNextAlarm(MON_13_44_6TH_JAN_2014);
		Calendar actual = a.getCalendar();
		Log.d("rochfordtest", MON_13_44_6TH_JAN_2014.toString());
		Log.d("rochfordtest", actual.toString());
		assertEquals(0, actual.get(Calendar.HOUR_OF_DAY));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.MINUTE), actual.get(Calendar.MINUTE));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_MONTH) + 1, actual.get(Calendar.DAY_OF_MONTH));
	}

	public void testComparable() {
		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014, 1, 2, EnumSet.noneOf(Recurrence.class), alarmIntentId++);
		AlarmItem a_copy = new AlarmItem(MON_13_44_6TH_JAN_2014, 1, 2, EnumSet.noneOf(Recurrence.class), alarmIntentId++);
		AlarmItem b = new AlarmItem(MON_13_44_6TH_JAN_2014, 2, 3, EnumSet.noneOf(Recurrence.class), alarmIntentId++);
		
		boolean eq = a.equals(a);
		assertEquals(true, eq);
		int cmp = a.compareTo(a);
		assertEquals(0, cmp);
		cmp = a.compareTo(a_copy);
		// a was created before a_copy
		assertEquals(0, cmp);
		
		AlarmItem a_exact_copy = a;
		cmp = a.compareTo(a_exact_copy);
		assertEquals(0, cmp);

		cmp = a.compareTo(b);
		assertEquals(-1, cmp);
		
/*		a.setActive(false);
		b.setActive(true);
		cmp = a.compareTo(b);
		assertEquals(1, cmp);
*/	}
	
	public void testTimeZone() {
		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014, 7, 30, EnumSet.noneOf(Recurrence.class), alarmIntentId++);
		TimeZone actual = a.getTimeZone();
		assertEquals(true, actual.equals(MON_13_44_6TH_JAN_2014.getTimeZone()));
		// check time of alarm is 7 am
		assertEquals(7, a.getHours());
		assertEquals(30, a.getMinutes());
		
		// change the time zone to London
		TimeZone london = TimeZone.getTimeZone("Europe/London");
		a.setTimeZone(london);
		assertEquals(7, a.getHours());
		assertEquals(30, a.getMinutes());
		
		// change the time zone back to original
		TimeZone hki = TimeZone.getTimeZone("Europe/Helsinki");
		a.setTimeZone(hki);
		assertEquals(7, a.getHours());
		assertEquals(30, a.getMinutes());
	}
}
