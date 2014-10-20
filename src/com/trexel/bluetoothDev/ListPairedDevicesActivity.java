package com.trexel.bluetoothDev;

import java.util.Set;
import android.util.Log;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListPairedDevicesActivity extends ListActivity {
    String TAG = "BTLog";
    private static final int SHOW_ITEM_DETAILS = 17;

    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayAdapter<String> btArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String deviceBTName = device.getName();
                String deviceBTMAC = device.getAddress();
                String deviceBTState = getBTBondState(device.getBondState());
                String deviceBTMajorClass = getBTMajorDeviceClass(device
                                                .getBluetoothClass()
                                                .getMajorDeviceClass());
                btArrayAdapter.add(deviceBTName + "\n"
                        + "Address: " + deviceBTMAC + "\n"
                        + "State: " + deviceBTState + "\n"
                        + "Class: " + deviceBTMajorClass);
            }
        }
        setListAdapter(btArrayAdapter);
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

    //when the user clicks on one of the items in the list
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        String selectedFromList =(l.getItemAtPosition(position).toString());
        Log.v("ListClick", "A list item was clicked\n" + selectedFromList);

        Intent intent = new Intent();
        intent.setClass(ListPairedDevicesActivity.this, singleListItemClicked.class);
        intent.putExtra("CLICKED_ITEM",selectedFromList);
        setResult(RESULT_OK, intent);
        startActivityForResult(intent, SHOW_ITEM_DETAILS);
        //finish();  //now handled in onActivityResult method
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == SHOW_ITEM_DETAILS){
            if(!bluetoothAdapter.isEnabled()) {
                finish();  //use to go all the way back to AndroidBluetooth Activity
            }
        }
    }
}
