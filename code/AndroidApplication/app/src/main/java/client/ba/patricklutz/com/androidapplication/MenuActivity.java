package client.ba.patricklutz.com.androidapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;


public class MenuActivity extends Activity {


    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void startControllIntent() {
        Intent intent = null;
        RadioButton steuerung1 = (RadioButton) findViewById(R.id.radioSteuerung1);
        RadioButton steuerung2 = (RadioButton) findViewById(R.id.radioSteuerung2);
        if (steuerung1.isChecked()) {
            intent = new Intent(this, ControlActivityDifferential.class);
        }
        if (steuerung2.isChecked()) {
            intent = new Intent(this, ControlActivityRCRemote.class);
        }

        startActivity(intent);
    }


    private void connect() {

        Websocket websocket = Websocket.getInstance(new ConnectionListener() {
            @Override
            public void onConnectionEstablished() {
                progress.dismiss();
                startControllIntent();
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onMessage(String message) {

            }
        });
        websocket.connect();
        progress = new ProgressDialog(this);
        progress.setTitle("Wait");
        progress.setMessage("Connecting to Server...");
        progress.setCancelable(false);
        progress.show();
    }


    public void startGame(View view) {
        connect();
    }






}
