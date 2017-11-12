package com.biaoyixin.shangcheng.api.user;

import com.biaoyixin.shangcheng.api.trade.GoodsApi;
import com.biaoyixin.shangcheng.model.BaseModel;
import com.google.gson.annotations.SerializedName;

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

        @GET("/broker/brokerageReport")
        public void brokerReport(Callback<BrokerReportResp> cb);


        @FormUrlEncoded
        @POST("/upgrade/check")
        public void upgradeCheck(@Field("internalVersion") int internalVersion,
                                 @Field("os") String os,
                                 Callback<UpgradeResp> cb);


        @GET("/systemInfo")
        public void getSystemInfo(Callback<SystemInfoResp> cb);


        @GET("/broker/customer/list")
        public void getBrokerList(Callback<BrokerListResp> cb);
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
        public String inviteCode;
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


    public static class BrokerReportResp extends BaseModel {
        public BrokerReport data;
    }


    public static class BrokerReport extends BaseModel {
        public int todayTradeMoneyLv1; // ": 10000,
        public int todayTradeMoneyLv2; // ": 10000,
        public int totalTradeMoneyLv1; // ": 20000,
        public int totalTradeMoneyLv2; // ": 20000,
        public int todayBrokerageLv1; // ": 50,
        public int todayBrokerageLv2; // ": 200,
        public int totalBrokerageLv1; // ": 100,
        public int totalBrokerageLv2; // ": 400
    }

    public static class UpgradeResp extends BaseModel {
        public UpgradeInfo data;
    }

    public static class UpgradeInfo  extends BaseModel {
        public boolean force; // ": true,//是否强制更新
//        public String version; // ": "2.1.2",//版本号
//        public int internalVersion; // ": 20102,//内部版本号
        public String desc; // ": "更新内容",
        public String url; // ": "下载地址"
    }


    public static class SystemInfoResp extends BaseModel {
        public SystemInfo data;
    }

    public static class SystemInfo extends BaseModel {
        public String settleTime; // : "18:06"
    }


    public static class BrokerListResp extends BaseModel {
        public BrokerListPage data;
    }


    public static class BrokerListPage extends BaseModel {
        public List<Broker> content;
        public boolean last;
    }

    public static class Broker extends BaseModel {
        public long id;
        @SerializedName("nickname")
        public String name;
        @SerializedName("mobile")
        public String phone;
    }
}
