package com.biaoyixin.shangcheng.api.broker;

import com.biaoyixin.shangcheng.model.BaseModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by zhujj on 17-11-25.
 */
public class BrokerApi  {


    public static interface IBroker {
        @GET("/broker/index")
        public void getBroker(Callback<BrokerResp>cb);
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
}
