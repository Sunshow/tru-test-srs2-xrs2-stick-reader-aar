package net.sunshow.trutest.uniapp;

import android.content.Context;

import com.psp.bluetoothlibrary.Bluetooth;
import com.psp.bluetoothlibrary.Connection;

public class TruTestClient {

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
    }

    public void turnOnBluetooth() {
        bluetooth.turnOnWithoutPermission();
    }

    public void turnOffBluetooth() {
        bluetooth.turnOff();
    }

}
