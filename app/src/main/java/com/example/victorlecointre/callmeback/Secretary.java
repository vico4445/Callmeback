package com.example.victorlecointre.callmeback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
//TEST COMMIT
/**
 * Created by victorlecointre on 14/01/15.
 */
public class Secretary extends AbstractModel {

    public Secretary(){
        SynchronizeCalendar();
    }
    // Synchronize the local calendar with the Google Calendar
    public void SynchronizeCalendar(){
       // this.calendar =;
        Log.i("Secretary", "I synchronize the local calendar");
    }

    // Locally modify calendar from user input
    public void ModifyCalendar(){
        Log.i("Secretary", "I modify the local calendar");
    }

    // Return the calendar to be able to display it ( will notify the observer)
    public void GetCalendar(){
        Log.i("Secretary", "I return the calendar to the observers");
    }

    // Set the VIPList
    public void SetCalendar(){
        Log.i("Secretary", "set the calendar");
    }

    // Set the VIPList
    public void SetVIPlist(ArrayList<String> Names,String Modifier){
        Log.i("Secretary", "set VIP list");
    }

    // Dealing with incoming call
    public void onReceiveCall(Context context, Intent intent){
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            Log.i("Secretary", state);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                String phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.i("Secretary", phoneNumber);
                Calendar c = Calendar.getInstance();
                Date d = c.getTime();

                ManageIncomingCall(phoneNumber,d);

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    //smsManager.sendTextMessage(phoneNumber, null, MainActivity.sms, null, null);
                    Toast.makeText(context, "SMS sent.(debug mode)", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(context, "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }

    // Manage the incoming call
    public void ManageIncomingCall(String Number,Date d){
        Log.i("Secretary", "I take care of the incoming call");
    }
}
