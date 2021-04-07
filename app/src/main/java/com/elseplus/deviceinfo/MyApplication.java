package com.elseplus.deviceinfo;

import android.app.Application;
import android.content.Context;

import com.bun.miitmdid.core.JLibrary;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();JLibrary.InitEntry(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }
}

