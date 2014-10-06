package com.trexel.bluetoothDev;
import java.util.UUID;
import java.util.Set;
import java.io.IOException;

import android.util.Log;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListPairedDevicesActivity extends ListActivity {
    String TAG = "BTLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> btArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceBTName = device.getName();
                String deviceBTMAC = device.getAddress();
                String deviceConnection = getBTConnectionStatus(device);
                String deviceBTState = getBTBondState(device.getBondState());
                String deviceBTMajorClass = getBTMajorDeviceClass(device
                                                .getBluetoothClass()
                                                .getMajorDeviceClass());
                btArrayAdapter.add(deviceBTName + "\n"
                        + "Address: " + deviceBTMAC + "\n"
                        + "State: " + deviceBTState + "\n"
                        + "Connection: " + deviceConnection + "\n"
                        + "Class: " + deviceBTMajorClass);
            }
        }
        setListAdapter(btArrayAdapter);

    }

    //checks if the device is paired with the phone
    private String getBTBondState(int bt_bond_state){
        switch(bt_bond_state){
            case android.bluetooth.BluetoothDevice.BOND_NONE:
                return "Not Bonded";
            case android.bluetooth.BluetoothDevice.BOND_BONDING:
                return "Pairing Now";
            case android.bluetooth.BluetoothDevice.BOND_BONDED:
                return "Bonded";
            default: return "unknown!";
        }
    }


    //attempt to establish a connection via creating a bluetooth socket
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

}
