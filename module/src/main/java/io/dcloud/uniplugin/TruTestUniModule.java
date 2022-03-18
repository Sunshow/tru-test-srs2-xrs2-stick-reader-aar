package io.dcloud.uniplugin;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.psp.bluetoothlibrary.BluetoothListener;
import com.psp.bluetoothlibrary.Connection;

import net.sunshow.trutest.client.TruTestClient;
import net.sunshow.trutest.client.TruTestCommand;
import net.sunshow.trutest.client.TruTestError;
import net.sunshow.trutest.client.TruTestEvent;
import net.sunshow.trutest.client.TruTestProtocol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

public class TruTestUniModule extends UniModule {

    private final String TAG = TruTestUniModule.class.getName();

    @UniJSMethod(uiThread = true)
    public void turnOnBluetooth() {
        Log.e(TAG, "turnOnBluetooth");

        TruTestClient.instance.turnOnBluetooth();
    }

    @UniJSMethod(uiThread = true)
    public void turnOffBluetooth() {
        Log.e(TAG, "turnOffBluetooth");

        TruTestClient.instance.turnOffBluetooth();
    }

    @UniJSMethod(uiThread = true)
    public void startDetectNearbyDevices() {
        Log.e(TAG, "startDetectNearbyDevices");
        TruTestClient.instance.startDetectNearbyDevices(new BluetoothListener.onDetectNearbyDeviceListener() {
            @Override
            public void onDeviceDetected(BluetoothDevice device) {
                // Device found
                Log.e(TAG, "Device found: " + device.getName());

                Map<String, Object> params = new HashMap<>();

                JSONObject data = new JSONObject();
                data.put("address", device.getAddress());
                data.put("name", device.getName());

                params.put("device", data);
                mUniSDKInstance.fireGlobalEventCallback(TruTestEvent.BlueToothDeviceDetected, params);
            }
        });
    }

    @UniJSMethod(uiThread = true)
    public void requestPairDevice(String deviceAddress, UniJSCallback callback) {
        Log.e(TAG, "requestPairDevice");
        if (TruTestClient.instance.requestPairDevice(deviceAddress, new BluetoothListener.onDevicePairListener() {
            @Override
            public void onDevicePaired(BluetoothDevice device) {
                // Paired successful
                Log.e(TAG, device.getName() + " Paired successful");

                Map<String, Object> params = new HashMap<>();

                params.put("paired", true);

                mUniSDKInstance.fireGlobalEventCallback(TruTestEvent.BlueToothDevicePairCompleted, params);
            }

            @Override
            public void onCancelled(BluetoothDevice device) {
                // Pairing failed
                Log.e(TAG, device.getName() + " Pairing failed");

                Map<String, Object> params = new HashMap<>();

                params.put("paired", false);

                mUniSDKInstance.fireGlobalEventCallback(TruTestEvent.BlueToothDevicePairCompleted, params);
            }
        })) {
            if (callback != null) {
                JSONObject data = new JSONObject();
                data.put("code", 0);
                callback.invoke(data);
            }
        } else {
            if (callback != null) {
                JSONObject data = new JSONObject();
                data.put("code", -1);
                callback.invoke(data);
            }
        }
    }

    @UniJSMethod(uiThread = true)
    public void unpairDevice(String deviceAddress, UniJSCallback callback) {
        Log.e(TAG, "unpairDevice");
        if (TruTestClient.instance.unpairDevice(deviceAddress)) {
            if (callback != null) {
                JSONObject data = new JSONObject();
                data.put("code", 0);
                callback.invoke(data);
            }
        } else {
            if (callback != null) {
                JSONObject data = new JSONObject();
                data.put("code", -1);
                callback.invoke(data);
            }
        }
    }

    @UniJSMethod(uiThread = true)
    public void listPairedDevices(UniJSCallback callback) {
        Log.e(TAG, "listPairedDevices");
        List<BluetoothDevice> deviceList = TruTestClient.instance.listPairedDevices();

        if (callback != null) {
            JSONArray array = new JSONArray();
            if (deviceList != null) {
                for (BluetoothDevice device : deviceList) {
                    JSONObject item = new JSONObject();
                    item.put("address", device.getAddress());
                    item.put("name", device.getName());

                    array.add(item);
                }
            }

            JSONObject data = new JSONObject();
            data.put("devices", array);
            callback.invoke(data);
        }
    }

    @UniJSMethod(uiThread = true)
    public void isConnected(UniJSCallback callback) {
        Log.e(TAG, "isConnected");
        if (callback != null) {
            JSONObject data = new JSONObject();
            data.put("connected", TruTestClient.instance.isConnected());
            callback.invoke(data);
        }
    }

    @UniJSMethod(uiThread = true)
    public void disconnect(UniJSCallback callback) {
        Log.e(TAG, "disconnect");
        TruTestClient.instance.disconnect();
        if (callback != null) {
            JSONObject data = new JSONObject();
            data.put("code", 0);
            callback.invoke(data);
        }
    }


    @UniJSMethod(uiThread = true)
    public void startConnection(JSONObject options, UniJSCallback callback) {
        Log.e(TAG, "startConnection: " + options);
        String deviceAddress = options.getString("address");
        if (TruTestClient.instance.startConnection(deviceAddress, new BluetoothListener.onConnectionListener() {
            @Override
            public void onConnectionStateChanged(BluetoothSocket socket, int state) {
                switch (state) {
                    case Connection.CONNECTING: {
                        Log.e(TAG, "Connecting...");

                        Map<String, Object> params = new HashMap<>();
                        mUniSDKInstance.fireGlobalEventCallback(TruTestEvent.DeviceConnecting, params);
                        break;
                    }
                    case Connection.CONNECTED: {
                        Log.e(TAG, "Connected");

                        Map<String, Object> params = new HashMap<>();
                        mUniSDKInstance.fireGlobalEventCallback(TruTestEvent.DeviceConnected, params);
                        break;
                    }
                    case Connection.DISCONNECTED: {
                        Log.e(TAG, "Disconnected");
                        // make sure call after detect bluetooth device disconnected
                        TruTestClient.instance.disconnect();

                        Map<String, Object> params = new HashMap<>();
                        mUniSDKInstance.fireGlobalEventCallback(TruTestEvent.DeviceDisconnected, params);
                        break;
                    }
                }
            }

            @Override
            public void onConnectionFailed(int errorCode) {
                switch (errorCode) {
                    case Connection.SOCKET_NOT_FOUND: {
                        Log.e(TAG, "Socket not found");
                        break;
                    }
                    case Connection.CONNECT_FAILED: {
                        Log.e(TAG, "Connect Failed");
                        break;
                    }
                }
                // make sure call after detect onConnectionFailed
                TruTestClient.instance.disconnect();

                Map<String, Object> params = new HashMap<>();
                mUniSDKInstance.fireGlobalEventCallback(TruTestEvent.DeviceConnectionFailed, params);
            }
        })) {
            if (callback != null) {
                JSONObject data = new JSONObject();
                data.put("code", 0);
                callback.invoke(data);
            }
        } else {
            if (callback != null) {
                JSONObject data = new JSONObject();
                data.put("code", -1);
                callback.invoke(data);
            }
        }
    }

    private final TruTestProtocol.onCommandCompletedListener<String> onCommandCompletedListener = new TruTestProtocol.onCommandCompletedListener<String>() {
        @Override
        public void onCompleted(TruTestCommand command, String data) {
            Map<String, Object> params = new HashMap<>();
            params.put("command", command.name());
            params.put("error", TruTestError.OK.getValue());
            mUniSDKInstance.fireGlobalEventCallback(TruTestEvent.CommandExecutionCompleted, params);
        }

        @Override
        public void onFailed(TruTestCommand command, TruTestError error) {
            Map<String, Object> params = new HashMap<>();
            params.put("command", command.name());
            params.put("error", error.getValue());
            mUniSDKInstance.fireGlobalEventCallback(TruTestEvent.CommandExecutionCompleted, params);
        }
    };

    @UniJSMethod(uiThread = true)
    public void requestClearAllSessionFiles(UniJSCallback callback) {
        Log.e(TAG, "requestClearAllSessionFiles");
        if (TruTestClient.instance.requestClearAllSessionFiles(onCommandCompletedListener)) {
            if (callback != null) {
                JSONObject data = new JSONObject();
                data.put("code", 0);
                callback.invoke(data);
            }
        } else {
            if (callback != null) {
                JSONObject data = new JSONObject();
                data.put("code", -1);
                callback.invoke(data);
            }
        }
    }

    @UniJSMethod(uiThread = true)
    public void requestResetCurrentSessionData(UniJSCallback callback) {
        Log.e(TAG, "requestResetCurrentSessionData");
        if (TruTestClient.instance.requestResetCurrentSessionData(onCommandCompletedListener)) {
            if (callback != null) {
                JSONObject data = new JSONObject();
                data.put("code", 0);
                callback.invoke(data);
            }
        } else {
            if (callback != null) {
                JSONObject data = new JSONObject();
                data.put("code", -1);
                callback.invoke(data);
            }
        }
    }

    @UniJSMethod(uiThread = true)
    public void requestDownloadCurrentSessionData(UniJSCallback callback) {
        Log.e(TAG, "requestDownloadCurrentSessionData");
        if (TruTestClient.instance.requestDownloadCurrentSessionData(new TruTestProtocol.onCommandCompletedListener<List<String>>() {
            @Override
            public void onCompleted(TruTestCommand command, List<String> data) {
                Map<String, Object> params = new HashMap<>();
                params.put("command", command.name());
                params.put("error", TruTestError.OK.getValue());
                params.put("data", data);
                mUniSDKInstance.fireGlobalEventCallback(TruTestEvent.CommandExecutionCompleted, params);
            }

            @Override
            public void onFailed(TruTestCommand command, TruTestError error) {
                Map<String, Object> params = new HashMap<>();
                params.put("command", command.name());
                params.put("error", error.getValue());
                mUniSDKInstance.fireGlobalEventCallback(TruTestEvent.CommandExecutionCompleted, params);
            }
        })) {
            if (callback != null) {
                JSONObject data = new JSONObject();
                data.put("code", 0);
                callback.invoke(data);
            }
        } else {
            if (callback != null) {
                JSONObject data = new JSONObject();
                data.put("code", -1);
                callback.invoke(data);
            }
        }
    }

    @UniJSMethod(uiThread = true)
    public void startRealTimeScanning(UniJSCallback callback) {
        Log.e(TAG, "startRealTimeScanning");
        TruTestClient.instance.startRealTimeScanning(new TruTestProtocol.onRealTimeDataScannedListener() {
            @Override
            public void onScanned(String data) {
                Map<String, Object> params = new HashMap<>();
                params.put("data", data);
                mUniSDKInstance.fireGlobalEventCallback(TruTestEvent.RealTimeDataScanned, params);
            }
        });
        if (callback != null) {
            JSONObject data = new JSONObject();
            data.put("code", 0);
            callback.invoke(data);
        }
    }

    @UniJSMethod(uiThread = true)
    public void stopRealTimeScanning(UniJSCallback callback) {
        Log.e(TAG, "stopRealTimeScanning");
        TruTestClient.instance.stopRealTimeScanning();
        if (callback != null) {
            JSONObject data = new JSONObject();
            data.put("code", 0);
            callback.invoke(data);
        }
    }
}