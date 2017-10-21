package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.trade.GoodsApi;
import com.ssc.weipan.api.user.UserApi;
import com.ssc.weipan.base.BaseFragment;
import com.ssc.weipan.base.ToastHelper;
import com.viewpagerindicator.PageIndicator;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-18.
 */
public class TradeHomeFragment extends BaseFragment {


    @BindView(R2.id.viewpager)
    ViewPager mViewPager;
    @BindView(R2.id.indicator)
    PageIndicator mIndicator;

    @BindView(R2.id.avatar)
    ImageView mAvatar;
    @BindView(R2.id.assets)
    TextView mAssets;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trade_home_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ButterKnife.bind(this, view);

        requireGoodsInfo();

        requireUserInfo();
    }


    private void requireGoodsInfo() {
        GoodsApi.IGood iGood = ServerAPI.getInterface(GoodsApi.IGood.class);
        iGood.goods(new Callback<GoodsApi.GoodsResp>() {
            @Override
            public void success(GoodsApi.GoodsResp goodsResp, Response response) {
                if (goodsResp.code != 0) {
                    ToastHelper.showToast(goodsResp.message);
                } else {
                    updateViewPager(goodsResp.data);
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    private void updateViewPager(GoodsApi.GoodsModel data) {

        Data.sData = data;

        final Map<String, GoodsApi.GoodName> names = data.names;
        Object[] temp = names.keySet().toArray();

        final String[] keys = new String[temp.length];
        for (int i = 0; i < temp.length; i++) {
            keys[i] = (String) temp[i];
        }

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                Bundle args = new Bundle();
                args.putString("key", keys[position]);
                return Fragment.instantiate(getContext(), TradeFragment.class.getName(), args);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return names.get(keys[position]).goods_name;
            }

            @Override
            public int getCount() {
                return keys.length;
            }
        });


        mIndicator.setViewPager(mViewPager);
    }


    private void requireUserInfo() {
        UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
        iUser.account(new Callback<UserApi.AccountResp>() {
            @Override
            public void success(UserApi.AccountResp accountResp, Response response) {
                if (accountResp.code != 0) {
                    ToastHelper.showToast(accountResp.message);
                } else {


                    mAssets.setText((TextUtils.isEmpty(accountResp.asset) ? 0 : accountResp.asset) + "元");
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }




}
