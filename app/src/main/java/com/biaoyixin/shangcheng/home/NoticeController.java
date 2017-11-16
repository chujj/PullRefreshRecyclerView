package com.biaoyixin.shangcheng.home;

import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.user.UserApi;
import com.biaoyixin.shangcheng.base.BaseApp;
import com.biaoyixin.shangcheng.base.ClosureMethod;
import com.biaoyixin.shangcheng.base.PreferencesUtil;
import com.google.gson.Gson;

import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-11-16.
 */
public class NoticeController {

    private final static String KEY = "PREF_KEY_NoticeController";
    private final ClosureMethod mNoticeCB;

    public NoticeController(ClosureMethod noticedCB) {
        mNoticeCB = noticedCB;
    }

    public static Callback<UserApi.NoticeResp> handler(final String type, final ClosureMethod noticedCB) {

        final NoticeController controller = new NoticeController(noticedCB);

        Callback<UserApi.NoticeResp> callback = new Callback<UserApi.NoticeResp>() {
            @Override
            public void success(UserApi.NoticeResp resp, Response response) {
                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);
                } else {
                    if (resp.data != null) {
                        controller.handle(type, resp.data);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        };

        return callback;
    }

    private void handle(String type, UserApi.Notice data) {
        Gson  gson = new Gson();
        Map<String, Object> noticeds = gson.fromJson( PreferencesUtil.getString(BaseApp.getApp(), KEY, "{}"),
                Map.class);


        boolean needShow = false;
        if (noticeds.containsKey(type)) {
            if (((Double)noticeds.get(type)).intValue() != data.id) {
                needShow = true;
            }
        } else {
            needShow = true;
        }


        if (needShow) {
            noticeds.put(type, data.id);
            PreferencesUtil.putString(BaseApp.getApp(), KEY, gson.toJson(noticeds));

            mNoticeCB.run(data);
        }

    }
}
