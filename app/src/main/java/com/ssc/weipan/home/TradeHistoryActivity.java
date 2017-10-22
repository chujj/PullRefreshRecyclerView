package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.Topbar;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhujj on 17-10-22.
 */
public class TradeHistoryActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;


    @BindViews({R2.id.tab_1, R2.id.tab_2, R2.id.tab_3,})
    TextView[] mTabs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.trade_history_activity);

        ButterKnife.bind(this, this);

        mTopbar.setTitle("我的交易轨迹");


        setSelectTab(0);
    }


    @OnClick(R2.id.tab_1)
    public void clickTab1() {
        setSelectTab(0);
    }
    @OnClick(R2.id.tab_2)
    public void clickTab2() {
        setSelectTab(1);
    }
    @OnClick(R2.id.tab_3)
    public void clickTab3() {
        setSelectTab(2);
    }
    private void setSelectTab(int index) {
        for (int i = 0; i < mTabs.length; i++) {
            mTabs[i].setSelected(i == index);
        }
    }
}
