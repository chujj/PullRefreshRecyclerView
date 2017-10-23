package com.ssc.weipan.api.trade;

import com.ssc.weipan.model.BaseModel;

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
                             Callback<BuyTradeResponse> cb);


        @POST("/customer/account_trade_list")
        public void getTradeHistory(Callback<BaseModel> cb);


        @GET("/asset/account_asset_pay_prepare")
        public void getInMoneyUIInfo(Callback<InMoneyUIInfoResp> cb);

        @GET("/asset/pay_type_list")
        public void igetInMoneyChannelList(Callback<InMoneyChannelListResp> cb);

    }

    ///////////////////////// goods /////////////////////

    public static class GoodsResp extends BaseModel {

        public GoodsModel data;
    }

    public static class GoodsModel extends BaseModel {
        public Map<String, GoodName> names;
        public List<Good> goods;

        public List<Charts> charts;
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
        public int trade_id;
        public String goods_name;
        public int up_down_type;
        public float open_price;
        public float close_price;
        public int leftTime;
        public float serve_price;
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
}
