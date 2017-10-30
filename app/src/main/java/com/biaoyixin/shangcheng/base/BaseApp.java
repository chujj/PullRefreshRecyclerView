package com.biaoyixin.shangcheng.base;

import android.app.Application;

/**
 * Created by zhujj on 17-10-17.
 */
public class BaseApp extends Application {

    private static BaseApp sBaseApp;

    @Override
    public void onCreate() {
        super.onCreate();

        sBaseApp = this;
    }


    public static Application getApp() {
        return sBaseApp;
    }
}
