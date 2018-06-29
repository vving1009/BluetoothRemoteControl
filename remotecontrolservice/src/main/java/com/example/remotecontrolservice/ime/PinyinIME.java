package com.example.remotecontrolservice.ime;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import com.example.remotecontrolservice.BluetoothChatService;
import com.example.remotecontrolservice.BluetoothBean;
import com.example.remotecontrolservice.R;
import com.google.gson.Gson;

/**
 * PinyinIME
 *
 * @author 贾博瑄
 */

public class PinyinIME extends InputMethodService {

    private static final String TAG = "liwei";

    private final int TEXT = 101;

    private final int KEYCODE = 102;

    //private Receiver mReceiver;

    //private SatcatcheKeyboardContainer mView;

    private boolean isShow = false;

    private int inputType = KEYCODE;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "RemoteControlService onCreate: ");
        /*mReceiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.satcatche.remoteservice.ime.key_event");
        registerReceiver(mReceiver, intentFilter);*/
        initBluetooth();
    }

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            stopSelf();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableIntent);
            // Otherwise, setup the chat session
        }

        if (mChatService == null) {
            // Initialize the BluetoothChatService to perform bluetooth connections
            mChatService = new BluetoothChatService(this);
        }

        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.setReceiveMessageListener(message -> {
                    Log.d(TAG, "receiveMessageListener: message= " + message);
                    BluetoothBean bean = getBean(message);
                    switch (bean.getType()) {
                        case "text":
                            Log.d(TAG, "initBluetooth: TEXT message= " + message);
                            sendText(bean.getMessage());
                            break;
                        case "keycode":
                            Log.d(TAG, "initBluetooth: KEYCODE message= " + message);
                            sendDownUpKeyEvents(Integer.parseInt(bean.getMessage()));
                            break;
                    }
                    /*if (inputType == KEYCODE) {
                        Log.d(TAG, "initBluetooth: KEYCODE message= " + message);
                        sendDownUpKeyEvents(Integer.parseInt(message));
                        switch (message) {
                            case "ok":
                                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_CENTER);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_CENTER, 0);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_CENTER, 1);
                                break;
                            case "left":
                                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_LEFT, 0);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_LEFT, 1);
                                break;
                            case "up":
                                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_UP);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_UP, 0);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_UP, 1);
                                break;
                            case "right":
                                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_RIGHT);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_RIGHT, 0);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_RIGHT, 1);
                                break;
                            case "down":
                                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_DOWN);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_DOWN, 0);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_DPAD_DOWN, 1);
                                break;
                            case "back":
                                sendDownUpKeyEvents(KeyEvent.KEYCODE_BACK);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_BACK, 0);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_BACK, 1);
                                break;
                            case "home":
                                sendDownUpKeyEvents(KeyEvent.KEYCODE_HOME);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_HOME, 0);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_HOME, 1);
                                break;
                            case "menu":
                                sendDownUpKeyEvents(KeyEvent.KEYCODE_MENU);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_MENU, 0);
//                                sendKeyCodeToIme(KeyEvent.KEYCODE_MENU, 1);
                                break;
                        }
                    } else if (inputType == TEXT) {
                        Log.d(TAG, "initBluetooth: TEXT message= " + message);
                        sendText(message);
                    }*/
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
    }

    private Gson gson = new Gson();

    private BluetoothBean getBean(String message) {
        return gson.fromJson(message, BluetoothBean.class);
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

/*    private void sendKeyCodeToIme(final int keycode, int action) {
        // 发送给自定义的输入法
        Intent intent = new Intent("com.satcatche.remoteservice.ime.key_event");
        intent.putExtra("keycode", keycode);
        intent.putExtra("action", action);
        sendBroadcast(intent);
    }*/

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            //mOutStringBuffer.setLength(0);
        }
    }

/*    @Override
    public View onCreateInputView() {
        this.mView = (SatcatcheKeyboardContainer) getLayoutInflater().inflate(R.layout.ime_keyboard_container, null);
        this.mView.setService(this);
        return mView;
    }*/

/*    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }*/

/*    @Override
    public void onFinishCandidatesView(boolean finishingInput) {
        super.onFinishCandidatesView(finishingInput);
    }*/

    @Override
    public void onDestroy() {
        //unregisterReceiver(mReceiver);
        Log.d(TAG, "PinyinIme onDestroy: ");
        if (mChatService != null) {
            mChatService.stop();
        }
        super.onDestroy();
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        if (info.inputType == 0) {
            requestHideSelf(0);
        }
    }

    @Override
    public void onWindowHidden() {
        Log.d(TAG, "onWindowHidden: ");
        super.onWindowHidden();
        isShow = false;
        inputType = KEYCODE;
        sendMessage("hide");
    }

    @Override
    public void onWindowShown() {
        Log.d(TAG, "onWindowShown: ");
        super.onWindowShown();
        isShow = true;
        inputType = TEXT;
        sendMessage("show");
    }

    private void sendText(String text) {
        if (!isShow) {
            Log.d(TAG, "ime is hidden");
            return;
        }
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            Log.d(TAG, "ic null");
            return;
        }
        //将中文等任一语系文本发送给程序
        ic.commitText(text, text.length());
    }

/*    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return !(!isShow || keyCode == 4) || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }*/

/*    public final void deleteText(int i) {
        InputConnection currentInputConnection = getCurrentInputConnection();
        if (currentInputConnection != null) {
            currentInputConnection.beginBatchEdit();
            currentInputConnection.deleteSurroundingText(i, 0);
            currentInputConnection.endBatchEdit();
        }
    }

    public final void commitText(String str) {
        InputConnection currentInputConnection = getCurrentInputConnection();
        if (currentInputConnection != null) {
            currentInputConnection.beginBatchEdit();
            currentInputConnection.commitText(str, 1);
            currentInputConnection.endBatchEdit();
        }
    }*/

/*    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                InputConnection currentInputConnection = PinyinIME.this.getCurrentInputConnection();
                if (intent != null && intent.getAction() != null) {
                    if (intent.getAction().equals("com.satcatche.remoteservice.ime.key_event")) {
                        int intExtra = intent.getIntExtra("keycode", 0);
                        int intExtra2 = intent.getIntExtra("action", 0);
                        if (PinyinIME.this.isShow && intExtra != 4) {
                            KeyEvent keyEvent = new KeyEvent(0, 0, 0, intExtra, 0, 0, 0, 0, 2);
                            KeyEvent keyEvent2 = new KeyEvent(0, 0, 1, intExtra, 0, 0, 0, 0, 2);
                            PinyinIME.this.onKeyDown(intExtra, keyEvent);
                            PinyinIME.this.onKeyUp(intExtra, keyEvent2);
                        } else if (intExtra2 == 0) {
                            currentInputConnection.sendKeyEvent(new KeyEvent(0, intExtra));
                        } else {
                            currentInputConnection.sendKeyEvent(new KeyEvent(1, intExtra));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/


}
