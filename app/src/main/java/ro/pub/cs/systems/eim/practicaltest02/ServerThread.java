package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class ServerThread extends Thread {

    private ServerSocket serverSocket;

    private boolean isRunning = true;

    private int port;

    public ServerThread(int port) {
        this.port = port;
    }

    private class MyObject {
        String weather;
        String temp;
        private MyObject(String weather, String temp) {
            this.weather = weather;
            this.temp = temp;
        }
    }

    private HashMap<String, MyObject> map = new HashMap<>();

    @Override
    public void run() {
        isRunning = true;
        try {
            serverSocket = new ServerSocket(port);

            while (isRunning) {
                Socket socket = serverSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

                String given_valuta = reader.readLine();
                String q = "https://api.coindesk.com/v1/bpi/currentprice/" + given_valuta + ".json";

                Log.d("mine", q);

                HttpClient httpCl = new DefaultHttpClient();

                HttpGet getr = new HttpGet(q);

                ResponseHandler<String> respH = new BasicResponseHandler();
                String res = httpCl.execute(getr, respH);
                Log.d("mine", res);

                JSONObject result = new JSONObject(res);
                JSONObject aux = (JSONObject) result.get("bpi");
                JSONObject aux2 = (JSONObject) aux.get(given_valuta);
                String resf = aux2.getString("rate");
                Log.d("mine", resf);

                pw.println(resf);



                socket.close();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}