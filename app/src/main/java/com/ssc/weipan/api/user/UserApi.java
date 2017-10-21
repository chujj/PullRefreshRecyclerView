package com.ssc.weipan.api.user;

import com.ssc.weipan.model.BaseModel;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by zhujj on 17-10-21.
 */
public class UserApi {

    public static interface IUser {

        @GET("/customer/user_status")
        public void status(Callback<StatusResp> cb);


        @GET("/customer/user_account")
        public void account(Callback<AccountResp> cb);
    }


    public static class StatusResp extends BaseModel {
    }


    public static class AccountResp extends BaseModel {
        public String nickname; // ":"站",
        public String mobile; // ":"13675840756",
        public String asset; // ":"98004",
        // "free_asset":"98004",
        // "lock_asset":"0",
        // "broker":0,
        public String head_portrait; // ":"http://wx.qlogo.cn/mmopen/PiajxSqBRaEKYAP4qbkb8Fkn0sn8hstyLmXicXUSeRhVjmzicDjbgYoVt2IkfzIOcRh03qHAVtdF9SwcBoUrUTQyw/0"
    }
}