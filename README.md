# arduinotaskerbluetooth

This repository contains three parts:

1. [Schematics](schematics) for setting up a connection between an HC-05 Bluetooth module, an Attiny85, and some transistors, in order to control a 5V power output.
2. [Arduino code](arduino_bluetooth_switch) that setups a bluetooth connection, and waits for serial input that controls the transistor output.
3. [Android code](BluetoothSerialfromTasker) that implements a Tasker plugin which sends messages over Serial to a paired Bluetooth device.


The overall decisions and motivation are discussed in my [blog post](https://ilias.giechaskiel.com/posts/bluetooth_serial/index.html), but in case you might want to try it out for yourself, setup the Arduino example (make sure to change the name and paircode), and install the APK from [Google Play](https://play.google.com/store/apps/details?id=com.giechaskiel.ilias.bluetoothserialfromtasker). 

Please note that for the Android code, Bluetooth must already be switched on, and the device to communicate with must already be paired.
