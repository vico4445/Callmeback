package com.example.victorlecointre.callmeback;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

/**
 * Created by victorlecointre on 04/01/15.
 */

public class CallReceiver extends BroadcastReceiver {

    private Secretary secretary;

    public CallReceiver(Secretary sec){
        this.secretary = sec;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        secretary.onReceiveCall(context,intent);
//        Bundle extras = intent.getExtras();
//        if (extras != null) {
//            String state = extras.getString(TelephonyManager.EXTRA_STATE);
//            Log.i("MY_DEBUG_TAG", state);
//            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                String phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
//                Log.i("MY_DEBUG_TAG", phoneNumber);
//                try {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    //smsManager.sendTextMessage(phoneNumber, null, MainActivity.sms, null, null);
//                    Toast.makeText(context, "SMS sent.(debug mode)", Toast.LENGTH_LONG).show();
//                } catch (Exception e) {
//                    Toast.makeText(context, "SMS failed, please try again.", Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//                }
//            }
//        }
    }
}
