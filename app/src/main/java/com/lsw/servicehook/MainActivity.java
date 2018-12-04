package com.lsw.servicehook;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.lsw.pluginlibrary.IMyInterface;
import com.lsw.servicehook.ams_hook.AMSHookHelper;
import com.lsw.servicehook.classloder_hook.BaseDexClassLoaderHookHelper;

import java.io.File;



public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private static final String apkName = "plugin1.apk";

    private ServiceConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(
                        new ComponentName("com.lsw.plugin1",
                                "com.lsw.plugin1.MyService1"));

                startService(intent);
            }
        });

        findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.setComponent(
                        new ComponentName("com.lsw.plugin1",
                                "com.lsw.plugin1.MyService1"));
                stopService(intent);
            }
        });

        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("baobao", "onServiceConnected");
                IMyInterface a = (IMyInterface)service;
                int result = a.getCount();
                Log.e(TAG, String.valueOf(result));
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d("baobao", "onServiceDisconnected");
            }
        };

        findViewById(R.id.btnBind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent();
                intent.setComponent(
                        new ComponentName("com.lsw.plugin1",
                                "com.lsw.plugin1.MyService2"));
                bindService(intent, conn, Service.BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.btnUnbind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService(conn);
            }
        });

        findViewById(R.id.btnGetStatus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        try {
            Utils.extractAssets(newBase, apkName);

            File dexFile = getFileStreamPath(apkName);
            File optDexFile = getFileStreamPath("plugin1.dex");
            BaseDexClassLoaderHookHelper.patchClassLoader(getClassLoader(), dexFile, optDexFile);

            // 解析插件中的Service组件
            ServiceManager.getInstance().preLoadServices(dexFile);

            AMSHookHelper.hookAMN();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
