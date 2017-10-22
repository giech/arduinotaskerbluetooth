/*
 * Author: Ilias Giechaskiel
 * Website: https://ilias.giechaskiel.com
 * Description: Class responsible for connecting to paired device and sending the message
 *
 */

package com.giechaskiel.ilias.bluetoothserialfromtasker;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.twofortyfouram.locale.sdk.client.receiver.AbstractPluginSettingReceiver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class SettingReceiver extends AbstractPluginSettingReceiver {
    // Tag for logging
    private final static String TAG = "SettingReceiver";

    // UUID necessary for creating socket
    private final static UUID MY_UUID = UUID.fromString("ba183b32-d29a-4436-a8bc-d77354c87bf5");

    // Method that checks whether bundle is valid
    @Override
    protected boolean isBundleValid(Bundle bundle) {
        return BundleManager.isBundleValid(bundle);
    }

    // Method that suggests whether this task should be handled in a background thread
    @Override
    protected boolean isAsync() {
        return true;
    }

    // Method responsible for the connection and data transmission. Assumes bluetooth is enabled
    // and the device has been paired with.
    @Override
    protected void firePluginSetting(Context context, Bundle bundle) {
        final String mac = BundleManager.getMac(bundle);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Device does not support Bluetooth");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth is disabled");
            return;
        }

        // Can also use the getRemoteDevice() function, but it always return an object for a valid
        // address, even if it has not previously been seen (IllegalArgumentException otherwise).
        // This is linear, but in general the number of paired devices is small.
        for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
            if (device.getAddress().equals(mac)) {
                BluetoothSocket socket = null;

                try {
                    socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    try {
                        socket.connect();
                    } catch (IOException e) {
                        // Android has changed its behavior
                        // https://stackoverflow.com/questions/18657427
                        // so using workaround from
                        // https://stackoverflow.com/questions/25698585
                        socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket",
                                new Class[]{int.class}).invoke(device, 1);
                        socket.connect();
                    }

                    byte[] bytes = BundleManager.getMsgBytes(bundle);
                    if (bytes != null) {
                        OutputStream out = socket.getOutputStream();
                        out.write(bytes);
                        out.flush();
                        Log.i(TAG, "Sent message successfully");
                    } else {
                        // this can happen, for instance, if string replacement of hex is incorrect
                        Log.e(TAG, "Got null bytes, so did not send message");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception while connecting and communicating with device", e);
                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            Log.e(TAG, "Exception trying to close socket", e);
                        }
                    }
                }
                return;
            }
        }
        Log.e(TAG, "MAC address provided is not in paired list");
    }
}
