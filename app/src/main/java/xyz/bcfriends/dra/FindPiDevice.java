package xyz.bcfriends.dra;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.*;

public class FindPiDevice extends AsyncTask<String, Integer, String> {
    private static final String tag = "SearchPi";

    private static final String query = "M-SEARCH * HTTP/1.1\r\n" + "HOST: 239.255.255.250:1900\r\n" + "MAN: \"ssdp:discover\"\r\n" + "MX: 1\r\n" +
            "ST: urn:schemas-upnp-org:device:MediaServer:1\r\n" +
            //"ST: ssdp:all\r\n" +
            "\r\n";

    private static final int port = 1900;

    public FindPiDevice() {
        super();
    }

    @Override
    protected String doInBackground(String... strings) {
        String response = "";
        byte[] sendData;
        byte[] receiveData = new byte[1024];
        sendData = query.getBytes();
        DatagramPacket sendPacket = null;

        try {
            sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("239.255.255.250"), port);
        } catch (UnknownHostException e) {
            Log.d(tag, "Unknown Host Exception Thrown after creating DatagramPacket to send to server");
            e.printStackTrace();
        }

        DatagramSocket clientSocket = null;
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            Log.d(tag, "Socket Exception thrown when creating socket to transport data");
            e.printStackTrace();
        }

        try {
            if (clientSocket != null) {
                clientSocket.setSoTimeout(1000);
                clientSocket.send(sendPacket);
            }
        } catch (IOException e) {
            Log.d(tag, "IOException thrown when sending data to socket");
            e.printStackTrace();
        }

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            if (clientSocket != null) {
                clientSocket.receive(receivePacket);
            }
        } catch (IOException e) {
            Log.d(tag, "IOException thrown when receiving data");
            e.printStackTrace();
        }

        for (int i = 0; i < 10; i++) {
            response = new String(receivePacket.getData());
            Log.d(tag, "Response contains: " + response);
            if (response.contains("/jambon-3000.xml")) {
                Log.d("logCat", receivePacket.getAddress().toString());
                return response;
            }
            else {
                Log.d("logCat", "Popz! Pi not found");
            }
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
