package com.openstreet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.widget.Toast;

public class UploadLocationService extends Service implements LocationListener
{
	
	String resp;
	LocationManager lm ;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Service Created", 300).show();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
/*		ThreadPolicy tp = ThreadPolicy.LAX;
		StrictMode.setThreadPolicy(tp);*/
		lm = (LocationManager) 
				this.getSystemService(Context.LOCATION_SERVICE);    

		//---request location updates---
		
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,	60000, 1, this);
				
		/*NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
				boolean isConnected = activeNetwork != null &&
                      activeNetwork.isConnectedOrConnecting();*/
	
		//TODO do something useful
	    return Service.START_REDELIVER_INTENT;
		}

	public void raiseToast(String msg){
		Toast.makeText(getApplicationContext(), msg, 200).show();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
		
	}
	private String sendLocation(String lat, String lon) {
		resp = "";
		HttpClient httpclient = new DefaultHttpClient();
		//"http://10.0.2.2:9084/WebOAndroid/VehicleTrack";
		HttpPost httppost = new HttpPost(
				"http://sekhar4u.herokuapp.com/DBServlet");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("lat", lat));
		nameValuePairs.add(new BasicNameValuePair("lon", lon));
		//nameValuePairs.add(new BasicNameValuePair("guestbookName","default"));
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			try {
				HttpResponse response = httpclient.execute(httppost);
				resp = EntityUtils.toString(response.getEntity());
				//Toast.makeText(getApplicationContext(),"msg:"+resp, 200).show();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				resp = "Client Protocol Problem";
			} catch (IOException e) {
				resp = "There's no Network";
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			resp = "Unsupported Encoding?";
		}

		return resp;
	}

	@Override
	public void onLocationChanged(Location location) {
		if(location != null)
		{
		String slat = location.getLatitude()+"";
		String slon = location.getLongitude()+"";
		String resp = sendLocation(slat,slon);
		raiseToast(slat +" : " +slon);
		if(resp.contains("success"))
		{raiseToast("Success");}
		else
		{
			//raiseToast(resp);
		}		
		
		lm.removeUpdates(this);
		}
		
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
