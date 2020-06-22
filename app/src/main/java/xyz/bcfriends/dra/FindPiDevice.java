package xyz.bcfriends.dra;

import android.content.ClipData;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.*;
import java.nio.charset.StandardCharsets;

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
        String result = "";
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
        try {
            String[] s = response.split("LOCATION: ");
            Log.d(tag, "Response contains: " + s[1]);
            result = s[1];
        } catch (Exception e) {
            e.printStackTrace(); //TODO
        }

        try {
            URL url = new URL(result);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }

            InputSource is = new InputSource(new StringReader(sb.toString()));

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            XPathExpression expr = xPath.compile("//item");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                result = node.getTextContent();
//                System.out.println("현재 노드 이름 : " + node.getNodeName());
//                System.out.println("현재 노드 타입 : " + node.getNodeType());
//                System.out.println("현재 노드 값 : " + node.getTextContent());
//                System.out.println("현재 노드 네임스페이스 : " + node.getPrefix());
//                System.out.println("현재 노드의 다음 노드 : " + node.getNextSibling());
            }

            Log.d(tag, "Result: " + result); //TODO

        } catch (ParserConfigurationException | XPathExpressionException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return result;
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
