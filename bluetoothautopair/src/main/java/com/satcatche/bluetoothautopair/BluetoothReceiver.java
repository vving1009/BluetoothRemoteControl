package com.satcatche.bluetoothautopair;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

/**
 * BluetoothReceiver
 *
 * @author 贾博瑄
 */
public class BluetoothReceiver extends BroadcastReceiver {

    private static final String PIN = "0000";  //此处为你要连接的蓝牙设备的初始密钥，一般为1234或0000

    private static final String DEVICE_NAME = "rk3288";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice btDevice;
        btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            Log.d("BluetoothReceiver", "发现设备: [" + btDevice.getName() + "]" + ":" + btDevice.getAddress());
            if (!TextUtils.isEmpty(btDevice.getName()) && btDevice.getName().contains(DEVICE_NAME))//HC-05设备如果有多个，第一个搜到的那个会被尝试。
            {
                if (btDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    try {
                        ClsUtils.createBond(btDevice.getClass(), btDevice);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
            Log.d("BluetoothReceiver", "配对请求: [" + btDevice.getName() + "]" + ":" + btDevice.getAddress());
            if (btDevice.getName().contains(DEVICE_NAME)) {
                try {
                    //1.确认配对
                    //ClsUtils.setPairingConfirmation(btDevice.getClass(), btDevice, true);
                    //2.终止有序广播
                    //abortBroadcast();
                    //3.调用setPin方法进行配对...
                    ClsUtils.setPin(btDevice.getClass(), btDevice, PIN);
                    ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
