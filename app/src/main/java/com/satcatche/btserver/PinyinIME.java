package com.satcatche.btserver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * PinyinIME
 *
 * @author 贾博瑄
 */

public class PinyinIME extends InputMethodService {

    private Receiver mReceiver;

    private SatcatcheKeyboardContainer mView;

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
        mReceiver = new Receiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.satcatche.remoteservice.ime.key_event");
        registerReceiver(mReceiver, intentFilter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }
        mChatService = new BluetoothChatService(this);
        if (!mBluetoothAdapter.isEnabled()) {
            //Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
            Toast.makeText(this, "Bluetooth is disable", Toast.LENGTH_LONG).show();
        }
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }

    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    @Override
    public View onCreateInputView() {
        this.mView = (SatcatcheKeyboardContainer) getLayoutInflater().inflate(R.layout.ime_keyboard_container, null);
        this.mView.setService(this);
        return mView;
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }

    @Override
    public void onFinishCandidatesView(boolean finishingInput) {
        super.onFinishCandidatesView(finishingInput);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
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
        super.onWindowHidden();
        isShow = false;
    }

    @Override
    public void onWindowShown() {
        super.onWindowShown();
        isShow = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return !(!isShow || keyCode == 4) || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    public final void deleteText(int i) {
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
    }

    private static class Receiver extends BroadcastReceiver {

        private final PinyinIME mPinyinIME;

        public Receiver(PinyinIME pinyinIME) {
            this.mPinyinIME = pinyinIME;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                InputConnection currentInputConnection = mPinyinIME.getCurrentInputConnection();
                if (intent != null && intent.getAction() != null) {
                    if (intent.getAction().equals("com.satcatche.remoteservice.ime.key_event")) {
                        int intExtra = intent.getIntExtra("keycode", 0);
                        int intExtra2 = intent.getIntExtra("action", 0);
                        if (mPinyinIME.isShow && intExtra != 4) {
                            PinyinIME pinyinIME = mPinyinIME;
                            KeyEvent keyEvent = new KeyEvent(0, 0, 0, intExtra, 0, 0, 0, 0, 2);
                            KeyEvent keyEvent2 = new KeyEvent(0, 0, 1, intExtra, 0, 0, 0, 0, 2);
                            pinyinIME.onKeyDown(intExtra, keyEvent);
                            pinyinIME.onKeyUp(intExtra, keyEvent2);
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
    }
}
