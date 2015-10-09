package client.ba.patricklutz.com.androidapplication;


import org.json.JSONException;
import org.json.JSONObject;

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
    private int controllertype = 2;


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
    public void setControllertype(int controllertype) {this.controllertype = controllertype;}

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

    public String getCommandJSON() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("controllerType", controllertype);

            jsonObject.put("motorLeft", veloLeft);
            jsonObject.put("motorRight", veloRight);
            jsonObject.put("shot", shot);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result = jsonObject.toString();

        return result;
    }
}
