package com.example.victorlecointre.callmeback;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by victorlecointre on 14/01/15.
 */
public abstract class AbstractModel {
    private ArrayList<String> VIPlist = new ArrayList<String>();
    private Intent LastCall = new Intent();
    //private Calendar calendar ;

    // Synchronize the local calendar with the Google Calendar
    public abstract void SynchronizeCalendar();

    // Locally modify calendar from user input
    public abstract void SetCalendar();

    // Return the calendar to be able to display it ( will notify the observer)
    public abstract void GetCalendar();

    // Set the list of VIP
    public abstract void SetVIPlist(ArrayList<String> Names,String Modifier);

    // Manage the incoming call
    public abstract void ManageIncomingCall(String Number,Date d);
}

