package net.sunshow.trutest.client;

import android.content.Context;

import com.psp.bluetoothlibrary.Bluetooth;
import com.psp.bluetoothlibrary.BluetoothListener;
import com.psp.bluetoothlibrary.Connection;

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

}