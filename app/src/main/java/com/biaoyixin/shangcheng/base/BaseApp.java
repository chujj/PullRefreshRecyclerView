package com.biaoyixin.shangcheng.base;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by zhujj on 17-10-17.
 */
public class BaseApp extends Application {

    private static BaseApp sBaseApp;

    @Override
    public void onCreate() {
        super.onCreate();

        sBaseApp = this;



        MobclickAgent. startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this, "5a1ec7ae8f4a9d06e3000203", "fir"));
    }


    public static Application getApp() {
        return sBaseApp;
    }
}
