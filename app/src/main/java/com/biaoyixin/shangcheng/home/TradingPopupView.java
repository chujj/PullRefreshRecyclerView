package com.biaoyixin.shangcheng.home;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biaoyixin.shangcheng.Consts;
import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.trade.GoodsApi;
import com.biaoyixin.shangcheng.base.ClosureMethod;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by zhujj on 17-10-21.
 */
public class TradingPopupView extends RelativeLayout {

    public static final int Layout = R.layout.trading_popup_layout;

    @BindView(R2.id.header_bg)
    View mHeaderBg;
    @BindView(R2.id.status)
    TextView mStatusTv;
    @BindView(R2.id.good_name)
    TextView mGoodName;
    @BindView(R2.id.ding_jin)
    TextView mDingJin;
    @BindView(R2.id.service_fee)
    TextView mServiceFee;
    @BindView(R2.id.status_promt)
    TextView mStatusPromt;
    @BindView(R2.id.open_price)
    TextView mOpenPrice;
    @BindView(R2.id.buy_up_down_type)
    TextView mBuyUpDownType;
    @BindView(R2.id.close_price)
    TextView mClosePrice;
    @BindView(R2.id.up_down_type)
    TextView mUpDownGuessType;
    @BindView(R2.id.confirm)
    View mConfirmBtn;

    private Runnable mUIUpdate;
    private Status mStatus;
    private CountDownTimer mTimer;

    private ClosureMethod mUIUpdater;

    public TradingPopupView(Context context) {
        super(context);
    }

    public TradingPopupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TradingPopupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TradingPopupView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public static class Status {
        public long trade_id;
        public String label;
        public boolean status_finished; // false -> 倒计时中; true -> 结束
        public int count_down_secs;
        public String goods_name;
        public float service_fee;
        public String chip;
        public String status_promt;
        public float open_price;
        public float close_price;

        public String win_money;

        public boolean buyUp;
        public boolean result_up;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this, this);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mConfirmBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        mStatus = new Status();


        mUIUpdater = new ClosureMethod() {

            @Override
            public Object[] run(Object... args) {
                for (GoodsApi.Good good : Data.sData.goods) {
                    if (TextUtils.equals(good.label, mStatus.label)) {
                        mStatus.close_price = good.newPrice;
                        break;
                    }
                }


                if (Data.sData._closeTrade.containsKey(Long.valueOf(mStatus.trade_id))) {
                    EventBus.getDefault().unregister(TradingPopupView.this);

                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }

                    mStatus.status_finished = true;
                    mStatus.close_price = Data.sData._closeTrade.get(Long.valueOf(mStatus.trade_id)).close_price;
                    mStatus.win_money = Data.sData._closeTrade.get(Long.valueOf(mStatus.trade_id)).win_money;
                }


                updateUI();

                return null;
            }
        };

        mUIUpdate = new Runnable() {
            @Override
            public void run() {
                mStatusTv.setText(mStatus.status_finished ? "平仓结果" : "获取中...");
                mGoodName.setText(String.format("合约：%s", mStatus.goods_name));
                mOpenPrice.setText(String.format("%.2f元", mStatus.open_price));
                mClosePrice.setText(String.format("%.2f元", mStatus.close_price));
                mServiceFee.setText(String.format("手续费：%.2f元", mStatus.service_fee));
                mDingJin.setText(String.format("定金：%s元", mStatus.chip));

                mBuyUpDownType.setText(mStatus.buyUp ? "买涨" : "买跌");
                mBuyUpDownType.setTextColor(mStatus.buyUp ? 0xFFF35833: 0xFF20B83E);


                float diff = (mStatus.close_price - mStatus.open_price);
                boolean equal = diff < 0.01f && diff > -0.01f;
                if (equal) {
                    mUpDownGuessType.setText("平");
                    mUpDownGuessType.setTextColor(0xffffffff);
                    mHeaderBg.setEnabled(true);

                    if (mStatus.status_finished) {
                        mStatusPromt.setText("平"  + mStatus.win_money);
                    }

                } else {
                    boolean priceUp = (mStatus.close_price - mStatus.open_price) > -0.001f;
                    boolean guessUp =(priceUp == mStatus.buyUp);
                    mUpDownGuessType.setText(guessUp ? "赢" : "亏");
                    mUpDownGuessType.setTextColor(guessUp ? 0xFFF35833: 0xFF20B83E);
                    mHeaderBg.setEnabled(guessUp);

                    if (mStatus.status_finished) {
                        mStatusPromt.setText((guessUp ? "赢" : "亏")  + mStatus.win_money);
                    }

                }


            }
        };

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

    }

    public void onEventMainThread(Message msg) {
        if (msg.what != Consts.BoardCast_PriceMsg &&
                msg.what != Consts.BoardCast_TradeClose) return;

        if (mUIUpdater != null) {
            mUIUpdater.run("newdata");
        }
    }

    private void close() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        ((ViewGroup)TradingPopupView.this.getParent()).removeView(TradingPopupView.this);
    }

    public Status getStatus() {
        return mStatus;
    }

    public void startCountDown() {
        mTimer = new CountDownTimer(mStatus.count_down_secs * 1000l, 300) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished /1000l <= 0) {
                    mStatusPromt.setText("获取中...");
                } else {
                    mStatusPromt.setText(millisUntilFinished /1000l + "秒");
                }
            }

            @Override
            public void onFinish() {
                mStatusPromt.setText("获取中...");
            }
        };
        mTimer.start();
    }


    public void updateUI() {
        mUIUpdate.run();
    }

}
