package com.example.remotecontrolservice;

import android.util.Log;

import java.io.OutputStream;

public class ShellCmd {

    private static final String TAG = "liwei";

    private static ShellCmd INSTANCE;

    private ShellCmd() {

    }

    public synchronized static ShellCmd getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ShellCmd();
        }
        return INSTANCE;
    }

    private OutputStream os;


    /**
     * 执行shell指令
     *
     * @param cmd
     *            指令
     */
    public final void exec(String cmd) {
        Log.d(TAG, "exec: "+cmd);

        try {
            if (os == null) {
                os = Runtime.getRuntime().exec("su").getOutputStream();
            }
            os.write(cmd.getBytes());
            os.flush();
            Log.d(TAG, "exec: finish: " + System.currentTimeMillis());
        } catch (Exception e) {
            Log.d(TAG, "err : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 后台模拟全局按键
     *
     * @param keyCode
     *            键值
     */
    public final void simulateKey(int keyCode) {
        exec("input keyevent " + keyCode + "\n");
    }
}
