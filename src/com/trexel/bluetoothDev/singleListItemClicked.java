package com.trexel.bluetoothDev;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class singleListItemClicked extends Activity {
    static String TAG = "BTDetails";

    //android view class variables
    Button connectButton;
    Button disconnectButton;
    Button doneButton;
    TextView deviceNameTextView;
    TextView deviceBondStateTextView;
    TextView deviceClassTextView;
    TextView deviceAddressTextView;
    TextView deviceUUIDTextView;
    TextView deviceConnectionTextView;

    //bluetooth class variables
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice selectedDevice;
    BluetoothSocket BTSocket;

    //class variables
    String selectedDeviceText = "";
    String deviceAddress = "";
    String deviceUUIDs = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.single_item_details);

        //link the view class variables to the actual view objects
        connectButton = (Button) findViewById(R.id.connectButton);
        disconnectButton = (Button) findViewById(R.id.disconnectButton);
        doneButton = (Button) findViewById(R.id.doneButton);
        deviceNameTextView = (TextView) findViewById(R.id.deviceName);
        deviceBondStateTextView = (TextView) findViewById(R.id.deviceBondState);
        deviceClassTextView = (TextView) findViewById(R.id.deviceClass);
        deviceAddressTextView = (TextView) findViewById(R.id.deviceAddress);
        deviceUUIDTextView = (TextView) findViewById(R.id.deviceUUID);
        deviceConnectionTextView = (TextView) findViewById(R.id.deviceConnection);

        //instantiate the class bluetooth variables
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //grab the appropriate device based on the address from
        //previous activity as passed through the result
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedDeviceText = extras.getString("CLICKED_ITEM");

            //parses the returned string for the MAC address
            Pattern p = Pattern.compile("((([a-f]|[A-F]|[0-9]){2}:){5}([a-f]|[A-F]|[0-9]){2})");
            Matcher m = p.matcher(selectedDeviceText);
            if(m.find()) {
                int start = m.start();
                int end = m.end();
                deviceAddress = selectedDeviceText.substring(start, end);
                Log.v(TAG, "Match: " + deviceAddress);
            }else{
                Log.v(TAG, "Error getting address for device...");
                Toast.makeText(getApplicationContext(),"Error Finding Device.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        //find device from previous address
        if(deviceAddress.length() > 0) {
            selectedDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
        }

        if(selectedDevice != null){
            deviceNameTextView.setText(selectedDevice.getName());
            deviceBondStateTextView.setText("Bond State: " +
                    getBTBondState(selectedDevice.getBondState()));
            deviceClassTextView.setText("Class: " + getBTMajorDeviceClass(selectedDevice
                    .getBluetoothClass()
                    .getMajorDeviceClass()));
            deviceAddressTextView.setText("Address: " + selectedDevice.getAddress());
            ParcelUuid uuids[] = selectedDevice.getUuids(); //selectedDevice.getUuids()[0].toString()
            for(ParcelUuid uuid : uuids){
                deviceUUIDs = deviceUUIDs + "    " + uuid.getUuid().toString() + "\n";
            }
            deviceUUIDTextView.setText("UUIDs:\n" + deviceUUIDs);
            deviceConnectionTextView.setText("Connection State: ");
        }

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedDevice != null) {
                    Intent intent = new Intent();
                    //intent.putExtra("DEVICE_ADDRESS", selectedDevice.getAddress());
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onNavigateUpFromChild(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //checks if the device is paired with the phone
    private String getBTBondState(int bt_bond_state) {
        switch (bt_bond_state) {
            case android.bluetooth.BluetoothDevice.BOND_NONE:
                return "Not Bonded";
            case android.bluetooth.BluetoothDevice.BOND_BONDING:
                return "Pairing Now";
            case android.bluetooth.BluetoothDevice.BOND_BONDED:
                return "Bonded";
            default:
                return "unknown!";
        }
    }

    //checks for the type of the device
    private String getBTMajorDeviceClass(int major){
        switch(major){
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                return "AUDIO_VIDEO";
            case BluetoothClass.Device.Major.COMPUTER:
                return "COMPUTER";
            case BluetoothClass.Device.Major.HEALTH:
                return "HEALTH";
            case BluetoothClass.Device.Major.IMAGING:
                return "IMAGING";
            case BluetoothClass.Device.Major.MISC:
                return "MISC";
            case BluetoothClass.Device.Major.NETWORKING:
                return "NETWORKING";
            case BluetoothClass.Device.Major.PERIPHERAL:
                return "PERIPHERAL";
            case BluetoothClass.Device.Major.PHONE:
                return "PHONE";
            case BluetoothClass.Device.Major.TOY:
                return "TOY";
            case BluetoothClass.Device.Major.UNCATEGORIZED:
                return "UNCATEGORIZED";
            case BluetoothClass.Device.Major.WEARABLE:
                return "WEARABLE_AUDIO_VIDEO";
            default: return "unknown!";
        }
    }



    //synchronouly / blocks main thread as this function attempts
    //to establish a connection via creating a bluetooth socket
    private String getBTConnectionStatus(BluetoothDevice device){
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmpSocket = null;

        // Default UUID
        UUID DEFAULT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // Use the UUID of the device that discovered // TODO Maybe need extra device object
            if (device != null)
            {
                Log.i(TAG, "Device UUID from fetch method: " + device.fetchUuidsWithSdp());
                Log.i(TAG, "Device Name: " + device.getName());
                Log.i(TAG, "Device UUID: " + device.getUuids()[0].getUuid());
                tmpSocket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());

                try {
                    tmpSocket.connect();
                    if(tmpSocket.isConnected()){
                        tmpSocket.close();
                        return "Could Connect";
                    }
                } catch (Exception e){
                    Log.i(TAG, "Failed to make connection with Device UUID");
                    return "Failed connection";
                }
            }
            else Log.d(TAG, "Device is null.");
        }
        catch (NullPointerException e1)
        {
            Log.d(TAG, " UUID from device is null, Using Default UUID, Device name: " + device.getName());
            try {
                tmpSocket = device.createRfcommSocketToServiceRecord(DEFAULT_UUID);
                try {
                    tmpSocket.connect();
                    if(tmpSocket.isConnected()){
                        tmpSocket.close();
                        return "Could Connect with Default";
                    }
                } catch (Exception e2){
                    Log.i(TAG, "Failed to make connection with Default UUID");
                }
            } catch (IOException e3) {
                Log.e(TAG, "IOException thrown [" + e3 + "]");
                //e1.printStackTrace();
                return "Failed connection";
            }
        }
        catch (IOException e4) { return "Failed connection"; }

        return "Failed connection";
    }

}
