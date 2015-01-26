package com.example.victorlecointre.callmeback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
//import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.plus.Plus;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by victorlecointre on 14/01/15.
 */
public class Secretary extends AbstractModel {
    private static final int REQ_SIGN_IN_REQUIRED = 55664;
    String token;

    public Secretary(Context ctx){
        SynchronizeCalendar(ctx);
    }
    // Synchronize the local calendar with the Google Calendar
    public void SynchronizeCalendar(Context context){
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

                //ManageIncomingCall(phoneNumber,d);

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

    public void setToken(String tok){
        this.token=tok;
    }




    public void setUp() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // The clientId and clientSecret can be found in Google Developers Console
        String clientId = "973098072165-ohvcjsnq7ahf1om3e8mq7bsqa6lq941r.apps.googleusercontent.com";
        String clientSecret = "";

        // Or your redirect URL for web based applications.
        String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";
        String scope = "https://www.googleapis.com/auth/calendar";

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow(
                httpTransport, jsonFactory, clientId, clientSecret, Collections.singleton(scope));
        // Step 1: Authorize
        String authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUrl).build();

        // Point or redirect your user to the authorizationUrl.
        System.out.println("Go to the following link in your browser:");
        System.out.println(authorizationUrl);

        // Read the authorization code from the standard input stream.
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("What is the authorization code?");
        String code = in.readLine();
        // End of Step 1

        // Step 2: Exchange
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUrl)
                .execute();
        // End of Step 2

        Credential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(clientId, clientSecret)
                .build().setFromTokenResponse(response);

        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("YOUR_APPLICATION_NAME").build();
    }








    // test for synch
    /*private void run() throws IOException {
        // Construct the {@link Calendar.Events.List} request, but don't execute it yet.

        Calendar.Events.List request = client.events().list("primary");


        // Load the sync token stored from the last execution, if any.
        String syncToken = syncSettingsDataStore.get(SYNC_TOKEN_KEY);
        if (syncToken == null) {
            System.out.println("Performing full sync.");

            // Set the filters you want to use during the full sync. Sync tokens aren't compatible with
            // most filters, but you may want to limit your full sync to only a certain date range.
            // In this example we are only syncing events up to a year old.
            Date oneYearAgo = Utils.getRelativeDate(java.util.Calendar.YEAR, -1);
            request.setTimeMin(new DateTime(oneYearAgo, TimeZone.getTimeZone("UTC")));
        } else {
            System.out.println("Performing incremental sync.");
            request.setSyncToken(syncToken);
        }

        // Retrieve the events, one page at a time.
        String pageToken = null;
        Calendar.Events events = null;
        do {
            request.setPageToken(pageToken);

            try {
                events = request.execute();
            } catch (GoogleJsonResponseException e) {
                if (e.getStatusCode() == 410) {
                    // A 410 status code, "Gone", indicates that the sync token is invalid.
                    System.out.println("Invalid sync token, clearing event store and re-syncing.");
                    syncSettingsDataStore.delete(SYNC_TOKEN_KEY);
                    eventDataStore.clear();
                    run();
                } else {
                    throw e;
                }
            }

            List<Event> items = events.getItems();
            if (items.size() == 0) {
                System.out.println("No new events to sync.");
            } else {
                for (Event event : items) {
                    syncEvent(event);
                }
            }

            pageToken = events.getNextPageToken();
        } while (pageToken != null);

        // Store the sync token from the last request to be used during the next execution.
        syncSettingsDataStore.set(SYNC_TOKEN_KEY, events.getNextSyncToken());

        System.out.println("Sync complete.");
    }*/






}

