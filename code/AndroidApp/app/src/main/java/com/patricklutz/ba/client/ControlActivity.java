package com.patricklutz.ba.client;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by privat-patrick on 16.09.2015.
 */
public abstract class ControlActivity extends Activity {

    protected CommandManager cmdManager;

    protected TextView countP1;
    protected TextView countP2;

    private Websocket connection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cmdManager = new CommandManager(this);

        countP1 = (TextView) findViewById(R.id.countP1Text);
        countP2 = (TextView) findViewById(R.id.countP2Text);

        connection = Websocket.getInstance(connectionListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        cmdManager.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cmdManager.stop();
    }

    ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void onConnectionEstablished() {

        }

        @Override
        public void onClose() {

            String message = "Connection refused!";
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            finish();
        }
    };
}
