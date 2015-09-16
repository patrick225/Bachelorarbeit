package com.patricklutz.ba.client;

import android.content.Context;

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

    private long lastShot;

    private Command command;
    private Websocket channel;
    private Context mainContext;


    public CommandManager(Context context) {
        mainContext = context;
        channel = Websocket.getInstance();

        command = new Command(0,0,false);
        lastShot = 0;
        new ShotDetector(context, this);

    }


    /**
     * Starts to send frequently commands to server
     */
    public void start() {
        timer = new Timer("commandTimer");
        timer.schedule(new DoFrequently(), 0, PERIOD);
    }


    /**
     * Stop sending commands to server
     */
    public void stop() {
        timer.cancel();
        channel.close();
    }


    public void setShot(boolean shot) {
        command.setShot(shot);
    }


    public void setVeloLeft(int velocity) {
        command.setVeloLeft(velocity);
    }


    public void setVeloRight(int velocity) {
        command.setVeloRight(velocity);
    }


    private class DoFrequently extends TimerTask {

        @Override
        public void run() {

            // want to shoot but last shot was recently
            if (command.isShot() &&
                    (System.currentTimeMillis() - lastShot) < SHOTDELAY)
                command.setShot(false);

            if (command.isShot())
                lastShot = System.currentTimeMillis();

            channel.send(command.getCommandJSON());
        }
    }

}
