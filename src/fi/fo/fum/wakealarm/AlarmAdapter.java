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

import java.util.EnumSet;
import fi.fo.fum.wakealarm.AlarmItem.Recurrence;
import android.app.Activity;
//import android.app.AlertDialog;
import android.content.Context;
//import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AlarmAdapter extends BaseAdapter {
	private Activity mActivity;
	private AlarmModel mAlarmModel;
	AlarmItem tempValues = null;
	private static LayoutInflater inflater = null;
	private AlarmManagerClient mAlarmMgrClient = null;

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements OnClickListener{           
        private int mPosition;
         
        OnItemClickListener(int position){
             mPosition = position;
        }
         
        @Override
        public void onClick(View v) {
        	
        	// if it is a power alarm show the delete dialog
        	if (mAlarmModel.get(mPosition).getAlarmId() == AlarmModel.getPowerNapAlarmId()) {
        		((AlarmMain) mActivity).cancelPowerNapDialog(mPosition);
        	} else {
    			Intent i = new Intent(mActivity.getApplicationContext(), EditAlarmActivity.class);
    			i.putExtra("com.kona.graduallightalarm.alarmindex", mPosition);
    			mActivity.startActivity(i);
        	}
        }               
    }
    
	public void cancelRepeatingTimer(int requestCode) {
		if (mAlarmMgrClient != null) {
			AlarmManagerClient.cancelAlarm(mActivity.getApplicationContext(), requestCode);
		}
	}

	/********* Create a holder Class to contain inflated xml file elements *********/
	public static class ViewHolder {
		public TextView mDescription;
		public TextView mDigitView;
		public TextView mAlarmRecurrence;
	}

	public AlarmAdapter(Activity a, /* AlarmModel list, */ Resources resLocal) {
		mActivity = a;

			mAlarmModel = AlarmModel.getInstance();

		mAlarmMgrClient = new AlarmManagerClient(a.getApplicationContext());
		Intent i = new Intent(
				"com.kona.graduallightalarm.AlarmManagerBroadcastReceiver");
		i.setClass(a.getApplicationContext(), AlarmManagerBroadcastReceiver.class);
		a.getApplicationContext().startService(i);

		inflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mAlarmModel.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		ViewHolder holder;

		if (convertView == null) {

			/****** Inflate tabitem.xml file for each row ( Defined below ) *******/
			vi = inflater.inflate(R.layout.alarm_row, null);

			/****** View Holder Object to contain tabitem.xml file elements ******/

			holder = new ViewHolder();
			holder.mDescription = (TextView) vi.findViewById(R.id.row_textAlarmDescription);
			holder.mDigitView = (TextView) vi.findViewById(R.id.row_digitview);
			holder.mAlarmRecurrence = (TextView) vi.findViewById(R.id.row_textAlarmRecurrence);

			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		if (mAlarmModel.size() <= 0) {
		} else {
			/***** Get each Model object from Arraylist ********/
			tempValues = null;
			tempValues = mAlarmModel.get(position);

			/************ Set Model values in Holder elements ***********/

			vi.setOnClickListener(new OnItemClickListener(position));
			// if not active then italic text
/*			if (!tempValues.getActive()) {
				holder.mDigitView.setTypeface(holder.mDigitView.getTypeface(), Typeface.ITALIC);
				holder.mDescription.setTypeface(holder.mDescription.getTypeface(), Typeface.ITALIC);
				holder.mAlarmRecurrence.setTypeface(holder.mAlarmRecurrence.getTypeface(), Typeface.ITALIC);
			}
*/			holder.mDigitView.setText(Utils.shortFormatTime(tempValues.getCalendar()));
			holder.mDescription.setText(tempValues.getDescription());
			holder.mAlarmRecurrence.setText(getRecurrenceString(mActivity.getResources(), tempValues));
		}
		return vi;
	}

	public static String getRecurrenceString(Resources res, AlarmItem a) {
		if (a.getRecurrence().containsAll(EnumSet.of(Recurrence.SATURDAY, Recurrence.SUNDAY)) && a.getRecurrence().size() == 2)
			return res.getString(R.string.recurrence_weekend);
		if (a.getRecurrence().containsAll(EnumSet.of(
				Recurrence.MONDAY, Recurrence.TUESDAY, Recurrence.WEDNESDAY, 
				Recurrence.THURSDAY, Recurrence.FRIDAY)) 
				&& a.getRecurrence().size() == 5)
			return res.getString(R.string.recurrence_weekdays);
		if (a.getRecurrence().size() == 7)
			return res.getString(R.string.recurrence_everyday);
		if (a.getRecurrence().isEmpty())
			return res.getString(R.string.recurrence_once_only);
		StringBuilder sb = new StringBuilder(20);
		if (a.getRecurrence().contains(Recurrence.MONDAY))
			sb.append(Recurrence.MONDAY.toString() + ", ");
		if (a.getRecurrence().contains(Recurrence.TUESDAY))
			sb.append(Recurrence.TUESDAY.toString() + ", ");
		if (a.getRecurrence().contains(Recurrence.WEDNESDAY))
			sb.append(Recurrence.WEDNESDAY.toString() + ", ");
		if (a.getRecurrence().contains(Recurrence.THURSDAY))
			sb.append(Recurrence.THURSDAY.toString() + ", ");
		if (a.getRecurrence().contains(Recurrence.FRIDAY))
			sb.append(Recurrence.FRIDAY.toString() + ", ");
		if (a.getRecurrence().contains(Recurrence.SATURDAY))
			sb.append(Recurrence.SATURDAY.toString() + ", ");
		if (a.getRecurrence().contains(Recurrence.SUNDAY))
			sb.append(Recurrence.SUNDAY.toString() + ", ");
		return sb.toString();
	}
}
