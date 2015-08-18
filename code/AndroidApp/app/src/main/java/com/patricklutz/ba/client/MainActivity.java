package com.patricklutz.ba.client;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * This is the first Controlltype via Seekbars for left and right motor
 *
 * @author privat-patrick
 */
public class MainActivity extends Activity {


    CommandManager cmdManager;

    SeekbarVertical leftSeekbar;
    SeekbarVertical rightSeekbar;

    ProgressBar powerBar;

    TextView countP1;
    TextView countP2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imgView = (ImageView) findViewById(R.id.imageview);

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        imgView.setImageBitmap(BitmapFactory.decodeFile(dir.getAbsolutePath() + "/27918-affe-zeigt-stinkefinger.jpg"));


        cmdManager = new CommandManager(this);
        Channel.getInstance().registerStateHandler(stateHandler);
        Channel.getInstance().registerConnectionHandler(connectionHandler);


        powerBar = (ProgressBar) findViewById(R.id.progressBarBattery);
        countP1 = (TextView) findViewById(R.id.countP1Text);
        countP2 = (TextView) findViewById(R.id.countP2Text);

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


    Handler stateHandler = new Handler() {
        public void handleMessage(Message msg) {

            GameState state = (GameState) msg.obj;
            powerBar.setProgress(state.getPower());
            countP1.setText(String.valueOf(state.getCountP1()));
            countP2.setText(String.valueOf(state.getCountP2()));
        }
    };

    Handler connectionHandler = new Handler() {
      public void handleMessage(Message msg) {
          String message;
          switch (msg.arg1) {
              case Channel.STATE_DISCONNECTED:
                  message = "Connection refused!";
                  Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                  finish();
                  break;
          }
      }
    };

}


