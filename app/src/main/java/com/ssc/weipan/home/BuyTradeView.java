package com.ssc.weipan.home;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.trade.GoodsApi;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.ToastHelper;
import com.ssc.weipan.base.Topbar;
import com.ssc.weipan.model.BaseModel;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-21.
 */
public class BuyTradeView extends RelativeLayout {


    public static int Layout = R.layout.trade_buy_layout;

    @BindView(R2.id.topbar)
    Topbar mTopbar;

    @BindView(R2.id.chips)
    ChipLabelsLayout mChipsLayout;


    @BindView(R2.id.good_name)
    TextView mGoodName;
    @BindView(R2.id.buy_up)
    TextView mBuyUp;
    @BindView(R2.id.time_interval)
    TextView mTimeInterval;
    @BindView(R2.id.price_now)
    TextView mPriceNow;
    @BindView(R2.id.service_fee)
    TextView mServiceFee;
    @BindView(R2.id.return_goods)
    TextView mReturnGoods;

    private Runnable mUIUpdate;
    private String mKey;
    private boolean mUp;
    private TimeIntervalProvider mProvider;

    private Map<String, Object> mBuyArgs = new HashMap<>();
    private BaseActivity mBaseActivity;

    public BuyTradeView(Context context) {
        super(context);
    }

    public BuyTradeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BuyTradeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BuyTradeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this, this);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        ((View)CommonUtils.findView(this, R.id.handel_click)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mTopbar.setTitle("购买");
        mTopbar.setBackgroundColor(0xFF1F1F1F);

        mUIUpdate = new Runnable() {

            @Override
            public void run() {
                mBuyArgs.put("goods_id", Data.sData.names.get(mKey).goods_id);
                mBuyArgs.put("up_down_type", mUp ? 0 : 1);
                mBuyArgs.put("secs", Integer.parseInt(mProvider.timeInterval().replaceAll("秒", "")));

                mGoodName.setText(String.format("合约：%s", Data.sData.names.get(mKey).goods_name));
                mTimeInterval.setText(String.format("结算周期：%s", mProvider.timeInterval()));
                mBuyUp.setText(String.format("订单方向：买%s", mUp ? "涨" : "跌"));
                for (int i = 0; i < Data.sData.goods.size(); i++) {
                    if (TextUtils.equals(Data.sData.goods.get(i).label, mKey)) {
                        mPriceNow.setText(String.format("当前价格：%.2f", Data.sData.goods.get(i).newPrice));
                        break;
                    }
                }

                mChipsLayout.setItems(Data.sData.names.get(mKey).chip);

                mChipsLayout.initView();

                mChipsLayout.setOnChipSelected(new ChipLabelsLayout.OnChipSelected() {
                    @Override
                    public void onChipSelected(String chipOri) {
                        int chip = Integer.parseInt(chipOri);
                        mBuyArgs.put("chip", chip);

                        mServiceFee.setText(
                                String.format("手续费：%.2f元", chip * Data.sData.names.get(mKey).serviceFee));
                        mReturnGoods.setText(
                                String.format("预期收入：%.2f元", chip * (1 - Data.sData.names.get(mKey).serviceFee)));
                    }
                });


                mChipsLayout.getChildAt(0).performClick();

            }
        };
    }


    private void close() {
        ((ViewGroup)BuyTradeView.this.getParent()).removeView(BuyTradeView.this);
    }

    public void setKey(String key) {
        mKey = key;
    }

    public void initUI() {
        mUIUpdate.run();
    }

    public void setUpDown(boolean up) {
        mUp = up;
    }



    @OnClick(R2.id.cancel)
    public void clickCancel() {
        close();
    }

    @OnClick(R2.id.ok)
    public void clickOK() {


        mBaseActivity.showLoadingDialog("加载中", false);

        GoodsApi.IGood iGood = ServerAPI.getInterface(GoodsApi.IGood.class);
        iGood.buyTrade(
                (int) mBuyArgs.get("up_down_type"),
                (int) mBuyArgs.get("goods_id"),
                (int) mBuyArgs.get("chip"),
                1,
                (int) mBuyArgs.get("secs"),
                new Callback<GoodsApi.BuyTradeResponse>() {
                    @Override
                    public void success(GoodsApi.BuyTradeResponse baseModel, Response response) {
                        mBaseActivity.dismissLoadingDialog();
                        if (baseModel.code != 0) {
                            ToastHelper.showToast(baseModel.message);
                            ServerAPI.handleCodeError(baseModel);
                        } else {
                            close();

                            showCounterdownText(baseModel);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mBaseActivity.dismissLoadingDialog();
                        ServerAPI.HandlerException(error);
                    }
                });
    }


    private void showCounterdownText (GoodsApi.BuyTradeResponse resp) {
        View viewRoot = LayoutInflater.from(getContext()).inflate(R.layout.trading_popup_layout, null, false);
        TradingPopupView tpv = CommonUtils.findView(viewRoot, R.id.trading_popup_view);
        tpv.getStatus().goods_name = resp.data.goods_name; // Data.sData.names.get(mKey).goods_name;
        tpv.getStatus().buyUp = resp.data.up_down_type == 0;
        tpv.getStatus().open_price = resp.data.open_price; // openPrice;
        tpv.getStatus().close_price = resp.data.close_price; // openPrice + 1;
        tpv.getStatus().status_finished = false;
        tpv.getStatus().count_down_secs =  resp.data.leftTime; // (int) mBuyArgs.get("secs");
        tpv.getStatus().service_fee =  resp.data.serve_price; // ((int) mBuyArgs.get("chip") * Data.sData.names.get(mKey).serviceFee);
        tpv.updateUI();
        tpv.startCountDown();

        CommonUtils.addToActivity(mBaseActivity, viewRoot);

    }

    public void setActivity(BaseActivity activity) {
        mBaseActivity = activity;
    }

    public static interface  TimeIntervalProvider {
        public String timeInterval();
    }
    public void setTimeIntervalProvider(TimeIntervalProvider provider) {

        mProvider = provider;
    }
}
