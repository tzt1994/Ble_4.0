package com.tzt.ble_40;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

public class BleUtils {

    public static boolean getBleDeviceGattConnect(Context context, BluetoothDevice device){
        boolean devecieStatus = false;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            int status = bluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
            if (status == BluetoothProfile.STATE_CONNECTED){
                devecieStatus = true;
            }else if (status == BluetoothProfile.STATE_DISCONNECTED){
                devecieStatus = false;
            }
        }
        return  devecieStatus;
    }
}
