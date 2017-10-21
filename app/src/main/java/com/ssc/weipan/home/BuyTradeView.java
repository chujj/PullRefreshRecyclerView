package com.ssc.weipan.home;

import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.Topbar;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
                    public void onChipSelected(int chip) {
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

    }


    public static interface  TimeIntervalProvider {
        public String timeInterval();
    }
    public void setTimeIntervalProvider(TimeIntervalProvider provider) {

        mProvider = provider;
    }
}
