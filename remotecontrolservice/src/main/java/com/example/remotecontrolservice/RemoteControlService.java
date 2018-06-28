package com.example.remotecontrolservice;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class RemoteControlService extends Service {
    private static final String TAG = "liwei";

    private final boolean USE_ADB = true;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;

    private ShellCmd mShellCmd = ShellCmd.getInstance();

    public RemoteControlService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "RemoteControlService onCreate: ");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "RemoteControlService onStartCommand: ");
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            stopSelf();
        } else if (!mBluetoothAdapter.isEnabled()) {
            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableIntent);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            // Initialize the BluetoothChatService to perform bluetooth connections
            mChatService = new BluetoothChatService(this);
        }

        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.setReceiveMessageListener(message -> {
                    switch (message) {
                        case "ok":
                            if (USE_ADB) {
                                sendKeyCodeToAdb(KeyEvent.KEYCODE_DPAD_CENTER);
                            } else {
                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_CENTER, 0);
                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_CENTER, 1);
                            }
                            break;
                        case "left":
                            if (USE_ADB) {
                                sendKeyCodeToAdb(KeyEvent.KEYCODE_DPAD_LEFT);
                            } else {
                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_LEFT, 0);
                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_LEFT, 1);
                            }
                            break;
                        case "up":
                            if (USE_ADB) {
                                sendKeyCodeToAdb(KeyEvent.KEYCODE_DPAD_UP);
                            } else {
                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_UP, 0);
                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_UP, 1);
                            }
                            break;
                        case "right":
                            if (USE_ADB) {
                                sendKeyCodeToAdb(KeyEvent.KEYCODE_DPAD_RIGHT);
                            } else {
                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_RIGHT, 0);
                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_RIGHT, 1);
                            }
                            break;
                        case "down":
                            if (USE_ADB) {
                                sendKeyCodeToAdb(KeyEvent.KEYCODE_DPAD_DOWN);
                            } else {
                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_DOWN, 0);
                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_DOWN, 1);
                            }
                            break;
                        case "back":
                            if (USE_ADB) {
                                sendKeyCodeToAdb(KeyEvent.KEYCODE_BACK);
                            } else {
                                sendKeyCodeToIme(KeyEvent.KEYCODE_BACK, 0);
                                sendKeyCodeToIme(KeyEvent.KEYCODE_BACK, 1);
                            }
                            break;
                        case "home":
                            if (USE_ADB) {
                                sendKeyCodeToAdb(KeyEvent.KEYCODE_HOME);
                            } else {
                                sendKeyCodeToIme(KeyEvent.KEYCODE_HOME, 0);
                                sendKeyCodeToIme(KeyEvent.KEYCODE_HOME, 1);
                            }
                            break;
                        case "menu":
                            if (USE_ADB) {
                                sendKeyCodeToAdb(KeyEvent.KEYCODE_MENU);
                            } else {
                                sendKeyCodeToIme(KeyEvent.KEYCODE_MENU, 0);
                                sendKeyCodeToIme(KeyEvent.KEYCODE_MENU, 1);
                            }
                            break;
                    }
                });
                mChatService.start();
            }
        }

        ensureDiscoverable();

        // TODO: 2018/6/27 for test
/*        new Handler().postDelayed(() -> {
            new Thread(() -> {
                for (int i = 0; i < 30; i++) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendKeyCodeToAdb(20);
                }
            }).start();
        }, 1000);*/
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendKeyCodeToIme(final int keycode, int action) {
        // 发送给自定义的输入法
        Intent intent = new Intent("com.satcatche.remoteservice.ime.key_event");
        intent.putExtra("keycode", keycode);
        intent.putExtra("action", action);
        sendBroadcast(intent);
    }

    private void sendKeyCodeToAdb(final int keycode) {
        mShellCmd.simulateKey(keycode);
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter != null && mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            discoverableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(discoverableIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "RemoteControlService onDestroy: ");
        if (mChatService != null) {
            mChatService.stop();
        }
    }
}
