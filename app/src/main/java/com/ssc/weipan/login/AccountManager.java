package com.ssc.weipan.login;

import android.text.TextUtils;

import com.ssc.weipan.account.AccountHelper;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.login.LoginApi;
import com.ssc.weipan.base.BaseApp;
import com.ssc.weipan.base.PreferencesUtil;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/**
 * Created by zhujj on 17-10-16.
 */
public class AccountManager {

    public final static String PREF_USER_ID = "key_user_id";

    public static boolean isLogin() {
        List<HttpCookie> cookie = AccountHelper.getCookieStore().get(URI.create(ServerAPI.HOST + LoginApi.LOGIN_PATH));

        String userId = PreferencesUtil.getString(BaseApp.getApp(), PREF_USER_ID, "");
        return cookie.size() > 0 && !TextUtils.isEmpty(userId);
    }
}
