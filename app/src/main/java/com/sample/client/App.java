package com.sample.client;

import android.app.Application;

import com.sample.client.tools.OrmLiteTool;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OrmLiteTool.getInstance(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        OrmLiteTool.getInstance(this).close();
    }
}
