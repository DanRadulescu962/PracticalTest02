package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PracticalTest02MainActivity extends AppCompatActivity {

    EditText serverPort;
    EditText valuta;
    EditText result;
    Button startServer;
    Button send;

    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            new ClientTask().execute("localhost", serverPort.getText().toString(), valuta.getText().toString());
        }
    }

    private class MyClickListener2 implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int mport = Integer.parseInt(serverPort.getText().toString());
            ServerThread serverThread = new ServerThread(mport);
            serverThread.start();
        }
    }

    private class ClientTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                int port = Integer.parseInt(strings[1]);
                Socket socket = new Socket(strings[0], port);

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                Log.d("mine", "Sending");
                pw.println(strings[2]);

                String res = reader.readLine();

                //Log.d("mine", res);

                publishProgress(res);

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //super.onProgressUpdate(values);
            result.setText(values[0]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPort = findViewById(R.id.server_port_text);
        valuta = findViewById(R.id.address_text);
        result = findViewById(R.id.result_text);

        startServer = findViewById(R.id.startButton);
        send = findViewById(R.id.sendButton);

        startServer.setOnClickListener(new MyClickListener2());
        send.setOnClickListener(new MyClickListener());
    }
}
