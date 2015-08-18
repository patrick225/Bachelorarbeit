package com.patricklutz.ba.client;


/**
 * Modelclass for one single Command to the Server
 *
 *
 * Created by privat-patrick on 12.05.2015.
 */
public class Command {

    private static final int packagesize = 3;
    private int veloLeft;
    private int veloRight;



    private boolean shot;

    public Command(int veloLeft, int veloRight, boolean shot) {
        this.veloLeft = veloLeft;
        this.veloRight = veloRight;
        this.shot = shot;
    }

    public void setVeloLeft(int veloLeft) {
        this.veloLeft = veloLeft;
    }
    public void setVeloRight(int veloRight) {
        this.veloRight = veloRight;
    }
    public void setShot(boolean shot) {
        this.shot = shot;
    }

    public int getVeloRight() {
        return veloRight;
    }

    public boolean isShot() {
        return shot;
    }

    public int getVeloLeft() {

        return veloLeft;
    }

    public byte[] getCommandData() {

        byte[] data = new byte[packagesize];

        data[0] = (byte) veloLeft;
        data[1] = (byte) veloRight;

        if (shot)
            data[2] = 0x01;
        else
            data[2] = 0x00;

        return data;
    }
}
