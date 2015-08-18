package com.patricklutz.ba.client;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Singleton Class for TCP Connection to the Server
 *
 * Serverport : 55055
 *
 * Created by privat-patrick on 12.05.2015.
 */
public class TCPClient extends Channel {

    private static String SERVERIP;
    private static final int SERVERPORT = 55055;
    private boolean running = false;

    private static final int PACKETSIZE = 3;



    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;

    public TCPClient(Handler handler) {
        this.connectionHandler = handler;
        state = STATE_DISCONNECTED;
        instance = this;
    }



    @Override
    public void run() {
        running = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            socket = new Socket(serverAddr, SERVERPORT);
            notifyHandler(STATE_CONNECTED);
            state = STATE_CONNECTED;

            try {

                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());

                byte[] buffer = new byte[PACKETSIZE];
                while(running && in.read(buffer) != -1) {

                    GameState state = new GameState();
                    state.setPower(buffer[0]);
                    state.setCountP1(buffer[1]);
                    state.setCountP2(buffer[2]);

                    Message msg = Message.obtain();
                    msg.obj = state;
                    stateHandler.sendMessage(msg);

                }

            } catch(IOException e) {
                notifyHandler(STATE_DISCONNECTED);
                state = STATE_DISCONNECTED;
                Log.e("TCPClient", e.getMessage());
            }
            socket.close();
        } catch (IOException e) {
            Log.e("TCPClient", e.getMessage());
            notifyHandler(STATE_DISCONNECTED);
        }
    }

    @Override
    public boolean send(byte[] data) {

        if (out != null && state == STATE_CONNECTED) {
            try {
                out.write(data);
                out.flush();
                return true;
            } catch (IOException e) {
                Log.e("TcpClient", e.getMessage());
                state = STATE_DISCONNECTED;
                notifyHandler(STATE_DISCONNECTED);

                return false;
            }
        }
        return false;
    }

    @Override
    public boolean open() {
        new GetServerIPTask().execute();
        return true;
    }

    @Override
    public void close() {
        running = false;
        try {
            if (socket != null) {
                socket.close();
                out.close();
                in.close();
            }
            state = STATE_DISCONNECTED;
        }catch (IOException e) {
            Log.e("TcpClient", e.getMessage());
        }

    }


    /**
     * Initialising Task to get ServerIP dynamic
     */
    class GetServerIPTask extends AsyncTask<Void, Void, String> {

        private DatagramSocket socket;
        private DatagramPacket packet;

        private final int TIMEOUT = 3000;
        private final int TRANSMIT_TRIES = 3;


        @Override
        protected String doInBackground(Void... params) {

            if (true)
                return "134.60.137.199";
            try {
                InetAddress host = InetAddress.getByName(getBroadcastIp());
                socket = new DatagramSocket(null);
                socket.setBroadcast(true);
                socket.setSoTimeout(TIMEOUT);

                packet = new DatagramPacket(new byte[0],0,host, SERVERPORT);


                int countFails = 0;
                boolean success = false;
                while(!success && countFails < TRANSMIT_TRIES) {
                    try {
                        socket.send(packet);
                        socket.receive(packet);
                        success = true;
                    } catch (IOException e) {
                        countFails++;
//                        Log.e("tcpclient", e.getMessage());
                    }
                }
                socket.close();
                if (!success) {
                    throw new IOException("Cant get IP from Server");
                }

                String result = packet.getAddress().toString();
                result = result.replace("/", "");

                return result;

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.i("tcpclient", "ServerIP: " + result);
                if (result.matches("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})"))
                    SERVERIP = result;
                else
                    Log.e("result", "received ip in wrong format");
                start();
            }

        }


        /**
         * Get BroadcastIp of corresponding Subnet
         *
         * @return String broadcastIp,
         *          Null on Error
         */
        private String getBroadcastIp() {
            String broadcastIp = "";
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = interfaces.nextElement();
                    if (networkInterface.isLoopback())
                        continue;    // Don't want to broadcast to the loopback interface
                    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                        InetAddress broadcast = interfaceAddress.getBroadcast();
                        if (broadcast == null)
                            continue;
                        broadcastIp = broadcast.toString().replace("/", "");
                        Log.i("tcpclient", broadcastIp);

                        return broadcastIp;
                    }

                }
            } catch (Exception e) {
                Log.e("tcpclient", "Error while getting BroadcastIp");
                Log.e("tcpclient", e.getMessage());
            }
            return null;

        }
    }
}
