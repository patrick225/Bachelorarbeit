package client.ba.patricklutz.com.androidapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by privat-patrick on 16.09.2015.
 */
public abstract class ControlActivity extends Activity {

    private final int UI_CONN_CLOSED = 1;

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

            cmdManager.stop();
            String message = "Connection refused!";
            Message uiMessage = uiHandler.obtainMessage(UI_CONN_CLOSED, message);
            uiMessage.sendToTarget();
            finish();
        }
    };

    Handler uiHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message message) {

            if (message.what == UI_CONN_CLOSED) {
                Toast.makeText(getApplicationContext(), message.obj.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };
}
