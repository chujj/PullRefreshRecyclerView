package com.ssc.weipan.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.Topbar;

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
    private Runnable mUIUpdate;
    private String mKey;
    private boolean mUp;

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
                mChipsLayout.setItems(Data.sData.names.get(mKey).chip);

                mChipsLayout.initView();
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
}
