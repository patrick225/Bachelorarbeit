package client.ba.patricklutz.com.androidapplication;

import android.content.Context;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class to manage outgoing Commands.
 *
 * Created by privat-patrick on 14.05.2015.
 */
public class CommandManager {


    private Timer timer = new Timer("commandTimer");

    /**
     * period to send commands to server (milliseconds)
     */
    private static final long PERIOD = 100;

    /**
     * minimum time, between two shots are sent
     */
    private static final long SHOTDELAY = 500;


    private JSONObject command;
    private Websocket channel;
    private Context mainContext;


    public CommandManager(Context context) {
        mainContext = context;
        channel = Websocket.getInstance();

        new ShotDetector(context, this);

        command = new JSONObject();

    }


    /**
     * Starts to send frequently commands to server
     */
    public void start() {
        timer = new Timer("commandTimer");
        timer.schedule(new DoFrequently(), 0, PERIOD);
    }

    public void setCommandValue(String key, boolean value) {
        try {
            if (value) {
                command.put(key, "true");
            } else {
                command.put(key, "false");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCommandValue(String key, int value) {
        try {
            command.put(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop sending commands to server
     */
    public void stop() {
        timer.cancel();
        channel.close();
    }


    private class DoFrequently extends TimerTask {

        @Override
        public void run() {


            channel.send(command.toString());

            try {
                if (command.getBoolean("shot")) {
                    command.put("shot", "false");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
