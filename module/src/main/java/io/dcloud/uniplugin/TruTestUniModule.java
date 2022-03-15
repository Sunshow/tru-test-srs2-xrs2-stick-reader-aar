package io.dcloud.uniplugin;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.psp.bluetoothlibrary.BluetoothListener;

import net.sunshow.trutest.client.TruTestClient;
import net.sunshow.trutest.client.TruTestEvent;

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
}