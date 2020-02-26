package com.spark.dehydrationapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private IntentFilter chargingIntentFilter;

    private IntentFilter headsetIntentFilter;

    private IntentFilter airplaneIntentFilter;

    private ImageView chargingImageStatus;
    private ImageView flightImageStatus;

    private TextView chargingTextStatus;
    private TextView flightTextStatus;

    private ChargingBroadcastReceiver chargingBroadcastReceiver;

    private AirplaneBroadcastReceiver airplaneBroadcastReceiver;

    private HeadsetBroadcastReceiver headsetBroadcastReceiver;

//    TODO: Create for Airplane Mode, Headset Plugged, Charging, Wifi, Hotspot

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chargingIntentFilter = new IntentFilter();
        chargingIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        chargingIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
//        Intent.AC
        headsetIntentFilter = new IntentFilter();
        headsetIntentFilter.addAction(Intent.ACTION_HEADSET_PLUG);

        airplaneIntentFilter = new IntentFilter();
        airplaneIntentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);

        chargingBroadcastReceiver = new ChargingBroadcastReceiver();

        airplaneBroadcastReceiver = new AirplaneBroadcastReceiver();

        headsetBroadcastReceiver = new HeadsetBroadcastReceiver();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChargeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.charge:
                            selectedFragment = new ChargeFragment();
                            break;
                        case R.id.airplane:
                            selectedFragment = new AirplaneFragment();
                            break;
                        case R.id.headset:
                            selectedFragment = new HeadsetFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(chargingBroadcastReceiver);
        unregisterReceiver(headsetBroadcastReceiver);
        unregisterReceiver(airplaneBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(chargingBroadcastReceiver, chargingIntentFilter);
        registerReceiver(headsetBroadcastReceiver, headsetIntentFilter);
        registerReceiver(airplaneBroadcastReceiver, airplaneIntentFilter);
    }


    public void showCharging(boolean isCharging) {
        chargingImageStatus = findViewById(R.id.chargeStatus);

        chargingTextStatus = findViewById(R.id.chargeTextStatus);

        if (isCharging) {
            chargingTextStatus.setText(R.string.charging);
            chargingTextStatus.setTextColor(getResources().getColor(R.color.colorCharging));
            chargingImageStatus.setImageResource(R.drawable.ic_battery_charging);
            Toast.makeText(this, "Device Charging", Toast.LENGTH_SHORT).show();
        } else {
            chargingTextStatus.setTextColor(getResources().getColor(R.color.colorUnplugged));
            chargingTextStatus.setText(R.string.unplugged);
            chargingImageStatus.setImageResource(R.drawable.ic_battery_unplugged);
            Toast.makeText(this, "Device Unplugged", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isFlightEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    }

    public void showFlightMode(boolean isEnabled) {
        flightImageStatus = findViewById(R.id.airplaneStatus);

        flightTextStatus = findViewById(R.id.airplaneTextStatus);

        if (isEnabled) {
            flightTextStatus.setText(R.string.airplanemodeon);
            flightTextStatus.setTextColor(getResources().getColor(R.color.colorCharging));
            flightImageStatus.setImageResource(R.drawable.ic_airplanemode_active);
            Toast.makeText(this, "Flight Mode Active", Toast.LENGTH_SHORT).show();
        } else {
            flightTextStatus.setTextColor(getResources().getColor(R.color.colorUnplugged));
            flightTextStatus.setText(R.string.airplanemodeoff);
            flightImageStatus.setImageResource(R.drawable.ic_airplanemode_inactive);
            Toast.makeText(this, "Flight Mode Off", Toast.LENGTH_SHORT).show();
        }
    }

    public void showPluggedStatus(boolean isPlugged) {
        ImageView headsetImageStatus = findViewById(R.id.headsetStatus);

        TextView headsetTextStatus = findViewById(R.id.headsetTextStatus);

        if (isPlugged) {
            headsetTextStatus.setText(R.string.headsetplugged);
            headsetTextStatus.setTextColor(getResources().getColor(R.color.colorCharging));
            headsetImageStatus.setImageResource(R.drawable.ic_headset_plugged);
            Toast.makeText(this, "Headset Plugged", Toast.LENGTH_SHORT).show();
        } else {
            headsetTextStatus.setTextColor(getResources().getColor(R.color.colorUnplugged));
            headsetTextStatus.setText(R.string.headsetunplugged);
            headsetImageStatus.setImageResource(R.drawable.ic_headset_unplugged);
            Toast.makeText(this, "Headset Unplugged", Toast.LENGTH_SHORT).show();
        }
    }

    public class ChargingBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean isCharging = (action.equals(Intent.ACTION_POWER_CONNECTED));
            showCharging(isCharging);
        }
    }

    public class HeadsetBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        //unplugged
                        showPluggedStatus(false);
                        break;
                    case 1:
                        showPluggedStatus(true);
                        //plugged
                        break;
                    default:
                }
            }
        }
    }

    public class AirplaneBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean isEnabled = isFlightEnabled(context);
            showFlightMode(isEnabled);
        }
    }

}
