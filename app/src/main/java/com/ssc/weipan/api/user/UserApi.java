package com.ssc.weipan.api.user;

import com.ssc.weipan.model.BaseModel;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by zhujj on 17-10-21.
 */
public class UserApi {

    public static interface IUser {

        @GET("/customer/user_status")
        public void status(Callback<StatusResp> cb);


        @GET("/customer/user_account")
        public void account(Callback<AccountResp> cb);


        @FormUrlEncoded
        @POST("/customer/init_password")
        public void initPassword(@Field("password") String pwd, Callback<BaseModel> cb);


        @FormUrlEncoded
        @POST("/customer/auth_password")
        public void authPawd(@Field("password") String pwd, Callback<BaseModel> cb);
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
