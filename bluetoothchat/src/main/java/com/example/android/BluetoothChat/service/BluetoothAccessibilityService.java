package com.example.android.BluetoothChat.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * BluetoothAccessibilityService
 *
 * @author 贾博瑄
 */

public class BluetoothAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                handleNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                handleBluetoothPairingDialog();
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }

    private void handleNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        for (CharSequence text : texts) {
            String content = text.toString();
            if (content.contains("蓝牙配对请求")) {
                if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                    Notification notification = (Notification) event.getParcelableData();
                    PendingIntent pendingIntent = notification.contentIntent;
                    try {
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void handleBluetoothPairingDialog() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("配对");
            nodeInfo.recycle();
            for (AccessibilityNodeInfo item : list) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }
}
