package com.biaoyixin.shangcheng.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.biaoyixin.shangcheng.Consts;
import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.api.trade.GoodsApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.BaseApp;
import com.biaoyixin.shangcheng.base.ClosureMethod;
import com.biaoyixin.shangcheng.base.ToastHelper;
import com.biaoyixin.shangcheng.model.BaseModel;
import com.biaoyixin.shangcheng.share.Share;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by zhujj on 17-10-22.
 */
public class WebApi {
    public final static String TAG ="WebApi";

    private Gson mGson = new Gson();


    public BaseActivity mActivity;

    @JavascriptInterface
    public void shareImage(final String imageUrl) {

        if (mActivity == null){
            return;
        }

        ShareActivity.sCallback = new ClosureMethod() {
            @Override
            public Object[] run(Object... args) {
                doShareImage(imageUrl, (Integer) args[0]);
                return new Object[0];
            }
        };
        Intent it = new Intent(mActivity, ShareActivity.class);
        mActivity.startActivity(it);
    }

    public void doShareImage(String imageUrl, final int shareType) {

        final ClosureMethod runnable = new ClosureMethod() {
            private static final int THUMB_SIZE = 120;

            @Override
            public Object[] run(Object... args) {

                int mTargetScene =  shareType; //  SendMessageToWX.Req.WXSceneSession;
                String path = (String) args[0];
                File file = new File(path);
                if (!file.exists()) {
                    Toast.makeText(BaseApp.getApp(), "文件不存在" + " path = " + path, Toast.LENGTH_LONG).show();
                    return null;
                }


                Bitmap bmp = BitmapFactory.decodeFile(path);

                WXImageObject imgObj = new WXImageObject(bmp);

                WXMediaMessage msg = new WXMediaMessage();
                msg.mediaObject = imgObj;

                Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                bmp.recycle();
                msg.thumbData = bmpToByteArray(thumbBmp, true);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("img");
                req.message = msg;
                req.scene = mTargetScene;
                Share.getInstance().getIWXAPI().sendReq(req);

                return null;
            }

            private String buildTransaction(final String type) {
                return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
            }


        };


        mActivity.showLoadingDialog("加载中...", false);
        Glide.with(BaseApp.getApp()).load(imageUrl).downloadOnly(new BaseTarget<File>() {

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                ToastHelper.showToast("图片数据出错，分享失败");
                mActivity.dismissLoadingDialog();
            }

            @Override
            public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                mActivity.dismissLoadingDialog();
//                System.out.println(resource.getAbsolutePath());
                runnable.run(new Object[] {resource.getAbsolutePath()});
            }

            @Override
            public void getSize(SizeReadyCallback cb) {
                cb.onSizeReady(100, 100);
            }
        });

    }

    public byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    @JavascriptInterface
    public void shareLink(final String url, final String title, final String desc, final String imageUrl) {
        if (mActivity == null) {
            return;
        }

        ShareActivity.sCallback = new ClosureMethod() {
            @Override
            public Object[] run(Object... args) {
                doShareLink(url, title, desc, imageUrl, (Integer) args[0]);
                return new Object[0];
            }
        };
        Intent it = new Intent(mActivity, ShareActivity.class);
        mActivity.startActivity(it);
    }

    public void doShareLink(final String url, final String title, final String desc, String imageUrl, final int type) {

        final ClosureMethod runnable = new ClosureMethod() {
            private static final int THUMB_SIZE = 120;

            @Override
            public Object[] run(Object... args) {

                int mTargetScene = type; //  SendMessageToWX.Req.WXSceneSession;
                boolean isPath = args[0] instanceof String;
                String path = null;
                Bitmap bitmap = null;
                if (isPath) {
                    path = (String) args[0];
                    File file = new File(path);
                    if (!file.exists()) {
                        Toast.makeText(BaseApp.getApp(), "文件不存在" + " path = " + path, Toast.LENGTH_LONG).show();
                        return null;
                    }
                } else {
                    bitmap = (Bitmap) args[0];
                }

                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = url;
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = title;
                msg.description = desc;
                if (isPath) {
                    Bitmap bmp = BitmapFactory.decodeFile(path);
                    Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
                    bmp.recycle();
                    msg.thumbData = bmpToByteArray(thumbBmp, true);
                } else {
                    msg.thumbData = bmpToByteArray(bitmap, true);
                }

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = mTargetScene;
                Share.getInstance().getIWXAPI().sendReq(req);

                return null;
            }

            private String buildTransaction(final String type) {
                return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
            }

            public byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
                if (needRecycle) {
                    bmp.recycle();
                }

                byte[] result = output.toByteArray();
                try {
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        };

        if (!TextUtils.isEmpty(imageUrl)) {
            mActivity.showLoadingDialog("加载中...", false);
            Glide.with(BaseApp.getApp()).load(imageUrl).downloadOnly(new BaseTarget<File>() {

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    ToastHelper.showToast("图片数据出错，分享失败");
                    mActivity.dismissLoadingDialog();
                }

                @Override
                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                    System.out.println(resource.getAbsolutePath());

                    mActivity.dismissLoadingDialog();

                    runnable.run(new Object[] {resource.getAbsolutePath()});
                }

                @Override
                public void getSize(SizeReadyCallback cb) {
                    cb.onSizeReady(100, 100);
                }
            });
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.ic_launcher);
            runnable.run(new Object[] {bitmap});
        }

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


                    List<Double> newData = Arrays.asList(
                            Double.valueOf(model.d2.openPrice),
                            Double.valueOf(model.d2.closePrice),
                            Double.valueOf(model.d2.lowPrice),
                            Double.valueOf(model.d2.highPrice));

                    if (model.dm != (chartIndex + 1)) {
                        chartData.xAxis.add(Long.valueOf(model.time));
                        chartData.data.add(newData);
                    } else {
                        int position = chartData.xAxis.size() - 1;
                        position = position < 0 ? 0 : position;
                        chartData.xAxis.add(position, Long.valueOf(model.time));
                        chartData.data.add(position, newData);
                    }
                }

                if (chart.list.size() > 2 && model.d3 != null) { // chart3

                    int chartIndex = 2;

                    GoodsApi.ChartData chartData = chart.list.get(chartIndex);
                    if (model.dm != (chartIndex + 1)) {
                        chartData.xAxis.remove(chartData.xAxis.size() - 1);
                        chartData.data.remove(chartData.data.size() - 1);
                    }

                    List<Double> newData = Arrays.asList(
                            Double.valueOf(model.d3.openPrice),
                            Double.valueOf(model.d3.closePrice),
                            Double.valueOf(model.d3.lowPrice),
                            Double.valueOf(model.d3.highPrice));


                    if (model.dm != (chartIndex + 1)) {
                        chartData.xAxis.add(Long.valueOf(model.time));
                        chartData.data.add(newData);
                    } else {
                        int position = chartData.xAxis.size() - 1;
                        position = position < 0 ? 0 : position;
                        chartData.xAxis.add(position, Long.valueOf(model.time));
                        chartData.data.add(position, newData);
                    }
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
                            Double.valueOf(model.d4.lowPrice),
                            Double.valueOf(model.d4.highPrice));

                    if (model.dm != (chartIndex + 1)) {
                        chartData.xAxis.add(Long.valueOf(model.time));
                        chartData.data.add(newData);
                    } else {
                        int position = chartData.xAxis.size() - 1;
                        position = position < 0 ? 0 : position;
                        chartData.xAxis.add(position, Long.valueOf(model.time));
                        chartData.data.add(position, newData);
                    }

                }



            }


            EventBus.getDefault().post(
                    Consts.getBoardCastMessage(Consts.BoardCast_PriceMsg));

        } else if (TextUtils.equals("2", type)) {

            Type_2_Model_Base modelBase = mGson.fromJson(json, Type_2_Model_Base.class);

            if (modelBase .isTradeCloseType()) {

                Type_2_Model model = mGson.fromJson(json, Type_2_Model.class);

                Data.sData._closeTrade.put(Long.valueOf(model.data.trade_id), model.data);
                EventBus.getDefault().post(
                        Consts.getBoardCastMessage(Consts.BoardCast_TradeClose));
            } else if (modelBase .isPaySuccessType()) {
                EventBus.getDefault().post(
                        Consts.getBoardCastMessage(Consts.BoardCast_PaySuccess));
            }


        }

    }


    public static class Type_2_Model_Base extends BaseModel {
        public String socketEventType;

        public boolean isTradeCloseType() {
            return TextUtils.equals("TRADE_CLOSED", socketEventType);
        }


        public boolean isPaySuccessType() {
            return TextUtils.equals("PAY_SUCCESS", socketEventType);
        }
    }

    public static class Type_2_Model extends BaseModel {
        public String socketEventType;

        GoodsApi.BuyTradeData data;

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
