package com.cicinnus.zoom;

import android.app.Application;

/**
 * App实例
 * author cicinnus
 * date 2018/6/1
 */
public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }
}
