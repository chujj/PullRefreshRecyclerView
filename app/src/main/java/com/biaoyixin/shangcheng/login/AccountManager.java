package com.biaoyixin.shangcheng.login;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.biaoyixin.shangcheng.account.AccountHelper;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.login.LoginApi;
import com.biaoyixin.shangcheng.base.BaseApp;
import com.biaoyixin.shangcheng.base.PreferencesUtil;
import com.biaoyixin.shangcheng.model.BaseModel;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by zhujj on 17-10-16.
 */
public class AccountManager {

    public final static String PREF_USER_ACCOUNT = "KEY_USER_ACCOUNT";


    private static Gson sGson = new Gson();

    public static boolean isLogin() {
        List<HttpCookie> cookie = AccountHelper.getCookieStore().get(URI.create(ServerAPI.getHost() + LoginApi.LOGIN_PATH));

        String userId = getAccount().id;
        return cookie.size() > 0 && !TextUtils.isEmpty(userId);
    }

    private static Account sAccount;

    public static class Account extends BaseModel {
        public String id;
        public String nickName;
        public String avatar;
        public String asset;
        public String free_asset;
        public String lock_asset;
        public String invite_code;
        public String phoneNum;
    }



    public static Account getAccount() {
        if (sAccount == null) {
            String str = PreferencesUtil.getString(BaseApp.getApp(), PREF_USER_ACCOUNT, "{}");
            sAccount = sGson.fromJson(str, Account.class);
        }

        return sAccount;
    }


    public static synchronized void saveAccount(Account account) {
        PreferencesUtil.putString(BaseApp.getApp(), PREF_USER_ACCOUNT, sGson.toJson(account));

        sAccount = null;

        EventBus.getDefault().post(account);
    }
}
