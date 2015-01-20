package com.example.victorlecointre.callmeback;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;


public class MainActivity extends Activity {
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
        secretary = new Secretary();

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
    }

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
}


