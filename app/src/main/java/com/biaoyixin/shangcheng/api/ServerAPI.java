package com.biaoyixin.shangcheng.api;

import android.content.Intent;
import android.os.Process;

import com.biaoyixin.shangcheng.Consts;
import com.biaoyixin.shangcheng.account.AccountHelper;
import com.biaoyixin.shangcheng.base.BaseApp;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.biaoyixin.shangcheng.base.ToastHelper;
import com.biaoyixin.shangcheng.home.ConfirmPwdActivity;
import com.biaoyixin.shangcheng.home.PasswordSetupActivity;
import com.biaoyixin.shangcheng.login.AccountManager;
import com.biaoyixin.shangcheng.login.LoginActivity;
import com.biaoyixin.shangcheng.model.BaseModel;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.android.MainThreadExecutor;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class ServerAPI {
    //    public final static String HOST = "http://m.weixl.org/xlcl-counselor-api";
    public final static String HOST = Consts.HOST; // "http://time.168zhibo.cn/";
    public final static String HOST_DOMAIN = "168zhibo.cn";
    private static ServerAPI sInstance;

    private HashMap<String, Object> mInterefaceMap;
    private RestAdapter mRestAdapter;

    public OkHttpClient mOKClient;

    public static synchronized <T> T cacheInsterface(Class<T> aInterface) {
        return getInstance().getCachedInterface(aInterface.getName(),
                aInterface);
    }

    public static synchronized ServerAPI getInstance() {
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

        mOKClient = createDumpContentOKClient();
        mRestAdapter = new RestAdapter.Builder().setEndpoint(HOST)
                .setLogLevel(CommonUtils.isDebugBuild() ? LogLevel.FULL : LogLevel.NONE)
                // .setClient(new TrustAllSSLConnectionClient())
                .setConverter(new GsonConverter(gson))
                .setExecutors(Executors.newCachedThreadPool(new ThreadFactory() {
                    @Override public Thread newThread(final Runnable r) {
                        return new Thread(new Runnable() {
                            @Override public void run() {
                                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
                                r.run();
                            }
                        }, "retrofit-idle");
                    }
                }), new MainThreadExecutor())
                .setClient(new OkClient(mOKClient)).build();

        mInterefaceMap = new HashMap<String, Object>();
    }

    private static OkHttpClient createDumpContentOKClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);

        CookieManager cm = new CookieManager(AccountHelper.getCookieStore(), CookiePolicy.ACCEPT_ALL);
////        cm = new CookieManager();
        okHttpClient.setCookieHandler(cm);
        okHttpClient.networkInterceptors().add(new StethoInterceptor());

        return okHttpClient;
    }


    public static void HandlerException(RetrofitError e) {
        ToastHelper.showToast("网络错误" + (CommonUtils.isDebugBuild() ? e.getMessage() : ""));
        e.printStackTrace();
    }


    public static void handleCodeError(BaseModel baseModel) {
        boolean showToast = true;
        if (baseModel.code == 3000) {

            Intent it = new Intent(BaseApp.getApp(), PasswordSetupActivity.class);
            BaseApp.getApp().startActivity(it);
        } else if (baseModel.code == 1000 || baseModel.code == 2000) {
            showToast = false;
            AccountManager.Account account = new Gson().fromJson("{}", AccountManager.Account.class);
            AccountManager.saveAccount(account);
            Intent it = new Intent(BaseApp.getApp(), LoginActivity.class);
            if (AccountManager.isLogin()) {
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            } else {
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            BaseApp.getApp().startActivity(it);

        } else if (baseModel.code == 5) {
            Intent it = new Intent(BaseApp.getApp(), ConfirmPwdActivity.class);
            BaseApp.getApp().startActivity(it);
        }

        if (showToast) {
            ToastHelper.showToast(baseModel.message);
        }
    }
}
