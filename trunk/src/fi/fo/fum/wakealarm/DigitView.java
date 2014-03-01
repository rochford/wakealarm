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

import java.util.ArrayList;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DigitView extends LinearLayout implements View.OnClickListener {

	public final static double MAX_TIME = 60 * 60 * 24 - 1; // 23:59

	ArrayList<TextView> mList = new ArrayList<TextView>();
	public TextView hour_1, min_0, min_1;
	public int hour_value, min_0_value, min_1_value;

	public int minute_0_mod = 6;
	int minute_1_mod = 10;
	DigitViewObserver mObserver = null;

	public DigitView(Context context) {
		super(context);
		initView(context);
	}

	public DigitView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	
	public void setObserver(DigitViewObserver obs) {
		mObserver = obs;
	}

	private void initView(Context context) {
		View.inflate(context, R.layout.digit_view, this); 

		hour_value = 0;
		min_0_value = 0;
		min_1_value = 0;

		hour_1 = createDigit(R.id.hour, hour_value);
		min_0 = createDigit(R.id.minute_0, min_0_value);
		min_1 = createDigit(R.id.minute_1, min_1_value);
	}

	private TextView createDigit(int id, int initial_value) {
		TextView d = (TextView) findViewById(id);
		d.setClickable(true);
		d.setOnClickListener(this);
		d.setText(String.valueOf(initial_value));
		mList.add(d);
		return d;
	}

	@Override
	public void onClick(View v) {
		TextView i = (TextView) v;
		int id = i.getId();

		int num = 0;
		switch (id) {
		case R.id.hour:
			if (++hour_value > 23)
				hour_value = 0;
			num = hour_value;
			break;
		case R.id.minute_0:
			min_0_value = ++min_0_value % minute_0_mod;
			num = min_0_value;
			break;
		case R.id.minute_1:
			min_1_value = ++min_1_value % minute_1_mod;
			num = min_1_value;
			break;
		}

		i.setText(String.valueOf(num));
		if (mObserver != null)
			mObserver.digitChanged(getHours(), getMinutes());
	}

	public int getMinutes() {
		return (min_0_value * 10) + min_1_value;
	}

	public int getHours() {
		return hour_value;
	}

	public void setTime(int hours, int minutes) {
		hour_value = hours;
		this.hour_1.setText(String.valueOf(hour_value));

		min_0_value = minutes / 10;
		this.min_0.setText(String.valueOf(min_0_value));
		min_1_value = minutes % 10;
		this.min_1.setText(String.valueOf(min_1_value));
	}
	/*
	 * private void setDigitsTime(double secs) {
	 * 
	 * Calendar c = Calendar.getInstance(); c.clear(); double wholeSeconds =
	 * Math.floor(secs); double msecX = secs - wholeSeconds;
	 * 
	 * 
	 * Date d = c.getTime(); hour_value = d.getHours(); if ( d.getMinutes() / 10
	 * != 0) min_0_value = d.getMinutes() / 10; else min_0_value = 0;
	 * min_1_value = d.getMinutes() % 10;
	 * 
	 * Utils.setImage(mList.get(0), hour_value / hour_mod);
	 * Utils.setImage(mList.get(1), hour_value % hour_mod);
	 * Utils.setImage(mList.get(2), min_0_value); Utils.setImage(mList.get(3),
	 * min_1_value); }
	 */
}
