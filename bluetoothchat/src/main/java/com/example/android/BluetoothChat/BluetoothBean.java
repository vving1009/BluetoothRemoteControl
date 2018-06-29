package com.example.android.BluetoothChat;

public class BluetoothBean {

    public static final String TEXT = "text";

    public static final String KEYCODE = "keycode";

    private String message;

    private String type;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
