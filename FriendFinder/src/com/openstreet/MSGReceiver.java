package com.openstreet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class MSGReceiver extends BroadcastReceiver
{    
	
	String senderTel;

	@Override
	public void onReceive(Context context, Intent intent) 
	{        
		//---get the SMS message that was received---
		Bundle bundle = intent.getExtras();        
		SmsMessage[] msgs = null;
		String str="";            
		if (bundle != null)
		{
			senderTel = "";

			//---retrieve the SMS message received---
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for (int i=0; i<msgs.length; i++){
				msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
				if (i==0) {
					//---get the sender address/phone number---
					senderTel = msgs[i].getOriginatingAddress();
				} 
				//---get the message body---
				str += msgs[i].getMessageBody().toString();                	
			}

			if (str.startsWith("9347881515")) {            	
				
				Intent intentone = new Intent(context.getApplicationContext(), MainActivity.class);
				intentone.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intentone);
			/*	//---request location updates---
				lm.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 
						60000, 
						1000, 
						this);
				*/
                //---abort the broadcast; SMS messages won�t be broadcasted---
				this.abortBroadcast();
			}
		}                         
	}


}
