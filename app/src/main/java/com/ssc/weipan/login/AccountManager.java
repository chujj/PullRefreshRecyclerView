package com.ssc.weipan.login;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.ssc.weipan.account.AccountHelper;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.login.LoginApi;
import com.ssc.weipan.base.BaseApp;
import com.ssc.weipan.base.PreferencesUtil;
import com.ssc.weipan.model.BaseModel;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by zhujj on 17-10-16.
 */
public class AccountManager {

    public final static String PREF_USER_ID = "key_user_id";
    public final static String PREF_USER_ACCOUNT = "KEY_USER_ACCOUNT";


    private static Gson sGson = new Gson();

    public static boolean isLogin() {
        List<HttpCookie> cookie = AccountHelper.getCookieStore().get(URI.create(ServerAPI.HOST + LoginApi.LOGIN_PATH));

        String userId = PreferencesUtil.getString(BaseApp.getApp(), PREF_USER_ID, "");
        return cookie.size() > 0 && !TextUtils.isEmpty(userId);
    }

    private static Account sAccount;

    public static class Account extends BaseModel {
        public String id;
        public String nickName;
        public String avatar;
        public String asset;
    }



    public static Account getAccount() {
        if (sAccount == null) {
            String str = PreferencesUtil.getString(BaseApp.getApp(), PREF_USER_ACCOUNT, "{}");
            sAccount = sGson.fromJson(str, Account.class);
        }

        return sAccount;
    }


    public static void saveAccount(Account account) {
        PreferencesUtil.putString(BaseApp.getApp(), PREF_USER_ACCOUNT, sGson.toJson(account));

        sAccount = null;

        EventBus.getDefault().post(account);
    }
}
