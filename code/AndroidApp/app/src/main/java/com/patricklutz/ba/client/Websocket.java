package com.patricklutz.ba.client;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by privat-patrick on 16.09.2015.
 */
public class Websocket extends WebSocketClient {

    static Websocket instance;

    static URI serverURI;
    static {
        try {
            serverURI = new URI("ws://192.168.178.28:8080/control/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    ConnectionListener connectionListener;

    private Websocket(ConnectionListener connectionListener) {
        super(serverURI, new Draft_17());
        this.connectionListener = connectionListener;

    }

    public static Websocket getInstance() {

        if (instance == null) {
            throw new NullPointerException("Websocket has no connectionListener");
        }

        return instance;
    }


    public static Websocket getInstance(ConnectionListener connectionListener) {

        if (instance == null)
            instance = new Websocket(connectionListener);
        else
            setConnectionListener(connectionListener);

        return instance;
    }

    public static void setConnectionListener(ConnectionListener connectionListener) {
        instance.connectionListener = connectionListener;
    }


    @Override
    public void onOpen(ServerHandshake handshakedata) {
        connectionListener.onConnectionEstablished();
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        connectionListener.onClose();
        instance = new Websocket(connectionListener);
    }

    @Override
    public void onError(Exception ex) {
        connectionListener.onClose();
        instance = new Websocket(connectionListener);
    }

    @Override
    public void send(String text) {

        try {
            super.send(text);
        } catch (WebsocketNotConnectedException e) {
            close();
            onError(e);
        }
    }
}
