package com.sample.client;

import android.app.Application;

import com.sample.client.tools.OrmLiteTool;


public class App extends Application {

    private static App sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        OrmLiteTool.getInstance(this).close();
    }

    public static App getApp() {
        return sApplication;
    }

}
