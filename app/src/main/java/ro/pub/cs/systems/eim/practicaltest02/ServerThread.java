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
        isRunning = true;
        this.port = port;
    }

    private String updateTime = "";
    private String eurVal = "";
    private String usdVal = "";

    private class UpdateThread extends Thread {
        @Override
        public void run() {
            while (isRunning) {
                String given_valuta = "EUR";
                String q = "https://api.coindesk.com/v1/bpi/currentprice/" + given_valuta + ".json";

                Log.d("mine", q);

                HttpClient httpCl = new DefaultHttpClient();

                HttpGet getr = new HttpGet(q);

                ResponseHandler<String> respH = new BasicResponseHandler();
                String res = null;
                try {
                    res = httpCl.execute(getr, respH);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("mine", res);

                JSONObject result = null;
                try {
                    result = new JSONObject(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject aux = null;
                try {
                    aux = (JSONObject) result.get("bpi");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject aux2 = null;
                try {
                    aux2 = (JSONObject) aux.get(given_valuta);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String resf = null;
                try {
                    resf = aux2.getString("rate");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("mine", resf);
                eurVal = resf;

                // Get dollars
                given_valuta = "USD";
                q = "https://api.coindesk.com/v1/bpi/currentprice/" + given_valuta + ".json";

                Log.d("mine", q);

                httpCl = new DefaultHttpClient();

                getr = new HttpGet(q);

                respH = new BasicResponseHandler();
                res = null;
                try {
                    res = httpCl.execute(getr, respH);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                result = null;
                try {
                    result = new JSONObject(res);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                aux = null;
                try {
                    aux = (JSONObject) result.get("bpi");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                aux2 = null;
                try {
                    aux2 = (JSONObject) aux.get(given_valuta);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                resf = null;
                try {
                    resf = aux2.getString("rate");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                usdVal = resf;

                // Get time
                aux = null;
                try {
                    aux = (JSONObject) result.get("time");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String tm = "";
                try {
                    tm = aux.getString("updated");
                    updateTime = tm;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("mine", updateTime);
                Log.d("mine", eurVal);
                Log.d("mine", usdVal);

                Log.d("mine", "waiting");

                try {
                    this.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        isRunning = true;
        try {
            new UpdateThread().start();
            serverSocket = new ServerSocket(port);

            while (isRunning) {
                Socket socket = serverSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                String given_valuta = reader.readLine();
                if (updateTime.length() > 0) {
                    if (given_valuta.equals("EUR"))
                        pw.println(eurVal);
                    else
                        pw.println(usdVal);
                } else {

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
                }

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