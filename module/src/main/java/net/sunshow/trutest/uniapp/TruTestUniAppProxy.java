package net.sunshow.trutest.uniapp;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import io.dcloud.feature.uniapp.UniAppHookProxy;

public class TruTestUniAppProxy implements UniAppHookProxy {

    private final String TAG = TruTestUniAppProxy.class.getName();

    @SuppressLint("StaticFieldLeak")
    static Context context;

    @Override
    public void onSubProcessCreate(Application application) {

    }

    @Override
    public void onCreate(Application application) {
        Log.e(TAG, "onCreate");
        context = application.getApplicationContext();
    }
}
