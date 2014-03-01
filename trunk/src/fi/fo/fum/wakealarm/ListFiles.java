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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListFiles extends ListActivity {
	private List<String> directoryEntries = new ArrayList<String>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Resources res = getResources(); // if you are in an activity
		AssetManager am = res.getAssets();
		String fileList[] = null;
		try {
			fileList = am.list("alarmtones");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (fileList != null) {
			for (int i = 0; i < fileList.length; i++) {
				this.directoryEntries.add(fileList[i]);
				Logger.log(fileList[i]);
			}
		}

		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
				R.layout.file_row, this.directoryEntries);
		setListAdapter(directoryList);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int pos, long id) {
		File clickedFile = new File(this.directoryEntries.get(pos));
		Intent i = getIntent();
		i.putExtra("clicked_file",  "alarmtones/" + clickedFile.toString());
		Logger.log("onListItemClick clicked_file " + clickedFile.toString());
		this.setResult(RESULT_OK, i);
		finish();
	}

}
