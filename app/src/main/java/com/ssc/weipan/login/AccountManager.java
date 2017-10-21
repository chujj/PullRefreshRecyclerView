package com.ssc.weipan.login;

import com.ssc.weipan.account.AccountHelper;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.login.LoginApi;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/**
 * Created by zhujj on 17-10-16.
 */
public class AccountManager {

    public static boolean isLogin() {
        List<HttpCookie> cookie = AccountHelper.getCookieStore().get(URI.create(ServerAPI.HOST + LoginApi.LOGIN_PATH));

        return cookie.size() > 0;
    }
}
