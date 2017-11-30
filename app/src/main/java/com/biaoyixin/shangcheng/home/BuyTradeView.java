package com.biaoyixin.shangcheng.home;

import android.content.Context;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
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

import java.util.HashMap;
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


    @BindView(R2.id.up_down_type)
    TextView mBuyUp;

    @BindView(R2.id.chips)
    ChipLabelsLayout mChips;
    @BindView(R2.id.up_down_size)
    ChipLabelsLayout mUpdownSize;

    @BindView(R2.id.num)
    TextView mNum;
    @BindView(R2.id.num_text)
    TextView mNumPromt;

    @BindView(R2.id.ok)
    View mOk;


    private String mKey;
    private boolean mUp;

    private Map<String, Object> mBuyArgs = new HashMap<>();
    private BaseActivity mBaseActivity;
    private ClosureMethod mUIUpdater;

    private GoodsApi.UpdownSellingUIInfo mUIInfo;

    public ClosureMethod mTradeCB;

    public int mNumCount = 1;

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

        mUIUpdater = new ClosureMethod() {
            @Override
            public Object[] run(Object... args) {

                mOk.setBackgroundResource(mUp ?
                        R.drawable.trade_fragment_buy_up_bg :
                        R.drawable.trade_fragment_buy_down_bg);

                mBuyUp.setText(mUp ? "看涨" : "看跌");
                mBuyUp.setBackgroundResource(mUp ? R.drawable.up_tit : R.drawable.down_tit);

                mBuyArgs.put("goods_id", Data.sData.names.get(mKey).goods_id);
                mBuyArgs.put("up_down_type", mUp ? 0 : 1);

                {
                    mChips.mSubfix = "";
                    mChips.setItems(mUIInfo.chipPrice);
                    mChips.initView();
                    mChips.setOnChipSelected(new ChipLabelsLayout.OnChipSelected() {
                        @Override
                        public void onChipSelected(String chipOri, boolean fromCoupon) {
                            mBuyArgs.put("chip", chipOri);
                        }
                    });
                    mChips.getChildAt(0).performClick();
                }

                {
                    mUpdownSize.mSubfix = "";
                    mUpdownSize.setItems(mUIInfo.point);
                    mUpdownSize.initView();
                    mUpdownSize.setOnChipSelected(new ChipLabelsLayout.OnChipSelected() {
                        @Override
                        public void onChipSelected(String chipOri, boolean fromCoupon) {
                            mBuyArgs.put("secs", chipOri);
                        }
                    });
                    mUpdownSize.getChildAt(0).performClick();
                }

                mNumPromt.setText("最大下手单数" + mUIInfo.maxTrades + mUIInfo.tradeUnit);

                mNum.setText("" + mNumCount);
                return null;
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
        loadData();
    }


    private void loadData() {
        if (getContext() instanceof BaseActivity) {
            final BaseActivity baseActivity = (BaseActivity) getContext();
//            baseActivity.showLoadingDialog("加载中...", false);

            GoodsApi.IGood iGood = ServerAPI.getInterface(GoodsApi.IGood.class);
            iGood.updownSelliingIndex(Data.sData.names.get(mKey).goods_id + "", new Callback<GoodsApi.UpdownSellingUIResp>() {
                @Override
                public void success(GoodsApi.UpdownSellingUIResp resp, Response response) {
                    if (resp.code != 0) {
                        ServerAPI.handleCodeError(resp);
                    } else {
                        mUIInfo = resp.data;
                        ButterKnife.bind(BuyTradeView.this, BuyTradeView.this);
                        mUIUpdater.run("init");
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

    @OnClick(R2.id.num_down)
    public void clickNumDown() {
        changeNum(-1);
    }

    @OnClick(R2.id.num_up)
    public void clickNumUp() {
        changeNum(1);
    }


    private void changeNum(int change) {
        int next = mNumCount + change;
        if (next <= 0) {
            return;
        }

        if (next > mUIInfo.maxTrades) {
            return;
        }

        mNumCount = next;
        mNum.setText("" + mNumCount);
    }

    @OnClick(R2.id.cancel)
    public void clickCancel() {
        close();
    }

    @OnClick(R2.id.ok)
    public void clickOK() {
        String chip = (String) mBuyArgs.get("chip");

        mBaseActivity.showLoadingDialog("加载中", false);

        GoodsApi.IGood iGood = ServerAPI.getInterface(GoodsApi.IGood.class);
        iGood.buyTrade(
                (int) mBuyArgs.get("up_down_type"),
                (int) mBuyArgs.get("goods_id"),
                chip,
                mNumCount,
                (String) mBuyArgs.get("secs"),

                new Callback<GoodsApi.BuyTradeResponse>() {
                    @Override
                    public void success(GoodsApi.BuyTradeResponse baseModel, Response response) {
                        mBaseActivity.dismissLoadingDialog();
                        if (baseModel.code != 0) {
                            ServerAPI.handleCodeError(baseModel);
                        } else {

                            if (mTradeCB != null) {
                                mTradeCB.run(baseModel.data);
                            }

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
//        View viewRoot = LayoutInflater.from(getContext()).inflate(R.layout.trading_popup_layout, null, false);
//        TradingPopupView tpv = CommonUtils.findView(viewRoot, R.id.trading_popup_view);
//        tpv.getStatus().trade_id = resp.data.trade_id;
//        tpv.getStatus().label = resp.data.label;
//        tpv.getStatus().chip = resp.data.chip;
//        tpv.getStatus().goods_name = resp.data.goods_name; // Data.sData.names.get(mKey).goods_name;
//        tpv.getStatus().buyUp = resp.data.up_down_type == 0;
//        tpv.getStatus().open_price = resp.data.open_price; // openPrice;
//        tpv.getStatus().close_price = resp.data.open_price; // openPrice + 1;
//        tpv.getStatus().status_finished = false;
//        tpv.getStatus().count_down_secs =  resp.data.leftTime; // (int) mBuyArgs.get("secs");
//        tpv.getStatus().service_fee =  resp.data.serve_price; // ((int) mBuyArgs.get("chip") * Data.sData.names.get(mKey).serviceFee);
//        tpv.updateUI();
//        tpv.startCountDown();
//
//        CommonUtils.addToActivity(mBaseActivity, viewRoot);
    }

    public void setActivity(BaseActivity activity) {
        mBaseActivity = activity;
    }


}
