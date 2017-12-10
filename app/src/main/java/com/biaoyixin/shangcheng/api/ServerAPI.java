package com.biaoyixin.shangcheng.api;

import android.content.Intent;
import android.os.Process;
import android.text.TextUtils;

import com.biaoyixin.shangcheng.Consts;
import com.biaoyixin.shangcheng.account.AccountHelper;
import com.biaoyixin.shangcheng.account.PersistentCookieStore;
import com.biaoyixin.shangcheng.base.BaseApp;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.biaoyixin.shangcheng.base.PreferencesUtil;
import com.biaoyixin.shangcheng.base.ToastHelper;
import com.biaoyixin.shangcheng.home.ConfirmPwdActivity;
import com.biaoyixin.shangcheng.home.PasswordSetupActivity;
import com.biaoyixin.shangcheng.login.AccountManager;
import com.biaoyixin.shangcheng.login.LoginActivity;
import com.biaoyixin.shangcheng.model.BaseModel;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
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
    private final static String USE_BACKUP_DOMAIN = "use_backup_domain";
    public final static String BACKUP_DOMAIN = "backup_domain";
    public final static String HOST = Consts.HOST; // "http://time.168zhibo.cn/";
    public final static String HOST_DOMAIN = "168zhibo.cn";
    private static ServerAPI sInstance;

    private HashMap<String, Object> mInterefaceMap;
    private RestAdapter mRestAdapter;

    public OkHttpClient mOKClient;


    public static String getHost() {
        String _host = HOST;
        if (PreferencesUtil.getBoolean(BaseApp.getApp(), USE_BACKUP_DOMAIN, false)) {
            _host = PreferencesUtil.getString(BaseApp.getApp(), BACKUP_DOMAIN);
        }
        return _host;
    }


    public static void fetchDomain(final boolean shouldSwitch) {
        final String old_host = getHost();
        final Runnable switchDomain = new Runnable() {
            @Override
            public void run() {
                String new_domain = getHost();
                PersistentCookieStore pcs = AccountHelper.getCookieStore();
                List<HttpCookie> cookies = pcs.get(URI.create(old_host));

                for (HttpCookie cookie: cookies) {
                    HttpCookie injectCookie = new HttpCookie(cookie.getName(), cookie.getValue());
                    pcs.add(URI.create(new_domain), injectCookie);
                }

                sInstance = null;

            }
        };


        boolean useBackup = PreferencesUtil.getBoolean(BaseApp.getApp(), USE_BACKUP_DOMAIN, false);
        boolean hasBackup = !TextUtils.isEmpty(PreferencesUtil.getString(BaseApp.getApp(), BACKUP_DOMAIN, ""));
        if (shouldSwitch && !useBackup && hasBackup) {
            PreferencesUtil.putBoolean(BaseApp.getApp(), USE_BACKUP_DOMAIN, true);
            switchDomain.run();
            return;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder().url(HOST + "dynamicDomain").get().build();
                    OkHttpClient client = new OkHttpClient();
                    Call call = client.newCall(request);
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        DynamicResp resp = new Gson().fromJson(response.body().string(), DynamicResp.class);

                        if (resp.code == 0 && !TextUtils.isEmpty(resp.data)) {
                            String _domainFormat = resp.data;
                            if (!_domainFormat.startsWith("http://")) {
                                _domainFormat = "http://" + _domainFormat;
                            }
                            if (!_domainFormat.endsWith("/")) {
                                _domainFormat = _domainFormat + "/";
                            }

                            PreferencesUtil.putString(BaseApp.getApp(), BACKUP_DOMAIN, _domainFormat);


                            if (shouldSwitch) {
                                PreferencesUtil.putBoolean(BaseApp.getApp(), USE_BACKUP_DOMAIN, true);
                                switchDomain.run();
                            }
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public static class DynamicResp extends BaseModel {
        public String data;
    }


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
        mRestAdapter = new RestAdapter.Builder().setEndpoint(getHost())
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

        { // 添加测试用cookie
            // http://yunpan.168zhibo.cn/recv_wechat/redirect?customerId=27&openid=oeAiVwiHuMngTuW4KeCSu9OPwi9g
//            HttpCookie injectCookie = new HttpCookie("SESSION", "c36e8a44-543a-4d31-b6fa-9ca2452ab41d");
//            AccountHelper.getCookieStore().add(URI.create(Consts.HOST),
//                    injectCookie);
        }

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
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

        } else if (baseModel.code == 3001) {
            Intent it = new Intent(BaseApp.getApp(), ConfirmPwdActivity.class);
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            BaseApp.getApp().startActivity(it);
        }

        if (showToast) {
            ToastHelper.showToast(baseModel.message);
        }
    }

}
