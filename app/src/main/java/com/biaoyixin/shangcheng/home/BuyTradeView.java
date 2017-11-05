package com.biaoyixin.shangcheng.home;

import android.content.Context;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biaoyixin.shangcheng.Consts;
import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.trade.GoodsApi;
import com.biaoyixin.shangcheng.api.user.UserApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.ClosureMethod;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.biaoyixin.shangcheng.base.ToastHelper;
import com.biaoyixin.shangcheng.base.Topbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
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


    @BindView(R2.id.coupon1)
    TextView mCoupon1;
    @BindView(R2.id.coupon2)
    TextView mCoupon2;
    @BindView(R2.id.empty_coupon)
    TextView mCouponEmpty;


    private String mKey;
    private boolean mUp;
    private TimeIntervalProvider mProvider;


    private Map<String, Object> mBuyArgs = new HashMap<>();
    private BaseActivity mBaseActivity;
    private ClosureMethod mUIUpdater;
    private List<UserApi.Youhuiquan> mCoupons;

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


        mUIUpdater = new ClosureMethod() {
            @Override
            public Object[] run(Object... args) {

                for (int i = 0; i < Data.sData.goods.size(); i++) {
                    if (TextUtils.equals(Data.sData.goods.get(i).label, mKey)) {
                        mPriceNow.setText(String.format("当前价格：%.2f", Data.sData.goods.get(i).newPrice));
                        break;
                    }
                }

                if (args[0] == "newdata") {

                    return null;
                }

                mBuyArgs.put("goods_id", Data.sData.names.get(mKey).goods_id);
                mBuyArgs.put("up_down_type", mUp ? 0 : 1);
                mBuyArgs.put("secs", Integer.parseInt(mProvider.timeInterval().replaceAll("秒", "")));

                mGoodName.setText(String.format("合约：%s", Data.sData.names.get(mKey).goods_name));
                mTimeInterval.setText(String.format("结算周期：%s", mProvider.timeInterval()));

                {
                    int[] _temp = new int[2];
                    StringBuilder sb = new StringBuilder("订单方向：");
                    _temp[0] = sb.length();
                    sb.append(mUp ? "买涨" : "买跌");
                    _temp[1] = sb.length();
                    SpannableString ss = new SpannableString(sb.toString());
                    ss.setSpan(new ForegroundColorSpan(mUp ? 0xFFF35833 : 0xFF2CB545), _temp[0], _temp[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    mBuyUp.setText(ss);
                }


                mChipsLayout.setItems(Data.sData.names.get(mKey).chip);

                mChipsLayout.initView();

                mChipsLayout.setOnChipSelected(new ChipLabelsLayout.OnChipSelected() {
                    @Override
                    public void onChipSelected(String chipOri, boolean fromCoupon) {
                        int chip = Integer.parseInt(chipOri);

                        UserApi.Youhuiquan coupon = null;
                        if (fromCoupon) {
                            coupon = (UserApi.Youhuiquan) mBuyArgs.get("coupon");
                            if (coupon.couponType == 2) { // 直盈
                                chip = (int) coupon.discount;
                            } else if (coupon.couponType == 1) { // 增益

                            }
                        } else {
                            mBuyArgs.remove("coupon");
                        }

                        mBuyArgs.put("chip", chip);

                        mServiceFee.setText(
                                String.format("手续费：%.2f元", chip * Data.sData.names.get(mKey).serviceFee));

                        int[] _temp = new int[4];
                        StringBuilder sb = new StringBuilder("预期盈亏：");
                        _temp[0] = sb.length();
                        if (coupon != null && coupon.couponType == 1) {
                            sb.append( ((int)((chip + coupon.discount)* (1 - Data.sData.names.get(mKey).serviceFee))) + "");
                        } else {
                            sb.append( ((int)(chip * (1 - Data.sData.names.get(mKey).serviceFee))) + "");
                        }
                        _temp[1] = sb.length();
                        sb.append("/");
                        _temp[2] = sb.length();
                        if (coupon != null && coupon.couponType == 2) {
                            sb.append("0");
                        } else {
                            sb.append("-" + chip);
                        }
                        _temp[3] = sb.length();

                        SpannableString ss = new SpannableString(sb.toString());
                        ss.setSpan(new ForegroundColorSpan(0xFFF35833), _temp[0], _temp[1], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ss.setSpan(new ForegroundColorSpan(0xFF2CB545), _temp[2], _temp[3], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


                        mReturnGoods.setText(ss);

                        if (!fromCoupon) {
                            initCoupon(chip);
                        }
                    }
                });


                mChipsLayout.getChildAt(0).performClick();

                return null;
            }
        };


    }



    private void initCoupon(int chip) {
        if (mCoupons == null) return;

        mCoupon1.setVisibility(GONE);
        mCoupon1.setSelected(false);
        mCoupon2.setVisibility(GONE);
        mCoupon2.setSelected(false);
        boolean gettype2 = false;
        boolean gettype1 = false;
        for(UserApi.Youhuiquan yhq : mCoupons) {
            final UserApi.Youhuiquan final_yhq = yhq;
            if (yhq.couponType == 1 && !gettype1 && chip == yhq.needMoney) {
                gettype1 = true;
                mCoupon2.setText("增益券\n" + yhq.discount + "元");
                mCoupon2.setVisibility(VISIBLE);
                mCoupon2.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCoupon1.setSelected(false);
                        mCoupon2.setSelected(true);
                        mBuyArgs.put("coupon", final_yhq);
                        if (mChipsLayout.mListener != null) {
                            mChipsLayout.mListener.onChipSelected("" +mBuyArgs.get("chip"), true);
                        }
                    }
                });
            } else if (yhq.couponType == 2 && !gettype2){
                gettype2 = true;
                mCoupon1.setText("直盈券\n" + yhq.discount + "元");
                mCoupon1.setVisibility(VISIBLE);
                mCoupon1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCoupon1.setSelected(true);
                        mCoupon2.setVisibility(GONE);
                        mCoupon2.setSelected(false);
                        mBuyArgs.put("coupon", final_yhq);
                        mChipsLayout.selectChipItem("0", null, true);
                    }
                });
            }
        }
    }


    private void close() {
        ((ViewGroup)BuyTradeView.this.getParent()).removeView(BuyTradeView.this);
    }

    public void setKey(String key) {
        mKey = key;
    }

    public void initUI() {
        mUIUpdater.run("init");
        loadData();
    }


    private void loadData() {
        if (getContext() instanceof BaseActivity) {

            final BaseActivity baseActivity = (BaseActivity) getContext();
//            baseActivity.showLoadingDialog("加载中...", false);

            UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
            iUser.getYouhuiquan(new Callback<UserApi.YouhuiquanResp>() {
                @Override
                public void success(UserApi.YouhuiquanResp resp, Response response) {
//                    baseActivity.dismissLoadingDialog();
                    if (resp.code != 0) {
                        ServerAPI.handleCodeError(resp);
                    } else {
                        mCoupons = resp.data;

                        if ((mCoupons == null || mCoupons.size() == 0)) {
                            mCouponEmpty.setVisibility(VISIBLE);
                        } else {
                            mCouponEmpty.setVisibility(GONE);
                            initCoupon((Integer) mBuyArgs.get("chip"));
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    baseActivity.dismissLoadingDialog();
                    ServerAPI.HandlerException(error);
                }
            });
        }

    }

    public void setUpDown(boolean up) {
        mUp = up;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(Message msg) {
        if (msg.what != Consts.BoardCast_PriceMsg &&
                msg.what != Consts.BoardCast_TradeClose) return;

        if (mUIUpdater != null) {
            mUIUpdater.run("newdata");
        }
    }

    @OnClick(R2.id.cancel)
    public void clickCancel() {
        close();
    }

    @OnClick(R2.id.ok)
    public void clickOK() {

        int chip = (int) mBuyArgs.get("chip");
        UserApi.Youhuiquan coupon = (UserApi.Youhuiquan) mBuyArgs.get("coupon");
        String coupon_type = "";
        if (coupon != null) {
            coupon_type = coupon.couponType + "";
        }

//        if (1 > 0) {
//            System.out.println(chip + ", " + coupon_type);
//            return;
//        }

        mBaseActivity.showLoadingDialog("加载中", false);


        GoodsApi.IGood iGood = ServerAPI.getInterface(GoodsApi.IGood.class);
        iGood.buyTrade(
                (int) mBuyArgs.get("up_down_type"),
                (int) mBuyArgs.get("goods_id"),
                chip,
                1,
                (int) mBuyArgs.get("secs"),
                coupon_type,
                new Callback<GoodsApi.BuyTradeResponse>() {
                    @Override
                    public void success(GoodsApi.BuyTradeResponse baseModel, Response response) {
                        mBaseActivity.dismissLoadingDialog();
                        if (baseModel.code != 0) {
                            ServerAPI.handleCodeError(baseModel);
                        } else {

                            Data.sTradings.add(baseModel.data);
                            Message msg = Message.obtain();
                            msg.what = 0x14d;
                            EventBus.getDefault().post(msg);


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
        tpv.getStatus().trade_id = resp.data.trade_id;
        tpv.getStatus().label = resp.data.label;
        tpv.getStatus().chip = resp.data.chip;
        tpv.getStatus().goods_name = resp.data.goods_name; // Data.sData.names.get(mKey).goods_name;
        tpv.getStatus().buyUp = resp.data.up_down_type == 0;
        tpv.getStatus().open_price = resp.data.open_price; // openPrice;
        tpv.getStatus().close_price = resp.data.open_price; // openPrice + 1;
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
