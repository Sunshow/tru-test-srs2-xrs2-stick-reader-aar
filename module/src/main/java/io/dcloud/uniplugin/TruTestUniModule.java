package io.dcloud.uniplugin;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import net.sunshow.trutest.uniapp.TruTestClient;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

public class TruTestUniModule extends UniModule {

    private final String TAG = TruTestUniModule.class.getName();

    //run ui thread
    @UniJSMethod(uiThread = true)
    public void testAsyncFunc(JSONObject options, UniJSCallback callback) {
        Log.e(TAG, "testAsyncFunc--" + options);

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
}