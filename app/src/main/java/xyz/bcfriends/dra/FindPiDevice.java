package xyz.bcfriends.dra;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static android.content.Context.CLIPBOARD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class FindPiDevice extends AsyncTask<String, Integer, String> {
    private static final String tag = "SearchPi";

    private static final String query = "M-SEARCH * HTTP/1.1\r\n" + "HOST: 239.255.255.250:1900\r\n" + "MAN: \"ssdp:discover\"\r\n" + "MX: 1\r\n" +
            "ST: urn:schemas-upnp-org:device:MediaServer:1\r\n" +
            //"ST: ssdp:all\r\n" +
            "\r\n";

    private static final int port = 1900;
    Context context;

    public FindPiDevice(Context context) {
        super();
        this.context = context;
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

        response = new String(receivePacket.getData(), 0, receivePacket.getLength());
        String[] s = response.split("LOCATION: ");
        Log.d(tag, "Response contains: " + s[1]);


        URL url = null;
        try {
            url = new URL(s[1]);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            Log.d(tag, "Root element :" + doc.getDocumentElement().getNodeValue());


//            android.content.ClipboardManager clipboard =  (android.content.ClipboardManager) getSystemService(context, CLIPBOARD_SERVICE);
//            ClipData clip = ClipData.newPlainText("label", "Text to Copy");
//            clipboard.setPrimaryClip(clip);

        } catch (MalformedURLException | ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return s[1];
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
