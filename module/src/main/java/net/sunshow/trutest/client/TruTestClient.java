package net.sunshow.trutest.client;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.psp.bluetoothlibrary.Bluetooth;
import com.psp.bluetoothlibrary.BluetoothListener;
import com.psp.bluetoothlibrary.Connection;

import java.util.List;

public class TruTestClient {

    private final String TAG = TruTestClient.class.getName();

    private Bluetooth bluetooth;

    private Connection connection;

    public static TruTestClient instance = new TruTestClient();

    private TruTestClient() {

    }

    public synchronized static void init(Context context) {
        if (instance.bluetooth == null) {
            instance.bluetooth = new Bluetooth(context);
            instance.connection = new Connection(context);
        }
        instance.turnOnBluetooth();
    }

    public void turnOnBluetooth() {
        bluetooth.turnOnWithoutPermission();
    }

    public void turnOffBluetooth() {
        bluetooth.turnOff();
    }

    public void setOnDetectNearbyDeviceListener(BluetoothListener.onDetectNearbyDeviceListener listener) {
        bluetooth.setOnDetectNearbyDeviceListener(listener);
    }

    public void startDetectNearbyDevices() {
        bluetooth.startDetectNearbyDevices();
    }

    public void startDetectNearbyDevices(BluetoothListener.onDetectNearbyDeviceListener listener) {
        bluetooth.setOnDetectNearbyDeviceListener(listener);
        bluetooth.startDetectNearbyDevices();
    }

    public boolean requestPairDevice(String deviceAddress, BluetoothListener.onDevicePairListener listener) {
        bluetooth.setOnDevicePairListener(listener);
        return bluetooth.requestPairDevice(deviceAddress);
    }

    public boolean unpairDevice(String deviceAddress) {
        return bluetooth.unpairDevice(deviceAddress);
    }

    public List<BluetoothDevice> listPairedDevices() {
        return bluetooth.getPairedDevices();
    }
}
