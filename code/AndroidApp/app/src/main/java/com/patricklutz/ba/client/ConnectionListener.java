package com.patricklutz.ba.client;

/**
 * Created by privat-patrick on 16.09.2015.
 */
public interface ConnectionListener {

    void onConnectionEstablished();
    void onClose();
}
