package client.ba.patricklutz.com.androidapplication;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.SeekBar;

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
    }




    final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        private static final int SLIDER_ZEROPOINT = 50;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            switch (seekBar.getId()) {
                case R.id.seekBarLeft:
                    cmdManager.setVeloLeft(progress - SLIDER_ZEROPOINT);
                    break;
                case R.id.seekBarRight:
                    cmdManager.setVeloRight(progress - SLIDER_ZEROPOINT);
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




//    Handler stateHandler = new Handler() {
//        public void handleMessage(Message msg) {
//
//            GameState state = (GameState) msg.obj;
//            powerBar.setProgress(state.getPower());
//            countP1.setText(String.valueOf(state.getCountP1()));
//            countP2.setText(String.valueOf(state.getCountP2()));
//        }
//    };
//
//    Handler connectionHandler = new Handler() {
//      public void handleMessage(Message msg) {
//          String message;
//          switch (msg.arg1) {
//              case Channel.STATE_DISCONNECTED:
//                  message = "Connection refused!";
//                  Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                  finish();
//                  break;
//          }
//      }
//    };

}


