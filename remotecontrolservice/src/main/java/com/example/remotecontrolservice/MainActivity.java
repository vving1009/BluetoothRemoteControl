package com.example.remotecontrolservice;

import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "liwei";

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*findViewById(R.id.button).setOnClickListener(v -> {
            Log.d(TAG, "button click ");
            Intent intent = new Intent();
            intent.setAction("com.example.remotecontrolservice.TEST_ACTION");
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.setComponent(new
                    ComponentName("com.example.remotecontrolservice","com.example.remotecontrolservice.LaunchReceiver"));
            sendBroadcast(intent);
        });*/
        recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new Adapter());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        Intent intent = new Intent();
        intent.setAction("com.example.remotecontrolservice.TEST_ACTION");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setComponent(new
                ComponentName("com.example.remotecontrolservice","com.example.remotecontrolservice.LaunchReceiver"));
        sendBroadcast(intent);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.text.setText(String.valueOf(position));
        }

        @Override
        public int getItemCount() {
            return 30;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView text;
            public ViewHolder(View itemView) {
                super(itemView);
                text = itemView.findViewById(R.id.text);
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
