package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
    @BindView(R2.id.viewpager)
    ViewPager mViewPager;
    @BindView(R2.id.listview)
    ListView mListView;
    @BindViews({R2.id.tab_1, R2.id.tab_2, R2.id.tab_3,})
    TextView[] mTabs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.trade_history_activity);

        ButterKnife.bind(this, this);

        mTopbar.setTitle("我的交易轨迹");


        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                LayoutInflater.from(container.getContext());
                TextView view = new TextView(container.getContext());
                view.setText("Position: " + position);
                container.addView(view);
                return view;
            }

            @Override
            public int getCount() {
                return 3;
            }


            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }


            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });


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
