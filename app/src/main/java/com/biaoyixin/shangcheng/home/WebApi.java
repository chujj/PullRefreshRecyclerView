package com.biaoyixin.shangcheng.home;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.biaoyixin.shangcheng.Consts;
import com.biaoyixin.shangcheng.api.trade.GoodsApi;
import com.biaoyixin.shangcheng.model.BaseModel;

import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by zhujj on 17-10-22.
 */
public class WebApi {
    public final static String TAG ="WebApi";

    private Gson mGson = new Gson();



    @JavascriptInterface
    public void share(String json) {

        ShareModel shareModel = mGson.fromJson(json, ShareModel.class);


    }



    public static class ShareModel extends BaseModel {
        public String url;
    }

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

                    int chartIndex = 0;

                    GoodsApi.ChartData chartData = chart.list.get(chartIndex);
                    if (model.dm != (chartIndex + 1)) {
                        chartData.xAxis.remove(chartData.xAxis.size() - 1);
                        chartData.data.remove(chartData.data.size() - 1);
                    }

                    chartData.xAxis.add(Long.valueOf(model.time));
                    chartData.data.add(Double.valueOf(model.newPrice));
                }

                if (chart.list.size() > 1 && model.d2 != null) { // chart2

                    int chartIndex = 1;

                    GoodsApi.ChartData chartData = chart.list.get(chartIndex);
                    if (model.dm != (chartIndex + 1)) {
//                        System.out.println("debug2: " + " update");
                        chartData.xAxis.remove(chartData.xAxis.size() - 1);
                        chartData.data.remove(chartData.data.size() - 1);
                    }

                    chartData.xAxis.add(Long.valueOf(model.time));

                    List<Double> newData = Arrays.asList(
                            Double.valueOf(model.d2.openPrice),
                            Double.valueOf(model.d2.closePrice),
                            Double.valueOf(model.d2.highPrice),
                            Double.valueOf(model.d2.lowPrice));
//                    System.out.println("debug2: " + newData);
                    chartData.data.add(newData);
                }

                if (chart.list.size() > 2 && model.d3 != null) { // chart3

                    int chartIndex = 2;

                    GoodsApi.ChartData chartData = chart.list.get(chartIndex);
                    if (model.dm != (chartIndex + 1)) {
                        chartData.xAxis.remove(chartData.xAxis.size() - 1);
                        chartData.data.remove(chartData.data.size() - 1);
                    }

                    chartData.xAxis.add(Long.valueOf(model.time));

                    List<Double> newData = Arrays.asList(
                            Double.valueOf(model.d3.openPrice),
                            Double.valueOf(model.d3.closePrice),
                            Double.valueOf(model.d3.highPrice),
                            Double.valueOf(model.d3.lowPrice));
                    chartData.data.add(newData);
                }

                if (chart.list.size() > 3 && model.d4 != null) { // chart3

                    int chartIndex = 3;

                    GoodsApi.ChartData chartData = chart.list.get(chartIndex);
                    if (model.dm != (chartIndex + 1)) {
                        chartData.xAxis.remove(chartData.xAxis.size() - 1);
                        chartData.data.remove(chartData.data.size() - 1);
                    }

                    chartData.xAxis.add(Long.valueOf(model.time));

                    List<Double> newData = Arrays.asList(
                            Double.valueOf(model.d4.openPrice),
                            Double.valueOf(model.d4.closePrice),
                            Double.valueOf(model.d4.highPrice),
                            Double.valueOf(model.d4.lowPrice));
                    chartData.data.add(newData);
                }



            }


            EventBus.getDefault().post(
                    Consts.getBoardCastMessage(Consts.BoardCast_PriceMsg));

        } else if (TextUtils.equals("2", type)) {

            Type_2_Model model = mGson.fromJson(json, Type_2_Model.class);

            if (model.isTradeCloseType()) {
                Data.sData._closeTrade.put(Long.valueOf(model.data.trade_id), model.data);
            }


             EventBus.getDefault().post(
                    Consts.getBoardCastMessage(Consts.BoardCast_TradeClose));
        }

    }


    public static class Type_2_Model extends BaseModel {
        public String socketEventType;

        GoodsApi.BuyTradeData data;


        public boolean isTradeCloseType() {
            return TextUtils.equals("TRADE_CLOSED", socketEventType);
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

        public D2 d2;
        public D3 d3;
        public D4 d4;
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
