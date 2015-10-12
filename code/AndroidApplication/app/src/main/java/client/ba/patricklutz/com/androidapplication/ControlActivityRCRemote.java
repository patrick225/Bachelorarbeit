package client.ba.patricklutz.com.androidapplication;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import org.json.JSONException;
import org.json.JSONObject;

public class ControlActivityRCRemote extends ControlActivity {

    SeekbarHorizontal seekbarDirection;
    SeekbarVertical seekbarVelocity;

    ProgressBar powerBar;

    private static final int SLIDER_ZEROPOINT = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_activity_rcremote);

        powerBar = (ProgressBar) findViewById(R.id.progressBarBattery);


        seekbarDirection = (SeekbarHorizontal) findViewById(R.id.seekBarDirection);
        seekbarVelocity = (SeekbarVertical) findViewById(R.id.seekBarVelocity);
        seekbarDirection.setOnSeekBarChangeListener(directionListener);
        seekbarVelocity.setOnSeekBarChangeListener(velocityListener);

        seekbarDirection.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar));
        seekbarVelocity.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar));

        cmdManager.setCommandValue("controllerType", 3);
        cmdManager.setCommandValue("direction", 0);
        cmdManager.setCommandValue("velocity", 0);
        cmdManager.setCommandValue("shot", false);
    }


    final SeekbarHorizontal.OnSeekBarChangeListener directionListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            cmdManager.setCommandValue("direction", progress - SLIDER_ZEROPOINT);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    final SeekbarVertical.OnSeekBarChangeListener velocityListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            cmdManager.setCommandValue("velocity", progress - SLIDER_ZEROPOINT);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };



}
