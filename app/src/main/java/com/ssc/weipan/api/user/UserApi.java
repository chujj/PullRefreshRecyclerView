package com.ssc.weipan.api.user;

import com.ssc.weipan.api.trade.GoodsApi;
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
        @POST("/customer/set/broker")
        public void setBroker(@Field("inviteCode") String phone, Callback<BaseModel> cb);


        @FormUrlEncoded
        @POST("/customer/init_password")
        public void initPassword(@Field("password") String pwd, Callback<BaseModel> cb);

        @FormUrlEncoded
        @POST("/customer/forget_password")
        public void forgetPassword(@Field("password") String pwd, Callback<BaseModel> cb);


        @FormUrlEncoded
        @POST("/customer/modify_password")
        public void modifyPassword(@Field("old_password") String old_pwd, @Field("new_password") String new_password,  Callback<BaseModel> cb);


        @FormUrlEncoded
        @POST("/customer/auth_password")
        public void authPawd(@Field("password") String pwd, Callback<BaseModel> cb);


        @GET("/customer/my_coupon")
        public void getYouhuiquan(Callback<YouhuiquanResp> cb);


        public void getTuijianma(Callback<BaseModel> cb);

        @GET("/customer/account_asset_change_list")
        public void getChuRuJinHistory(Callback<ChuRuJinHistoryResp> cb);
    }


    public static class StatusResp extends BaseModel {
        public StatusData data;
    }

    public static class StatusData extends BaseModel {
        public String wechat_openid; // ": null,
		public boolean initpassword; // ": true,
		public String bindedmobile; // ": true,
//		"trades": [],
        public List<GoodsApi.BuyTradeData> trades;
		public int id; // ": 838,
		public String asset; // ": "109878",
//		"timeout": true,
		public String head_portrait; // ": "http://time.168zhibo.cn/assets/images/default_head_portrait.png"
    }


    public static class AccountResp extends BaseModel {
        public AccountData data;
    }

    public static class AccountData extends BaseModel {
        public String nickname; // ":"站",
        public String mobile; // ":"13675840756",
        public String asset; // ":"98004",
        public String free_asset; // ":"98004",
        public String lock_asset; // ":"0",
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


    public static class YouhuiquanResp extends BaseModel {

        public List<Youhuiquan> data;
    }

    public static class Youhuiquan extends BaseModel {
        public long id; // ": 1,
        public long customerId; // ": 844,
        public long gmtCreated; // ": 1508658869000,
        public long couponType; // ": 1, 优惠券类型 1:增益券 2:体验券
        public long discount; // ": 10,
        public long needMoney; // ": 100,
        public long validDays; // ": 7
    }
}
