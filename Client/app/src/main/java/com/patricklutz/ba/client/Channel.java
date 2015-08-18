package com.patricklutz.ba.client;

import android.os.Handler;
import android.os.Message;

/**
 * Abstract class to provide a Channel for Data-transmission
 *
 * Created by privat-patrick on 14.05.2015.
 */
public abstract class Channel extends Thread {

    public static final int STATE_CONNECTED = 1;
    public static final int STATE_DISCONNECTED = 2;

    public int TYPE;

    public static final int TYPE_WLAN = 12;
    public static final int TYPE_BLUETOOTH = 13;

    protected Handler connectionHandler;
    protected Handler stateHandler;
    protected int state;

    protected static Channel instance;


    protected void notifyHandler(int state) {
        Message msg = Message.obtain();
        msg.arg1 = state;
        connectionHandler.sendMessage(msg);
    }

    public static Channel getInstance() {
        return instance;
    }

    public void registerStateHandler(Handler handler) {
        this.stateHandler = handler;
    }

    public void registerConnectionHandler(Handler handler) {
        this.connectionHandler = handler;
    }

    public abstract void run();
    public abstract boolean open();
    public abstract void close();
    public abstract boolean send(byte[] data);

}
