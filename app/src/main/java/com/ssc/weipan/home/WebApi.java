package com.ssc.weipan.home;

import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by zhujj on 17-10-22.
 */
public class WebApi {
    private final static String TAG ="WebApi";

    @JavascriptInterface
    public void sender(String json, String type) {
        Log.e(TAG, json + " | " + type);
    }

}
