package client.ba.patricklutz.com.androidapplication;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is the first Controlltype via Seekbars for left and right motor
 *
 * @author privat-patrick
 */
public class ControlActivityDifferential extends ControlActivity {


    SeekbarVertical leftSeekbar;
    SeekbarVertical rightSeekbar;

    ProgressBar powerBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_activity_differential);


        powerBar = (ProgressBar) findViewById(R.id.progressBarBattery);


        leftSeekbar = (SeekbarVertical) findViewById(R.id.seekBarLeft);
        rightSeekbar = (SeekbarVertical) findViewById(R.id.seekBarRight);
        leftSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        rightSeekbar.setOnSeekBarChangeListener(seekBarChangeListener);

        leftSeekbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar));
        rightSeekbar.setProgressDrawable(getResources().getDrawable(R.drawable.progress_bar));

        cmdManager.setCommandValue("controllerType", 2);
        cmdManager.setCommandValue("motorLeft", 0);
        cmdManager.setCommandValue("motorRight", 0);
        cmdManager.setCommandValue("shot", false);
    }


    final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        private static final int SLIDER_ZEROPOINT = 50;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            switch (seekBar.getId()) {
                case R.id.seekBarLeft:
                    cmdManager.setCommandValue("motorLeft", progress - SLIDER_ZEROPOINT);
                    break;
                case R.id.seekBarRight:
                    cmdManager.setCommandValue("motorRight", progress - SLIDER_ZEROPOINT);
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


}


