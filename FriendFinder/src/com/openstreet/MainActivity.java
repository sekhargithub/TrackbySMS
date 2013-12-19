package com.openstreet;

import java.util.Calendar;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
	WebView webview;
	int clsId = 192836;
	AlarmManager am = null;
	private PendingIntent pintent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("Friend Finder");
		setContentView(R.layout.main);
		
		ConnectivityManager cm =
		        (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		
		if(isConnected)
		{
		//****************************************************
		webview = (WebView) findViewById(R.id.webview1);
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadUrl("http://sekhar4u.herokuapp.com/track.jsp");
		webview.requestFocus();
		//*****************************************************
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());

		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(MainActivity.this,
				UploadLocationService.class);
		pintent = PendingIntent.getService(MainActivity.this, 0, intent, 0);

		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				2 * 60 * 1000, pintent);
		
		}
		else
			Toast.makeText(this, "There's no network", 200).show();
	
	}

	public void onStop(View view) {
		view.setEnabled(false);
		stopService(new Intent(MainActivity.this, UploadLocationService.class));
		am.cancel(pintent);
		finish();
	}

	public void onView(View view) {
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("http://sekhar4u.herokuapp.com/track.jsp"));
		startActivity(intent);
	}

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (UploadLocationService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}