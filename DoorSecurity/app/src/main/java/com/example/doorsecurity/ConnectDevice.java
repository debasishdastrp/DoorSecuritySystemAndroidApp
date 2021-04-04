package com.example.doorsecurity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ConnectDevice extends AppCompatActivity {

    Button disconnectBtn;
    TextView SerialMonitor;
    String address=null;
    private boolean p= false;
    private ProgressDialog pregress;
    BluetoothAdapter bt = null;
    BluetoothSocket btSocket = null;
    private boolean isBTconnected=false;
    private MediaPlayer mp = null;
    private double seq;
    NotificationCompat.Builder Nbuilder;
    NotificationManagerCompat notificationMgr;
    private boolean alertOn=false;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        address = i.getStringExtra(MainActivity.E_ADD);
        setContentView(R.layout.activity_connect_device);
        mp=MediaPlayer.create(this,R.raw.alarm);
        SerialMonitor = findViewById(R.id.serialText);
        new ConnectBT().execute();

        createNotificationChannel();
        BuildNotification();
    }

    public void startRead(View v)
    {

        seq=0;
        Log.d("test","Chjecking Connection");
            if(isBTconnected) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true) {
                            try {
                                final String s = readRawData(btSocket.getInputStream());
                                Log.i("info",s);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        SerialMonitor.setText(s.substring(0,s.length()-1));
                                    }
                                });
                                if(seq>=2)
                                {
                                if(Integer.valueOf(s.replaceAll("[^0-9]", ""))<=70)
                                {
                                    startAlert();
                                }}

                            } catch (IOException e) {
                                Log.e("Error", e.getMessage());
                            }
                            seq++;
                        }
                    }
                }).start();

            }

    }

    public void startAlert()
    {
        if(!alertOn)
        {
            notificationMgr.notify(56,Nbuilder.build());

            sound();
            alertOn = true;
        }
    }

    public void resetAlert(View v)
    {
        alertOn=false;
    }

    public void setDist(String s)
    {
        SerialMonitor.setText(s);
    }
    public void sound()
    {


        if(mp.isPlaying())
        {
            mp.pause();
            Log.e("Error", "Stopping Sound");
        }
        else
        {
            mp.start();
            Log.e("Error", "Playing Sound");
        }
    }

    public void playPause(View v)
    {
        sound();
    }

    public String readRawData(InputStream in) throws IOException
    {
        byte[] b=new byte[256];
        int bytes,i=0;
        StringBuilder res = new StringBuilder();
        DataInputStream ins = new DataInputStream(in);
        bytes = ins.read(b);
        String msg = new String(b,0,bytes);
        return msg.toString();
    }

    private void BuildNotification()
    {
        Nbuilder = new NotificationCompat.Builder(this,"doorSecurity")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Door Security by ClicksAndBits")
                .setContentText("Someone is trying to open / temper the main Door ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationMgr = NotificationManagerCompat.from(this);
    }




    public void  createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "alertChannel";
            String description = "Alert Channel for Security system Applications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("doorSecurity",name,importance);
            channel.setDescription(description);

            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }


    private class ConnectBT extends AsyncTask<Void,Void,Void>
    {
        private boolean BTconnected = true;

        @Override
        protected void onPreExecute()
        {
            pregress = ProgressDialog.show(ConnectDevice.this,"Connecting................", "Please Wait !");
        }


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                {
                    bt=BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice btDevice=bt.getRemoteDevice(address);
                    btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            }
            catch (IOException e) {
                BTconnected = false;
                Log.e("Error", e.getMessage().toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(BTconnected)
            {
                isBTconnected = true;
                Toast.makeText(ConnectDevice.this,"Device Connected Successfully",Toast.LENGTH_LONG).show();
                Log.i("info","Device Connected Successfully");
            }
            else
            {
                isBTconnected = false;
                Toast.makeText(ConnectDevice.this,"Connection Failed",Toast.LENGTH_LONG).show();
                Log.e("Error","Connection Failed");

            }
            pregress.dismiss();
        }
    }
}