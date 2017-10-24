package com.ssc.weipan.api.user;

import com.ssc.weipan.model.BaseModel;

import java.util.List;

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
        @POST("/customer/modify_password")
        public void modifyPassword(@Field("old_password") String old_pwd, @Field("new_password") String new_password,  Callback<BaseModel> cb);


        @FormUrlEncoded
        @POST("/customer/auth_password")
        public void authPawd(@Field("password") String pwd, Callback<BaseModel> cb);


        public void getYouhuiquan(Callback<BaseModel> cb);


        public void getTuijianma(Callback<BaseModel> cb);

        @GET("/customer/account_asset_change_list")
        public void getChuRuJinHistory(Callback<ChuRuJinHistoryResp> cb);
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



    public static class ChuRuJinHistoryResp extends BaseModel {
        public List<ChuRuJin> data;
    }


    public static class ChuRuJin extends BaseModel {
        public int changeType; // ":1,
        public String title; // ":"微信支付",
        public String orderNo; // ":"C15170730105626476123002",
        public String money; // ":"0.6",
        public String createdOn; // ":"2017-07-30 10:56:27",
//            "changeStatus":101,
//            "changeOn":null
    }
}
