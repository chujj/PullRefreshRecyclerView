package com.biaoyixin.shangcheng.api.broker;

import com.biaoyixin.shangcheng.model.BaseModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by zhujj on 17-11-25.
 */
public class BrokerApi  {


    public static interface IBroker {
        @GET("/broker/index")
        public void getBroker(Callback<BrokerResp>cb);


        @GET("/broker/get_qrcode_url")
        public void getQRCode(Callback<QRCodeResp>cb);

        @FormUrlEncoded
        @POST("/broker/customer/list")
        public void getZhiShu(@Field("page") int page, @Field("size") int size, @Field("mobile") String mobile, Callback<ZhiShuResp>cb);


        @FormUrlEncoded
        @POST("/broker/brokerageDetail/list")
        public void getFanyong(@Field("page") int page, @Field("size") int size,
                               @Field("begin") String begine, @Field("end") String end,
                               Callback<FanyongResp>cb);

        @FormUrlEncoded
        @POST("/broker/broker_apply")
        void brokerRegist(@Field("realname") String name, @Field("orgcode") String id, Callback<BaseModel> baseModelCallback);
    }


    public static class BrokerResp extends BaseModel {
        public Broker data;
    }


    public static class Broker extends  BaseModel {
        @SerializedName("id")
        @Expose
        public long id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("headPortrait")
        @Expose
        public String headPortrait;
        @SerializedName("settledServeMoney")
        @Expose
        public long settledServeMoney;
        @SerializedName("unsettledServeMoney")
        @Expose
        public long unsettledServeMoney;
        @SerializedName("totalServeMoney")
        @Expose
        public long totalServeMoney;
    }


    public static class QRCodeResp extends BaseModel {
        public String data;
    }


    public static class ZhiShuResp extends BaseModel {
        public ZhiShuInfo data;
    }


    public static class ZhiShuInfo extends BaseModel {
        @SerializedName("content")
        @Expose
        public List<ZhiShuItem> content;
        @SerializedName("last")
        @Expose
        public boolean last;
        @SerializedName("totalPages")
        @Expose
        public long totalPages;
        @SerializedName("totalElements")
        @Expose
        public long totalElements;
        //        @SerializedName("sort")
//        @Expose
//        public Object sort;
        @SerializedName("size")
        @Expose
        public long size;
        @SerializedName("number")
        @Expose
        public long number;
        @SerializedName("first")
        @Expose
        public boolean first;
        @SerializedName("numberOfElements")
        @Expose
        public long numberOfElements;
    }


    public static class ZhiShuItem extends BaseModel {
        @SerializedName("id")
        @Expose
        public long id;
        @SerializedName("nickname")
        @Expose
        public String nickname;
        @SerializedName("mobile")
        @Expose
        public String mobile;
        @SerializedName("asset")
        @Expose
        public long asset;
        @SerializedName("createdOn")
        @Expose
        public String createdOn;
    }


    public static class FanyongResp extends BaseModel {
        public FanyongInfo data;
    }

    public static class FanyongInfo extends BaseModel {
        @SerializedName("content")
        @Expose
        public List<FanyongItem> content;
        @SerializedName("last")
        @Expose
        public boolean last;
        @SerializedName("totalPages")
        @Expose
        public long totalPages;
        @SerializedName("totalElements")
        @Expose
        public long totalElements;
        //        @SerializedName("sort")
//        @Expose
//        public Object sort;
        @SerializedName("first")
        @Expose
        public boolean first;
        @SerializedName("numberOfElements")
        @Expose
        public long numberOfElements;
        @SerializedName("size")
        @Expose
        public long size;
        @SerializedName("number")
        @Expose
        public long number;
    }


    public static class FanyongItem extends BaseModel {

        @SerializedName("nickname")
        @Expose
        public String nickname;
        @SerializedName("goodsName")
        @Expose
        public String goodsName;
        @SerializedName("closeTime")
        @Expose
        public String closeTime;
        @SerializedName("earnedServeFee")
        @Expose
        public long earnedServeFee;
    }
}
