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

import fi.fo.fum.wakealarm.AlarmItem;
import fi.fo.fum.wakealarm.AlarmModel;
import fi.fo.fum.wakealarm.AlarmItem.Recurrence;

import junit.framework.TestCase;

public class AlarmModelTest extends TestCase {

	private static Calendar MON_13_44_6TH_JAN_2014 = Calendar.getInstance();

	public AlarmModelTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		AlarmModel mdl = AlarmModel.getInstance();
		mdl.clear();

		MON_13_44_6TH_JAN_2014.clear(Calendar.SECOND);
		MON_13_44_6TH_JAN_2014.clear(Calendar.MINUTE);
		MON_13_44_6TH_JAN_2014.clear(Calendar.HOUR_OF_DAY);
		MON_13_44_6TH_JAN_2014.clear(Calendar.DAY_OF_WEEK);
		MON_13_44_6TH_JAN_2014.clear(Calendar.DAY_OF_MONTH);
		MON_13_44_6TH_JAN_2014.clear(Calendar.MONTH);
		MON_13_44_6TH_JAN_2014.clear(Calendar.YEAR);

		MON_13_44_6TH_JAN_2014.set(Calendar.SECOND, 0);
		MON_13_44_6TH_JAN_2014.set(Calendar.MINUTE, 44);
		MON_13_44_6TH_JAN_2014.set(Calendar.HOUR_OF_DAY, 13);
		MON_13_44_6TH_JAN_2014.set(Calendar.DAY_OF_WEEK, 2); // Monday
		MON_13_44_6TH_JAN_2014.set(Calendar.MONTH, Calendar.JANUARY); // Jan
		MON_13_44_6TH_JAN_2014.set(Calendar.DAY_OF_MONTH, 6); // 6th
		MON_13_44_6TH_JAN_2014.set(Calendar.YEAR, 2014); // 2014
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		AlarmModel mdl = AlarmModel.getInstance();
		mdl.clear();
	}

	public void testCreation() {

		AlarmModel mdl = AlarmModel.getInstance();
		assertNotNull(mdl);
		assertEquals(false, mdl.hasReadAlarmData());

		AlarmModel mdl2 = AlarmModel.getInstance();
		assertNotNull(mdl);

		assertEquals(true, mdl.equals(mdl2));
	}

	public void testCannotClone() {
		AlarmModel mdl = AlarmModel.getInstance();
		AlarmModel copy = mdl;
		assertEquals(true, mdl.equals(copy));
	}

	public void testTestAlarm() {
		AlarmModel mdl = AlarmModel.getInstance();
		assertEquals(0, mdl.size());
		AlarmItem test = AlarmModel.newTestAlarm();
		assertEquals(1, mdl.size());

		// must only be one, even if second is created
		AlarmItem test2 = AlarmModel.newTestAlarm();
		assertEquals(1, mdl.size());

		// check has correct Id
		assertEquals(AlarmModel.getTestAlarmId(), test2.getAlarmId());

		// cannot remove the first alarm that no longer exists
		boolean actual = mdl.remove(test);
		assertEquals(false, actual);
	}

	public void testMaxAlarms() {
		AlarmModel mdl = AlarmModel.getInstance();
		final int max = AlarmModel.getMaxAlarms();
		for (int i = 0; i < max; i++) {
			AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014, 22, i,
					EnumSet.noneOf(Recurrence.class),
					mdl.getAlarmCounterId() + 1);
			mdl.add(a);
		}
		assertEquals(max, mdl.size());

		// this should fail
		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014, 22, 50,
				EnumSet.noneOf(Recurrence.class), mdl.getAlarmCounterId() + 1);
		try {
			mdl.add(a);
			assertEquals(true, false);
		} catch (IndexOutOfBoundsException e) {
			assertEquals(true, true);
			assertEquals(max, mdl.size());
		}
		// even if alarm count is reached, then still should be able to add
		// test alarm and powernap
		AlarmModel.newPowerNap(MON_13_44_6TH_JAN_2014, MON_13_44_6TH_JAN_2014);
		assertEquals(max + 1, mdl.size());
		AlarmModel.newTestAlarm();
		assertEquals(max + 2, mdl.size());

		// ordinary alarm should still fail
		// this should fail
		try {
			mdl.add(a);
			assertEquals(true, false);
		} catch (IndexOutOfBoundsException e) {
			assertEquals(true, true);
			assertEquals(max + 2, mdl.size());
		}

	}

	public void testPowerNap() {
		AlarmModel mdl = AlarmModel.getInstance();
		assertEquals(0, mdl.size());
		AlarmItem nap = AlarmModel.newPowerNap(MON_13_44_6TH_JAN_2014,
				MON_13_44_6TH_JAN_2014);
		assertEquals(1, mdl.size());

		// must only be one, even if second is created
		AlarmItem nap2 = AlarmModel.newPowerNap(MON_13_44_6TH_JAN_2014,
				MON_13_44_6TH_JAN_2014);
		assertEquals(1, mdl.size());

		// check has correct Id
		assertEquals(AlarmModel.getPowerNapAlarmId(), nap2.getAlarmId());

		// cannot remove the first alarm that no longer exists
		boolean actual = mdl.remove(nap);
		assertEquals(false, actual);
	}

	public void testPowerNapHourCrossOver() {
		AlarmModel mdl = AlarmModel.getInstance();
		assertEquals(0, mdl.size());

		int napTime = 45; // mins

		Calendar nap = (Calendar) MON_13_44_6TH_JAN_2014.clone();
		nap.add(Calendar.MINUTE, napTime);

		AlarmItem a = AlarmModel.newPowerNap(MON_13_44_6TH_JAN_2014, nap);
		a.setNextAlarm(MON_13_44_6TH_JAN_2014);

		Calendar actual = a.getCalendar();
		assertEquals(14, actual.get(Calendar.HOUR_OF_DAY));
		assertEquals(29, actual.get(Calendar.MINUTE));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_MONTH),
				actual.get(Calendar.DAY_OF_MONTH));
	}

	public void testPowerNapManyHours() {
		AlarmModel mdl = AlarmModel.getInstance();
		assertEquals(0, mdl.size());

		int napTime = 120; // mins

		Calendar nap = (Calendar) MON_13_44_6TH_JAN_2014.clone();
		nap.add(Calendar.MINUTE, napTime);

		AlarmItem a = AlarmModel.newPowerNap(MON_13_44_6TH_JAN_2014, nap);
		a.setNextAlarm(MON_13_44_6TH_JAN_2014);

		Calendar actual = a.getCalendar();
		assertEquals(13 + 2, actual.get(Calendar.HOUR_OF_DAY));
		assertEquals(44, actual.get(Calendar.MINUTE));
		assertEquals(MON_13_44_6TH_JAN_2014.get(Calendar.DAY_OF_MONTH),
				actual.get(Calendar.DAY_OF_MONTH));
	}

	public void testAddAlarm() {
		AlarmModel mdl = AlarmModel.getInstance();

		// alarmId TestAlarmId is reserved
		int alarmId = AlarmModel.getTestAlarmId();
		try {
			AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014, 22, 10,
					EnumSet.noneOf(Recurrence.class), alarmId);
			mdl.add(a);
			assertEquals(true, false); // not ok
		} catch (Exception e) {
			assertEquals(0, mdl.size());
			assertEquals(true, true); // ok
		}

		// alarmId powernap is reserved
		alarmId = AlarmModel.getPowerNapAlarmId();
		try {
			AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014, 22, 10,
					EnumSet.noneOf(Recurrence.class), alarmId);
			mdl.add(a);
			assertEquals(true, false); // not ok
		} catch (Exception e) {
			assertEquals(0, mdl.size());
			assertEquals(true, true); // ok
		}

		// alarmId powernap is reserved
		alarmId = mdl.getAlarmCounterId();

		AlarmItem a = new AlarmItem(MON_13_44_6TH_JAN_2014, 22, 10,
				EnumSet.noneOf(Recurrence.class), alarmId);
		mdl.add(a);
		assertEquals(1, mdl.size());
	}

	public void testIndexOf() {
		AlarmModel mdl = AlarmModel.getInstance();
		int alarmId = mdl.getAlarmCounterId();

		AlarmItem first = new AlarmItem(MON_13_44_6TH_JAN_2014, 22, 10,
				EnumSet.noneOf(Recurrence.class), alarmId);
		mdl.add(first);
		assertEquals(1, mdl.size());

		alarmId = mdl.getAlarmCounterId();
		AlarmItem second = new AlarmItem(MON_13_44_6TH_JAN_2014, 23, 10,
				EnumSet.noneOf(Recurrence.class), alarmId);
		mdl.add(second);
		assertEquals(2, mdl.size());

		mdl.sort();

		int index = mdl.indexOf(first);
		assertEquals(0, index);

		index = mdl.indexOf(second);
		assertEquals(1, index);
	}
}
