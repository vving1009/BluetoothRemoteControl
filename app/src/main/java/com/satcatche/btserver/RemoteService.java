package com.satcatche.btserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * RemoteService
 *
 * @author 贾博瑄
 */

public class RemoteService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendKeyCode(int keycode, int action) {
        Intent intent = new Intent("com.satcatche.remoteservice.ime.key_event");
        intent.putExtra("keycode", keycode);
        intent.putExtra("action", action);
        sendBroadcast(intent);
    }
}
