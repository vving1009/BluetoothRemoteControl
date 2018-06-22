package com.satcatche.btserver;

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

import com.example.android.BluetoothChat.R;


/**
 * PinyinIME
 *
 * @author 贾博瑄
 */

public class PinyinIME extends InputMethodService {

    private static final String TAG = "liwei";

    private Receiver mReceiver;

    private SatcatcheKeyboardContainer mView;

    private boolean isShow = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "PinyinIME onCreate: ");
        mReceiver = new Receiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.satcatche.remoteservice.ime.key_event");
        registerReceiver(mReceiver, intentFilter);
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
            Log.d(TAG, "pinyin receiver : constructor");
            this.mPinyinIME = pinyinIME;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "pinyin receiver onReceive: ");
            try {
                InputConnection currentInputConnection = mPinyinIME.getCurrentInputConnection();
                if (intent != null && intent.getAction() != null) {
                    if (intent.getAction().equals("com.satcatche.remoteservice.ime.key_event")) {
                        int intExtra = intent.getIntExtra("keycode", 0);
                        int intExtra2 = intent.getIntExtra("action", 0);
                        Log.d(TAG, "onReceive: keycode=" + intExtra);
                        Log.d(TAG, "onReceive: action=" + intExtra2);
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
