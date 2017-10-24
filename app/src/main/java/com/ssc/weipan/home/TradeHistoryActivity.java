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
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.trade.GoodsApi;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.ToastHelper;
import com.ssc.weipan.base.Topbar;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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


    private TradeHistoroyAdapter[] mAdapter = new TradeHistoroyAdapter[3];

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
                View vievw = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.trade_history_listview, container, false);
                ListView listView = CommonUtils.findView(vievw, R.id.listview);


//                int[] color = new int[] {
//                        0xffff0000,
//                        0xff00ff00,
//                        0xff0000ff,
//                };
//                vievw.setBackgroundColor(color[position]);
                TradeHistoroyAdapter adapter = new TradeHistoroyAdapter();
                mAdapter[position] = adapter;
                listView.setAdapter(adapter);

                container.addView(vievw);

                return vievw;
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


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelectTab(position, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setSelectTab(0);


        loadData();
    }

    private void loadData() {

        showLoadingDialog("加载中...", false);

        GoodsApi.IGood iGood = ServerAPI.getInterface(GoodsApi.IGood.class);
        iGood.getTradeHistory(new Callback<GoodsApi.TradeHistoryResp>() {
            @Override
            public void success(GoodsApi.TradeHistoryResp baseModel, Response response) {
                TradeHistoryActivity.this.dismissLoadingDialog();
                if (baseModel.code != 0) {
                    ToastHelper.showToast(baseModel.message);
                    ServerAPI.handleCodeError(baseModel);
                } else {

                    mAdapter[0].setData(baseModel.data.open);
                    mAdapter[0].setType(TradeHistoroyAdapter.TYPE_Today);
                    mAdapter[1].setData(baseModel.data.today);
                    mAdapter[2].setData(baseModel.data.history);

                    for (int i = 0; i < mAdapter.length; i++) {
                        mAdapter[i].notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                TradeHistoryActivity.this.dismissLoadingDialog();
                ServerAPI.HandlerException(error);
            }
        });
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
        this.setSelectTab(index, true);
    }

    private void setSelectTab(int index, boolean triggleViewPager) {
        for (int i = 0; i < mTabs.length; i++) {
            mTabs[i].setSelected(i == index);
        }

        if (triggleViewPager) {
            mViewPager.setCurrentItem(index);
        }
    }
}
