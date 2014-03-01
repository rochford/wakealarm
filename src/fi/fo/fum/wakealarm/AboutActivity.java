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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.text.util.Linkify;

public class AboutActivity extends Activity {

	private static Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_about);
		mContext = this;
		
		TextView ver = (TextView) this.findViewById(R.id.textVersion);
		String versionName = "1.0";
		try {
			versionName = mContext.getPackageManager()
				    .getPackageInfo(mContext.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String text = String.format(
			    this.getResources().getString(R.string.version_text),
			    versionName);
		ver.setText(text);
		
		TextView source = (TextView) this.findViewById(R.id.textSourceCode);
		Linkify.addLinks(source, Linkify.WEB_URLS);
		
		TextView contact = (TextView) this.findViewById(R.id.textContact);
		Linkify.addLinks(contact, Linkify.EMAIL_ADDRESSES);

		TextView icon_copyright = (TextView) this.findViewById(R.id.textIconCopyright);
		Linkify.addLinks(icon_copyright, Linkify.WEB_URLS);

		TextView audio_copyright = (TextView) this.findViewById(R.id.textAudioCopyright);
		Linkify.addLinks(audio_copyright, Linkify.WEB_URLS);

		TextView gpl = (TextView) this.findViewById(R.id.textgpl);
		StringBuffer gplBuf = readAsset("COPYING");
	    gpl.setText(gplBuf);

		TextView lgpl = (TextView) this.findViewById(R.id.textlgpl);
		StringBuffer lgplBuf = readAsset("COPYING.LESSER");
		lgpl.setText(lgplBuf);

	}

	private StringBuffer readAsset(String file)
	{
		StringBuffer gplBuf = new StringBuffer();
	    try
	    {
	        InputStream inputStream = getAssets().open(file);

	        BufferedReader f = new BufferedReader(new InputStreamReader(inputStream));

	        String line = f.readLine();

	        while (line != null)
	        {
	        	gplBuf.append(line);
	            line = f.readLine();
	        }
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    }
		return gplBuf;
	}

	public static void launchMarket(View v) {
    	Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
// debug only
//    	Uri uri = Uri.parse("https://market.android.com/details?id=" + mContext.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
        	mContext.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, R.string.couldnt_launch_market, Toast.LENGTH_LONG).show();
        }
    }
}
