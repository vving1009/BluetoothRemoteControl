package com.example.android.BluetoothChat;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

public class InputTextDialog extends Dialog {

    public InputTextDialog(@NonNull Context context) {
        super(context);
    }

    public InputTextDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected InputTextDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.input_dialog_layout, null, false);
        setContentView(view);
    }
}
