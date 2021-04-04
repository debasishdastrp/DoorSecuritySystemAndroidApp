package com.example.doorsecurity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button pairedDevbtn;
    ListView devList;
    private BluetoothAdapter bt = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String E_ADD="Device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pairedDevbtn = findViewById(R.id.button);
        devList = findViewById(R.id.devListView);

        bt=BluetoothAdapter.getDefaultAdapter();

        if(bt==null)
        {
            Toast.makeText(this,"Bluetooth Feature not available on this device",Toast.LENGTH_LONG);
            Log.e("err","Bluetooth Feature not available on this device");
            finish();
        }
        else if(!bt.isEnabled())
        {
            Intent BTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(BTon,1);
        }
    }

    public void listPairedDevices(View v)
    {
        pairedDevices = bt.getBondedDevices();
        ArrayList dList = new ArrayList();

        if(pairedDevices.size() > 0)
        {
            Log.d("err","Started....................");
            for(BluetoothDevice bt:pairedDevices)
            {
                dList.add(bt.getName() + "\n" + bt.getAddress());
                Log.d("err",bt.getName() + "\n" + bt.getAddress());
            }
        }
        else
        {
            Toast.makeText(this,"No Paired Device found",Toast.LENGTH_LONG);
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,dList);
        devList.setAdapter(adapter);
        devList.setOnItemClickListener(btDevSelect);

    }

    private AdapterView.OnItemClickListener btDevSelect = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String t = ((TextView) view).getText().toString();
            String btAddress= t.substring(t.length()-17);

            Intent i = new Intent(MainActivity.this,ConnectDevice.class);

            i.putExtra(E_ADD,btAddress);
            startActivity(i);
        }
    };
}