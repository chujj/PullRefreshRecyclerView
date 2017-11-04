package com.biaoyixin.shangcheng.share;

import android.app.Activity;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by zhujj on 17-11-4.
 */
public class Share {


    private final static String APP_ID = "";
    private IWXAPI api;

    private void regToWX(Activity activity) {
        api = WXAPIFactory.createWXAPI(activity, APP_ID, true);
        api.registerApp(APP_ID);
    }
}
