package com.lsw.servicehook;

import android.app.Application;
import android.content.Context;

public class ServiceHookApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }
}
