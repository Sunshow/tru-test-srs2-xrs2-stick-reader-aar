package net.sunshow.trutest.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import com.psp.bluetoothlibrary.Bluetooth;
import com.psp.bluetoothlibrary.BluetoothListener;
import com.psp.bluetoothlibrary.Connection;

import java.util.ArrayList;
import java.util.List;

public class TruTestClient {

    private final String TAG = TruTestClient.class.getName();

    private Bluetooth bluetooth;

    private Connection connection;

    private TruTestProtocol protocol;

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

    public boolean isConnected() {
        return connection.isConnected();
    }

    public boolean startConnection(String deviceAddress, final BluetoothListener.onConnectionListener listener) {
        final TruTestProtocol protocol = new TruTestProtocol(connection);
        if (connection.connect(deviceAddress, false, new BluetoothListener.onConnectionListener() {
            @Override
            public void onConnectionStateChanged(final BluetoothSocket socket, final int state) {
                if (state == Connection.CONNECTED) {
                    if (!protocol.setAcknowledgeOn(new TruTestProtocol.onCommandCompletedListener() {
                        @Override
                        public void onCompleted(TruTestCommand command, Object data) {
                            listener.onConnectionStateChanged(socket, state);
                        }

                        @Override
                        public void onFailed(TruTestCommand command, TruTestError error) {
                            disconnect();
                            listener.onConnectionStateChanged(socket, Connection.DISCONNECTED);
                        }
                    })) {
                        disconnect();
                        listener.onConnectionStateChanged(socket, Connection.DISCONNECTED);
                    }
                    return;
                }
                listener.onConnectionStateChanged(socket, state);
            }

            @Override
            public void onConnectionFailed(int errorCode) {
                listener.onConnectionFailed(errorCode);
            }
        }, protocol.receiveListener)) {
            this.protocol = protocol;
            return true;
        }
        return false;
    }

    public void disconnect() {
        if (connection.isConnected()) {
            connection.disconnect();
        }
        protocol = null;
    }

    public boolean requestClearAllSessionFiles(TruTestProtocol.onCommandCompletedListener<String> listener) {
        return protocol.clearAllSessionFiles(listener);
    }

    public boolean requestResetCurrentSessionData(TruTestProtocol.onCommandCompletedListener<String> listener) {
        return protocol.resetCurrentSessionData(listener);
    }

    public boolean requestDownloadCurrentSessionData(final TruTestProtocol.onCommandCompletedListener<List<String>> listener) {
        return protocol.downloadCurrentSessionData(new TruTestProtocol.onCommandCompletedListener<String>() {
            @Override
            public void onCompleted(TruTestCommand command, String data) {
                List<String> result = new ArrayList<>();
                String[] lines = data.split("\\]\\[");
                for (String line : lines) {
                    // [0,991 005002562568,,16/03/2022,12:22:28]
                    String[] fields = line.split(",");
                    result.add(fields[1].replaceAll(" ", ""));
                }
                listener.onCompleted(command, result);
            }

            @Override
            public void onFailed(TruTestCommand command, TruTestError error) {
                listener.onFailed(command, error);
            }
        });
    }
}
