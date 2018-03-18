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
    private final static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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

    // Method that returns Bluetooth Device with given mac. null if an error occurs
    private BluetoothDevice getBluetoothDevice(final String mac) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Device does not support Bluetooth");
            return null;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth is disabled");
            return null;
        }
        // Can also use the getRemoteDevice() function, but it always return an object for a valid
        // address, even if it has not previously been seen (IllegalArgumentException otherwise).
        // This is linear, but in general the number of paired devices is small.
        for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
            if (device.getAddress().equals(mac)) {
                return device;
            }
        }

        Log.e(TAG, "MAC address provided is not in paired list");
        return null;
    }

    // Method to close socket, and catch any errors
    private void closeSocket(BluetoothSocket socket) {
        if (socket != null) {
            try {
                socket.close();
                Log.i(TAG, "Closed socket successfully");
            } catch (IOException e) {
                Log.e(TAG, "Exception trying to close socket", e);
            }
        } else {
            // Do not log, we sometimes call this method when exceptions have
            // occured
            // Log.w(TAG, "Tried to close null socket");
        }
    }

    // Method to get a socket from the given device. The method first tries
    // a secure connection, then an insecure one, then a legacy one.
    private BluetoothSocket getConnectedSocket(BluetoothDevice device) {
        if (device == null) {
            Log.e(TAG, "Tried to connect socket of null device");
            return null;
        }

        BluetoothSocket socket = null;

        try {
            // Try to connect securely
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            Log.i(TAG, "Successfully connected securely");
        } catch (IOException e_sec) {
            Log.e(TAG, "Error creating secure socket", e_sec);
            closeSocket(socket);
            socket = null;
            try {
                // Try to connect with an insecure socket
                socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                Log.i(TAG, "Successfully connected insecurely");
            } catch (IOException e_insec) {
                Log.e(TAG, "Error creating insecure socket", e_insec);
                closeSocket(socket);
                socket = null;
                try {
                    // Try legacy mode
                    // Android has changed its behavior
                    // https://stackoverflow.com/questions/18657427
                    // so using workaround from
                    // https://stackoverflow.com/questions/25698585
                    socket = (BluetoothSocket) device.getClass().getMethod("createRfcommSocket",
                            new Class[]{int.class}).invoke(device, 1);
                    socket.connect();
                    Log.i(TAG, "Successfully connected in legacy mode");
                } catch (IOException e_leg) {
                    Log.e(TAG, "Error creating legacy socket", e_leg);
                    closeSocket(socket);
                    socket = null;
                } catch (Exception e_misc) {
                    Log.e(TAG, "Other socket error (legacy): ", e_misc);
                    closeSocket(socket);
                    socket = null;
                }
            }
        } catch (Exception e_overall) {
            Log.e(TAG, "Other socket error (overall): ", e_overall);
            closeSocket(socket);
            socket = null;
        }

        return socket;
    }

    // Method responsible for the connection and data transmission. Assumes bluetooth is enabled
    // and the device has been paired with.
    @Override
    protected void firePluginSetting(Context context, Bundle bundle) {
        final String mac = BundleManager.getMac(bundle);
        BluetoothDevice device = getBluetoothDevice(mac);
        if (device == null) {
            return;
        }

        BluetoothSocket socket = getConnectedSocket(device);

        if (socket == null) {
            return;
        }

        byte[] bytes = BundleManager.getMsgBytes(bundle);
        if (bytes != null) {
            try {
                OutputStream out = socket.getOutputStream();
                out.write(bytes);
                out.flush();
            } catch (IOException e) {
                Log.e(TAG, "Error writing to output stream", e);
            }

            Log.i(TAG, "Sent message successfully");
        } else {
            // this can happen, for instance, if string replacement of hex is incorrect
            Log.e(TAG, "Got null bytes, so did not send message");
        }

        closeSocket(socket);

    }
}
