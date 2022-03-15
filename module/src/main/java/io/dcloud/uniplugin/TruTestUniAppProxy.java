package io.dcloud.uniplugin;

import android.app.Application;
import android.util.Log;

import net.sunshow.trutest.uniapp.TruTestClient;

import io.dcloud.feature.uniapp.UniAppHookProxy;

public class TruTestUniAppProxy implements UniAppHookProxy {

    private final String TAG = TruTestUniAppProxy.class.getName();

    @Override
    public void onSubProcessCreate(Application application) {

    }

    @Override
    public void onCreate(Application application) {
        Log.e(TAG, "onCreate");
        TruTestClient.init(application.getApplicationContext());
    }
}
