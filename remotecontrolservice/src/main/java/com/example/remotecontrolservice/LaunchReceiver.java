package com.example.remotecontrolservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LaunchReceiver extends BroadcastReceiver {
    private static final String TAG = "liwei";
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context , RemoteControlService.class));
        Log.d(TAG, "LaunchReceiver onReceive: ");
    }
}
