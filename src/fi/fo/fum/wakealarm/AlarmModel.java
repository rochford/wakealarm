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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fi.fo.fum.wakealarm.AlarmItem.Recurrence;

public class AlarmModel {
	private static AlarmModel INSTANCE = null;
	private boolean mReadDataFromFile = false;
	private static final int MAX_ALARMS = 10;
	private static final int TEST_ALARM_ID = 0;

	public static int getMaxAlarms() {
		return MAX_ALARMS;
	}

	public static int getTestAlarmId() {
		return TEST_ALARM_ID;
	}

	private static final int POWER_NAP_ALARM_ID = 1;
	private static int mAlarmIdCounter = POWER_NAP_ALARM_ID + 1;

	private static List<AlarmItem> mAlarmList = new ArrayList<AlarmItem>();

	private AlarmModel() {
	}

	public boolean hasReadAlarmData() {
		return mReadDataFromFile;
	}

	public static AlarmModel getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AlarmModel();
			// INSTANCE.loadState(fis);
		}

		return INSTANCE;
	}

	@Override
	public String toString() {
		return "size: " + mAlarmList.size() + ", mAlarmIdCounter:"
				+ mAlarmIdCounter;
	}

	public void saveState(FileOutputStream fos) {
		ObjectOutputStream oos = null;
		try {
			// FileOutputStream fos = c.openFileOutput("alarmzzz",
			// Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(mAlarmList);
			oos.writeObject(mAlarmIdCounter);
			oos.flush();
			oos.close();
			Logger.log("saveState done");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (oos != null)
				try {
					oos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	public void loadState(FileInputStream fis) {
		try {
			// FileInputStream fis = c.openFileInput("alarmzzz");
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object o = ois.readObject();
			if (o instanceof ArrayList<?>)
				mAlarmList = (ArrayList<AlarmItem>) o;
			o = ois.readObject();
			if (o instanceof Integer)
				mAlarmIdCounter = (Integer) o;
			ois.close();
			Logger.log("loadState done");

		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sort();
		this.mReadDataFromFile = true;
	}

	public int add(AlarmItem i) {
		if (mAlarmList.size() >= MAX_ALARMS)
			throw new IndexOutOfBoundsException("too many alarms");

		if (i.getAlarmId() == TEST_ALARM_ID)
			throw new IndexOutOfBoundsException("bad alarm id"); // TODO: XXX
																	// TIM

		if (i.getAlarmId() == POWER_NAP_ALARM_ID)
			throw new IndexOutOfBoundsException("bad alarm id"); // TODO: XXX
																	// TIM

		// add to front of list
		mAlarmList.add(0, i);
		return 0;
	}

	public int indexOf(AlarmItem a) {
		return mAlarmList.indexOf(a);
	}

	public void remove(int index) {
		mAlarmList.remove(index);
	}

	public boolean remove(AlarmItem a) {
		boolean result = mAlarmList.remove(a);
		return result;
	}

	public static void removeAlarmId(int id) {
		Iterator<AlarmItem> iter = mAlarmList.iterator();

		while (iter.hasNext()) {
			AlarmItem a = iter.next();

			if (a.getAlarmId() == id)
				iter.remove();
		}
	}

	public AlarmItem getByAlarmId(int id) {
		for (AlarmItem a : mAlarmList) {
			if (a.getAlarmId() == id)
				return a;
		}
		return null; // TODO : XXX TIM not good
	}

	public int size() {
		return mAlarmList.size();
	}

	public int alarmCount() {
		int cnt = mAlarmList.size();
		if (getByAlarmId(TEST_ALARM_ID) != null)
			--cnt;
		if (getByAlarmId(POWER_NAP_ALARM_ID) != null)
			--cnt;
		return cnt;
	}

	public static int getPowerNapAlarmId() {
		return POWER_NAP_ALARM_ID;
	}

	public AlarmItem get(int index) {
		return mAlarmList.get(index);
	}

	public int getAlarmCounterId() {
		return mAlarmIdCounter;
	}

	static public AlarmItem newTestAlarm() {
		removeAlarmId(TEST_ALARM_ID);
		Calendar now = Calendar.getInstance();
		AlarmItem alarm = new AlarmItem(now, now.get(Calendar.HOUR_OF_DAY),
				now.get(Calendar.MINUTE), EnumSet.noneOf(Recurrence.class),
				TEST_ALARM_ID);
		alarm.setDescription("TEST DESCRIPTION");

		mAlarmList.add(0, alarm);
		return alarm;
	}

	static public AlarmItem newPowerNap(Calendar now, Calendar nap) {
		removeAlarmId(POWER_NAP_ALARM_ID);
		AlarmItem alarm = new AlarmItem(now, nap.get(Calendar.HOUR_OF_DAY),
				nap.get(Calendar.MINUTE), EnumSet.noneOf(Recurrence.class),
				POWER_NAP_ALARM_ID);
		alarm.setDescription("POWERNAP");

		mAlarmList.add(0, alarm);
		return alarm;
	}

	static public AlarmItem newAlarmItem(int hrs, int mins,
			Set<Recurrence> recurrenceFlags) {
		if (mAlarmList.size() >= MAX_ALARMS)
			throw new IndexOutOfBoundsException("too many alarms");

		AlarmItem alarm = new AlarmItem(Calendar.getInstance(), hrs, mins,
				recurrenceFlags, mAlarmIdCounter++);
		// mAlarmList.add(0, alarm);
		return alarm;
	}

	public void clear() {
		mAlarmList.clear();
	}

	public void sort() {
		Collections.sort(mAlarmList);
	}
}
