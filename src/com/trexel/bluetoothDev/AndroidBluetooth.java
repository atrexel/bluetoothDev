package com.trexel.bluetoothDev;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class AndroidBluetooth extends Activity {

    String selected_item_address;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PAIRED_DEVICE = 2;

    /** Called when the activity is first created. */
    Button OnButton;
    Button OffButton;
    Button VisibleButton;
    Button btnListPairedDevices;
    TextView stateBluetooth;
    BluetoothAdapter bluetoothAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        OnButton = (Button)findViewById(R.id.bluetoothOnButton);
        OnButton.setOnClickListener(btOnButtonOnClickListener);
        OffButton = (Button)findViewById(R.id.bluetoothOffButton);
        OffButton.setOnClickListener(btOffButtonOnClickListener);
        VisibleButton = (Button)findViewById(R.id.bluetoothVisibleButton);
        VisibleButton.setOnClickListener(btVisibleButtonOnClickListener);
        btnListPairedDevices = (Button)findViewById(R.id.listpaireddevices);
        btnListPairedDevices.setOnClickListener(btnListPairedDevicesOnClickListener);

        stateBluetooth = (TextView)findViewById(R.id.bluetoothstate);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        CheckBlueToothState();
    }

    private void CheckBlueToothState(){
        if (bluetoothAdapter == null){
            stateBluetooth.setText("Bluetooth NOT support");
        }else{
            if (bluetoothAdapter.isEnabled()){
                if(bluetoothAdapter.isDiscovering()){
                    stateBluetooth.setText("Bluetooth is currently in device discovery process.");
                }else{
                    stateBluetooth.setText("Bluetooth is Enabled.");
                    btnListPairedDevices.setEnabled(true);
                }
            } else {
                stateBluetooth.setText("Bluetooth is NOT Enabled!");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }


    private Button.OnClickListener btnListPairedDevicesOnClickListener
            = new Button.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            Intent intent = new Intent();
            intent.setClass(AndroidBluetooth.this, ListPairedDevicesActivity.class);
            Toast.makeText(getApplicationContext(),"Select a Device for Details\n"+
                    "or to test a connection.",
                    Toast.LENGTH_SHORT).show();
            startActivityForResult(intent, REQUEST_PAIRED_DEVICE);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_ENABLE_BT){
            CheckBlueToothState();
        }if (requestCode == REQUEST_PAIRED_DEVICE){
            if(resultCode == RESULT_OK){

            }
        }
    }

    private Button.OnClickListener btOnButtonOnClickListener = new Button.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, 0);
                Toast.makeText(getApplicationContext(),"Bluetooth Enabled"
                        ,Toast.LENGTH_SHORT).show();
                CheckBlueToothState();
            }
            else{
                Toast.makeText(getApplicationContext(),"Bluetooth already Enabled",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
    private Button.OnClickListener btOffButtonOnClickListener = new Button.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            //turning bluetooth off this way requires BLUETOOTH_ADMIN permissions set in the AndroidManifest
            bluetoothAdapter.disable();
            Toast.makeText(getApplicationContext(),"Bluetooth Disabled" ,
                    Toast.LENGTH_SHORT).show();
            CheckBlueToothState();
        }
    };
    private Button.OnClickListener btVisibleButtonOnClickListener = new Button.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(getVisible, 0);
        }
    };
}