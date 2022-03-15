package io.dcloud.uniplugin;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.psp.bluetoothlibrary.BluetoothListener;

import net.sunshow.trutest.client.TruTestClient;
import net.sunshow.trutest.client.TruTestEvent;

import java.util.HashMap;
import java.util.Map;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

public class TruTestUniModule extends UniModule {

    private final String TAG = TruTestUniModule.class.getName();

    //run ui thread
    @UniJSMethod(uiThread = true)
    public void testAsyncFunc(JSONObject options, UniJSCallback callback) {
        Log.e(TAG, "testAsyncFunc--" + options);

        startDetectNearbyDevices();

        if (callback != null) {
            JSONObject data = new JSONObject();
            data.put("code", "success");
            callback.invoke(data);
            //callback.invokeAndKeepAlive(data);
        }
    }

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
}