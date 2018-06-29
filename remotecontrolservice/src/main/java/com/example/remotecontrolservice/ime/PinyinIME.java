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

    private boolean isShow = false;

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
                });
                mChatService.start();
            }
        }
        ensureDiscoverable();
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

    @Override
    public void onDestroy() {
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
        sendMessage("hide");
    }

    @Override
    public void onWindowShown() {
        Log.d(TAG, "onWindowShown: ");
        super.onWindowShown();
        isShow = true;
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
}
