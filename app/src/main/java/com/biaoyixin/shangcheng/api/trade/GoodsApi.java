package com.biaoyixin.shangcheng.api.trade;

import com.google.gson.annotations.SerializedName;
import com.biaoyixin.shangcheng.model.BaseModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by zhujj on 17-10-21.
 */
public class GoodsApi {

    public static interface IGood {

        @GET("/datasource")
        public void goods(Callback<GoodsResp> cb);

        @FormUrlEncoded
        @POST("/asset/time_up_down_selling")
        public void buyTrade(@Field("up_down_type") int up, // up 0; down 1
                             @Field("goods_id") int goods_id,
                             @Field("chip") int chip,
                             @Field("amount") int amount,
                             @Field("stop_win_percent") int secs,
                             @Field("use_coupon_type") String coupon_type,
                             Callback<BuyTradeResponse> cb);

        @POST("/customer/account_trade_list")
        public void getTradeHistory(Callback<TradeHistoryResp> cb);

        @FormUrlEncoded
        @POST("/customer/account_trade_list")
        public void getTradeHistoryV2(@Field("customerId") long customerId , Callback<TradeHistoryResp> cb);


        @GET("/asset/account_asset_pay_prepare")
        public void getInMoneyUIInfo(Callback<InMoneyUIInfoResp> cb);

        @GET("/asset/pay_type_list")
        public void igetInMoneyChannelList(Callback<InMoneyChannelListResp> cb);

        @GET("/asset/account_asset_extract_prepare")
        public void getOutMoneyUIInfo(Callback<OutMoneyUIInfoResp> cb);

        @GET("/areacode")
        public void getCityList(Callback<List<City>> cb);

        @GET("/asset/extract_type_list")
        public void getOutMoneyChannelList(Callback<OutChannelResp> cb);

        @GET("/customer/all_trade_list")
        void getTradeAllList(Callback<TradeAllListResp> cb);
    }

    ///////////////////////// goods /////////////////////

    public static class GoodsResp extends BaseModel {

        public GoodsModel data;
    }

    public static class GoodsModel extends BaseModel {
        public Map<String, GoodName> names;
        public List<Good> goods;

        public List<Charts> charts;


        public HashMap<Long, BuyTradeData> _closeTrade = new HashMap<>();
    }


    public static class GoodName extends BaseModel {
        public String goods_name;
        public int goods_id;
        public float serviceFee;
        public String[] point;
        public List<String> chip;
    }

    public static class Good extends BaseModel {
        public String label;
        public float open;
        public float high;
        public float low;
        public float newPrice;
    }

    public static class Charts extends BaseModel {
        public String label;

        public List<ChartData> list;
    }

    public static class ChartData extends BaseModel {
        public String name;


        public List<Long> xAxis;
        public List<Object> data;
    }


    ////////////////////////// trades ///////////////////
//    {
//	"code": 0,
//	"message": "交易成功",
//	"data": {
//		"trade_status": 0,
//		"trade_id": 682,
//		"label": "trade_btc",
//		"goods_id": 2,
//		"goods_name": "红木",
//		"open_price": 19400,
//		"open_time": "2017-10-22 14:06:15",
//		"open_time_interval": 0,
//		"chip": "100",
//		"amount": 1,
//		"up_down_type": 0,
//		"stop_loss_percent": 60,
//		"stop_win_percent": 60,
//		"leftTime": 60,
//		"close_price": null,
//		"close_type": null,
//		"close_time": null,
//		"serve_price": "10",
//		"win_money": "0",
//		"pay_type": 0
//	}
//}

    public static class BuyTradeResponse extends BaseModel {

        public BuyTradeData data;
    }

    public static class BuyTradeData extends BaseModel {
        public String label;
        public int trade_id;
        public String goods_name;
        public int up_down_type;
        public float open_price;
        public float close_price;
        public String close_time;
        public int pay_type;
        public int close_type;
        public String chip;
        public int leftTime;
        public float serve_price;
        public int amount;

        public String win_money;
        public String open_time;
        public float open_time_interval;
    }


    //////////////////////// input money ////////////////
    public static class InMoneyUIInfoResp extends BaseModel {
        public InMoneyUIInfo data;
    }

    public static class InMoneyUIInfo extends BaseModel {
        public String cashInFee;
        public List<String> pays;
    }
    public static class InMoneyChannelListResp extends BaseModel {
        public List<Channel> data;
    }

    public static class Channel extends BaseModel {
        public String name;
        public String icon;
        public String bank_url;
        public String type;
        public String url;
    }


    public static class WeChatPayResp extends BaseModel {

        public WeChatGateWay data;
    }

    public static class WeChatGateWay extends BaseModel {
        public String gateway;
    }


    public static class QRCodePayResp extends BaseModel {
        public String data;
    }


    public static class BankResp extends BaseModel {
        public List<Bank> data;
    }

    public static class Bank extends BaseModel {

        public String bankCode;
        public String bankName;
    }


    public static class UnipayResp extends BaseModel {
        public UnipayData data;
    }

    public static class UnipayData extends BaseModel {
        public String gateway;
    }


    public static class TradeHistoryResp extends BaseModel {
        public TradeHistory data;
    }


    public static class TradeHistory extends BaseModel {
        public List<BuyTradeData> today;
        public List<BuyTradeData> history;
        public List<BuyTradeData> open;
    }


    public static class OutMoneyUIInfoResp extends BaseModel {
        public OutMoneyUIInfo data;
    }

    public static class OutMoneyUIInfo extends BaseModel {
        public String extract_fee; // ":"2",
        public String mobile; // ":"136****0756"
        public boolean allowAssetExtract; // ":true,
        public String free_asset; // ":"98004",
        public String tips; // ":"每周一至周五9:00-16:00",
        public String bank_name; // ":"",
        public String id_card; // ":"",
        public String bank_account; // ":"",
        public String realname; // ":""
    }

    public static class City {
        @SerializedName("name")
        public String name; // ":"市辖区",
        @SerializedName("code")
        public String code; // ":"110100",
        @SerializedName("children")
        public List<City> children; // ":null
    }

    public static class OutChannelResp extends BaseModel {
        public List<OutChannel> data;
    }


    public static class OutChannel extends BaseModel {
        public String name; // ":"98出金",
        public String bank_url; // ":"http://time.168zhibo.cn/98pay/bank_list",
        public String extract_url; // ":"http://time.168zhibo.cn/98pay/asset_extract",
        public String type; // ":"union_pay"
    }


    public static class TradeAllListResp extends BaseModel {
        public List<BuyTradeData> data;
    }
}

