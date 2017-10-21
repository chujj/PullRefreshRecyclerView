package com.ssc.weipan.api.trade;

import com.ssc.weipan.model.BaseModel;

import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by zhujj on 17-10-21.
 */
public class GoodsApi {

    public static interface IGood {

        @GET("/datasource")
        public void goods(Callback<GoodsResp> cb);
    }


    public static class GoodsResp extends BaseModel {

        public GoodsModel data;
    }

    public static class GoodsModel extends BaseModel {
        public Map<String, GoodName> names;
        public List<Good> goods;
    }


    public static class GoodName extends BaseModel {
        public String goods_name;
        public int goods_id;
        public String[] point;
    }

    public static class Good extends BaseModel {
        public String label;
        public float open;
        public float high;
        public float low;
    }

}
