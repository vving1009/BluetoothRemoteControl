package com.example.android.BluetoothChat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.text_input)
    TextView textInput;
    @BindView(R.id.server_btn)
    Button serverBtn;
    @BindView(R.id.client_btn)
    Button clientBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.server_btn)
    public void onServerBtnClicked() {
        BluetoothServerActivity.startActivity(this);
    }

    @OnClick(R.id.client_btn)
    public void onClientBtnClicked() {
        BluetoothClientActivity.startActivity(this);
    }
}
