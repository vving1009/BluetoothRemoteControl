package com.satcatche.btserver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextInput = (TextView) findViewById(R.id.text_input);
        startService();
    }

    private void startService() {
        Intent intent = new Intent(this, RemoteService.class);
        startService(intent);
    }
}
