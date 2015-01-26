package com.example.victorlecointre.callmeback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;


public class MainActivity extends Activity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // Variables for Google Sign-In
    private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;
    private static final int REQ_SIGN_IN_REQUIRED = 55664;


    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;

    private TextView mSignInStatus;
    private SignInButton mSignInButton;
    private View mSignOutButton;
    private View mRevokeAccessButton;
    private GoogleApiClient mGoogleApiClient= null;

    private ProgressDialog mConnectionProgressDialog;

    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    //private PlusClient mPlusClient;

    private ConnectionResult mConnectionResult;
//    SignInButton GoogleSignIn;
 //   Button GoogleSignOut;


    public static String sms = "message ici";

    // Global Variable to be used in CallReceiver
    static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Switch mySwitch;
    static final int PICK_CONTACT_REQUEST = 1;

    private Context mContext = this;

    //Model associated
    private Secretary secretary;

    //Receiver
    private CallReceiver Receiver;
    private IntentFilter callIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Create model
        secretary = new Secretary(getApplicationContext());

        //Create CallReceiver and give it the model
        Receiver = new CallReceiver(secretary);
        callIntentFilter = new IntentFilter("android.intent.action.PHONE_STATE");

        //Attatch Switch to CallReceiver
        mySwitch = (Switch) findViewById(R.id.switch1);
        mySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    Log.i(LOG_TAG,"is On");
                    mySwitch.setText("is On");
                    enableBroadcastReceiver();

                }else{
                    Log.i(LOG_TAG,"is Off");
                    mySwitch.setText("is Off");
                    disableBroadcastReceiver();
                }
            }
        });

        // Sign-In part
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API, Plus.PlusOptions.builder()
                        .addActivityTypes("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity").build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mConnectionProgressDialog = new ProgressDialog(this);
        mConnectionProgressDialog.setMessage("Signing in...");

        mSignInButton=(SignInButton) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);
        mSignInButton.setVisibility(View.VISIBLE);

        mSignOutButton=(Button)findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(this);

        //mGoogleApiClient.connect();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        secretary.SynchronizeCalendar(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    //OnClickListener
    @Override
    public void onClick(View view) {
        Log.d(LOG_TAG, "Button Clicked");
        switch (view.getId()) {
            case R.id.sign_in_button:
                if (!mGoogleApiClient.isConnecting()) {
                    int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
                    if (available != ConnectionResult.SUCCESS) {
                        showDialog(DIALOG_GET_GOOGLE_PLAY_SERVICES);
                        return;
                    } else if (mConnectionResult == null) {
                        Log.d(LOG_TAG, "Button Sign-In showProgress");
                        mConnectionProgressDialog.show();
                    } else {
                        Log.d(LOG_TAG, "Button mConnectionResult not null");
                        try {
                            Log.d(LOG_TAG, "Button try");
                            mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                            Log.d(LOG_TAG, "Button waiting");

                        } catch (IntentSender.SendIntentException e) {
                            // Nouvelle tentative de connexion
                            Log.d(LOG_TAG, "Button error");
                            mConnectionResult = null;
                            mGoogleApiClient.connect();
                        }

                        //mSignInClicked = true;
                        //mSignInStatus.setText("Signing in...");
                        //mConnectionProgressDialog.show();
                    }
                }
                break;
            case R.id.sign_out_button:
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mSignInButton.setVisibility(View.VISIBLE);
                    mSignOutButton.setVisibility(View.INVISIBLE);
                    mGoogleApiClient.reconnect();
                }
                break;
/*        if (view.getId() == R.id.sign_in_button && !mPlusClient.isConnected()) {
            Log.d(LOG_TAG, "Button Sign-In Clicked");
            if (mConnectionResult == null) {
                Log.d(LOG_TAG, "Button Sign-In showProgress");
                mConnectionProgressDialog.show();
            } else {
                Log.d(LOG_TAG, "Button mConnectionResult not null");
                try {
                    Log.d(LOG_TAG, "Button try");
                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                    Log.d(LOG_TAG, "Button waiting");

                } catch (IntentSender.SendIntentException e) {
                    // Nouvelle tentative de connexion
                    Log.d(LOG_TAG, "Button error");
                    mConnectionResult = null;
                    mPlusClient.connect();
                }
            }
        }
        else if (view.getId() == R.id.sign_out_button) {
            Log.d(LOG_TAG, "Button Sign-Out Clicked");
            if (mPlusClient.isConnected()) {
                mPlusClient.clearDefaultAccount();
                mPlusClient.disconnect();
                mPlusClient.connect();
            }
        }*/
        }
    }


    // CallReceiver
    public void enableBroadcastReceiver(){
        registerReceiver (Receiver, callIntentFilter);

//        ComponentName receiver = new ComponentName(this, CallReceiver.class); // Normally it's the package in input not "this"
//        PackageManager pm  = this.getPackageManager();
//        pm.setComponentEnabledSetting(receiver,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
//                PackageManager.DONT_KILL_APP);
//        Toast.makeText(getApplicationContext(), "activated", Toast.LENGTH_LONG).show();
    }

    public void disableBroadcastReceiver() {
        unregisterReceiver (Receiver);

//        ComponentName receiver = new ComponentName(this, CallReceiver.class);
//        PackageManager pm = this.getPackageManager();
//        pm.setComponentEnabledSetting(receiver,
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP);
//        Toast.makeText(getApplicationContext(), "Disabled broadcst receiver", Toast.LENGTH_SHORT).show();
    }

    // Google Sign-In part
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mConnectionProgressDialog.isShowing()) {
            // L'utilisateur a déjà appuyé sur le bouton de connexion. Commencer à résoudre
            // les erreurs de connexion. Attendre jusqu'à onConnected() pour masquer la
            // boîte de dialogue de connexion.
            if (result.hasResolution()) {
                try {
                    result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    mGoogleApiClient.connect();
                }
            }
        }

        // Enregistrer l'intention afin que nous puissions lancer une activité lorsque
        // l'utilisateur clique sur le bouton de connexion.
        mConnectionResult = result;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        Log.d(LOG_TAG, "Button result");
        switch (requestCode) {
            case REQUEST_CODE_RESOLVE_ERR:
                if (responseCode == RESULT_OK) {
                    Log.d(LOG_TAG, "Button Ok");
                    mConnectionResult = null;
                    mGoogleApiClient.connect();
                } else {
                    if (responseCode == RESULT_CANCELED) {
                        Log.d(LOG_TAG, "Signed out");
                    } else {
                        Log.d(LOG_TAG, "Error during resolving recoverable error.");
                    }
                }
                break;

            case REQ_SIGN_IN_REQUIRED:
                if (responseCode == RESULT_OK) {
                    RetrieveAccessToken();
                } else {
                    Log.d(LOG_TAG, "Error while solving token error");
                }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Nous avons résolu toutes les erreurs de connexion.
        mConnectionProgressDialog.dismiss();
        Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        String currentPersonName = person != null
                ? person.getDisplayName()
                : "Unknow";
        secretary.setUserAccount(mGoogleApiClient);
        RetrieveAccessToken();

        Toast.makeText(this, currentPersonName + " is connected.", Toast.LENGTH_LONG).show();
        mSignInButton.setVisibility(View.INVISIBLE);
        mSignOutButton.setVisibility(View.VISIBLE);
    }

    // Accessing google APIs

    // Get token
    public void RetrieveAccessToken() {
        if (mGoogleApiClient != null){
            String mAccountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
            RetrieveTokenTask Task = new RetrieveTokenTask();
            Task.execute(mAccountName);
        }
    }

    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:https://www.googleapis.com/auth/calendar";
            //String scopes = "oauth2:googleapis.com/auth/plus";
            Log.e(LOG_TAG, accountName);

            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(LOG_TAG, e.getMessage());
                Log.e(LOG_TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            secretary.setToken(s);
            Log.d(LOG_TAG, "Token :" + s);
            //((TextView) findViewById(R.id.token_value)).setText("Token Value: " + s);
        }
    }

}


