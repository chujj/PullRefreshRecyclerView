package com.ssc.weipan.api;

import android.content.Intent;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.ssc.weipan.account.AccountHelper;
import com.ssc.weipan.base.BaseApp;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.ToastHelper;
import com.ssc.weipan.home.PasswordSetupActivity;
import com.ssc.weipan.model.BaseModel;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class ServerAPI {
//    public final static String HOST = "http://m.weixl.org/xlcl-counselor-api";
    public final static String HOST = "http://time.168zhibo.cn";
    public final static String HOST_DOMAIN = "168zhibo.cn";
    private static ServerAPI sInstance;

    private HashMap<String, Object> mInterefaceMap;
    private RestAdapter mRestAdapter;

    public static synchronized <T> T cacheInsterface(Class<T> aInterface) {
        return getInstance().getCachedInterface(aInterface.getName(),
                aInterface);
    }

    private static synchronized ServerAPI getInstance() {
        if (sInstance == null) {
            sInstance = new ServerAPI();
        }

        return sInstance;
    }
    
    public static <T> T getInterface(Class<T> clz) {
        return cacheInsterface(clz);
    }

    private synchronized <T> T getCachedInterface(String name,
            Class<T> aInterface) {
        if (mInterefaceMap.get(name) == null) {
            mInterefaceMap.put(name, mRestAdapter.create(aInterface));
        }
        return (T) mInterefaceMap.get(name);
    }

    private ServerAPI() {
        Gson gson = new GsonBuilder()
//        .setExclusionStrategies(new ExclusionStrategy() {
//            @Override
//            public boolean shouldSkipField(FieldAttributes f) {
//                return f.getDeclaringClass().equals(RealmObject.class);
//            }
//
//            @Override
//            public boolean shouldSkipClass(Class<?> clazz) {
//                return false;
//            }
//        })
        .create();
        
        mRestAdapter = new RestAdapter.Builder().setEndpoint(HOST)
                .setLogLevel(CommonUtils.isDebugBuild() ? LogLevel.FULL : LogLevel.NONE)
                // .setClient(new TrustAllSSLConnectionClient())
                .setConverter(new GsonConverter(gson))
                .setClient(createDumpContentOKClient()).build();

        mInterefaceMap = new HashMap<String, Object>();
    }

    private static OkClient createDumpContentOKClient() {
        OkHttpClient okHttpClient = new OkHttpClient();

        CookieManager cm = new CookieManager(AccountHelper.getCookieStore(), CookiePolicy.ACCEPT_ALL);
////        cm = new CookieManager();
        okHttpClient.setCookieHandler(cm);
        okHttpClient.networkInterceptors().add(new StethoInterceptor());

        return new OkClient(okHttpClient);
    }


    public static void HandlerException(RetrofitError e) {
        ToastHelper.showToast("网络错误" + (CommonUtils.isDebugBuild() ? e.getMessage() : ""));
        e.printStackTrace();
    }


    public static void handleCodeError(BaseModel baseModel) {
        if (baseModel.code == 3000) {

            Intent it = new Intent(BaseApp.getApp(), PasswordSetupActivity.class);
            BaseApp.getApp().startActivity(it);
        } else {

        }
    }
}
