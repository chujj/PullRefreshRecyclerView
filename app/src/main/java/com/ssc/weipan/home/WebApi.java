package com.ssc.weipan.home;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.ssc.weipan.api.trade.GoodsApi;
import com.ssc.weipan.model.BaseModel;

import de.greenrobot.event.EventBus;

/**
 * Created by zhujj on 17-10-22.
 */
public class WebApi {
    public final static String TAG ="WebApi";

    private Gson mGson = new Gson();

    @JavascriptInterface
    public void sender(String json, String type) {
        Log.e(TAG, json + " | " + type);

        if (TextUtils.equals("1", type)) {
            Type_1_Model model = mGson.fromJson(json, Type_1_Model.class);


            // goods
            for(GoodsApi.Good good : Data.sData.goods) {
                if (TextUtils.equals(good.label, model.label)) {
                    good.newPrice = model.newPrice;
                    good.high = model.high;
                    good.low = model.low;
                }
            }

            // charts
            for(GoodsApi.Charts chart : Data.sData.charts) {
                if (!TextUtils.equals(chart.label, model.label)) {
                    continue;
                }




                if(chart.list.size() > 0) { // chart1

                    GoodsApi.ChartData chartData = chart.list.get(chart.list.size() - 1);
                    if (model.dm == 1) {
                        chartData.xAxis.remove(chartData.xAxis.size() - 1);
                        chartData.data.remove(chartData.data.size() - 1);
                    }

                    chartData.xAxis.add(Long.valueOf(model.time));
                    chartData.data.add(Double.valueOf(model.newPrice));
                }



            }


            Message msg = Message.obtain();
            msg.what = 0x13d;
            EventBus.getDefault().post(msg);
        }

    }

    public static class Type_1_Model extends BaseModel {

        public long time; // ": 1509182477124,
        public String label; // ": "SCau0001",
        public float lastClose; // ": 333,
        public float open; // ": 331,
        public float high; // ": 331,
        public float low; // ": 329,
        public float newPrice; // ": 352,
        public int dm; // ": 1,
    }


    public static class D2 extends BaseModel {
        public float openPrice; // ": 360,
        public float closePrice; // ": 352,
        public float highPrice; // ": 363,
        public float lowPrice; // ": 297,
        @SerializedName("new")
        public boolean new_flag; // : true
    }

    public static class D3 extends BaseModel {
        public float openPrice; // ": 360,
        public float closePrice; // ": 352,
        public float highPrice; // ": 363,
        public float lowPrice; // ": 297,
        @SerializedName("new")
        public boolean new_flag; // : true
    }

    public static class D4 extends BaseModel {
        public float openPrice; // ": 360,
        public float closePrice; // ": 352,
        public float highPrice; // ": 363,
        public float lowPrice; // ": 297,
        @SerializedName("new")
        public boolean new_flag; // : true
    }

}
