package com.patricklutz.ba.client;

import org.java_websocket.client.WebSocketClient;
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
        super(serverURI);
        this.connectionListener = connectionListener;

    }

    public static Websocket getInstance() {

        if (instance == null) {
            throw new NullPointerException("Websocket has no connectionListener");
        }

        return instance;
    }
    public static Websocket getInstance(ConnectionListener connectionListener) {

        if (instance == null) instance = new Websocket(connectionListener);
        return instance;
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
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

    }

    @Override
    public void onError(Exception ex) {

    }
}
