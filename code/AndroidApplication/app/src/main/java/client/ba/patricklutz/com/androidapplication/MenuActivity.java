package client.ba.patricklutz.com.androidapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;


public class MenuActivity extends Activity {

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
                startControllIntent();
            }

            @Override
            public void onClose() {

            }
        });
        websocket.connect();
    }


    public void startGame(View view) {
        connect();
    }






}
