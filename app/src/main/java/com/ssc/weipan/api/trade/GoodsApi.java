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
                             Callback<BaseModel> cb);

    }


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

}
