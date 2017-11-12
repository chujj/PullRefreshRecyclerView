package com.biaoyixin.shangcheng.share;

import android.content.Context;

import com.biaoyixin.shangcheng.base.BaseApp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by zhujj on 17-11-4.
 */
public class Share {


//    private final static String APP_ID = "wxc062f523cb2dfaaf"; // 熊猫商城
    private final static String APP_ID = "wxa95394123b4a5040"; // 微心理
    private IWXAPI api;

    private void regToWX(Context activity) {
        api = WXAPIFactory.createWXAPI(activity, APP_ID, true);
        api.registerApp(APP_ID);
    }


    private static Share sInstance;

    public static synchronized  Share getInstance() {
        if (sInstance == null) {
            sInstance = new Share();
            sInstance.regToWX(BaseApp.getApp());
        }
        return sInstance;
    }


    public IWXAPI getIWXAPI() {
        return api;
    }
}
