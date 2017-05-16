package com.leon.locum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;


    // Battery charging source notifier
    public class PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        final Intent mIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = mIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        int chargePlug = mIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        if (usbCharge) {
            Toast.makeText(context, R.string.usb_charging, Toast.LENGTH_SHORT).show();
        }else{
            if (acCharge) {
                Toast.makeText(context, R.string.ac_charging, Toast.LENGTH_SHORT).show();
            }
        }
    }
}