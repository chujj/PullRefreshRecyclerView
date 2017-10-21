package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.user.UserApi;
import com.ssc.weipan.base.BaseFragment;
import com.ssc.weipan.base.ToastHelper;
import com.viewpagerindicator.PageIndicator;

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


        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                if (position < 1) {
                    return Fragment.instantiate(getContext(), TradeFragment.class.getName());
                } else {
                    Bundle args = new Bundle();
                    args.putBoolean("is_keyline_test", true);
                    return Fragment.instantiate(getContext(), TradeFragment.class.getName(), args);
                }
            }

            @Override
            public CharSequence getPageTitle(int position) {
                String title = "";
                switch (position) {
                    case 0:
                        title = "美元兑日元";
                        break;
                    case 1:
                        title = "现货黄金";
                        break;
                    case 2:
                        title = "奥贵银";
                        break;
                }

                return title;
            }

            @Override
            public int getCount() {
                return 3;
            }
        });


        mIndicator.setViewPager(mViewPager);


        requireUserInfo();
    }


    private void requireUserInfo() {
        UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
        iUser.account(new Callback<UserApi.AccountResp>() {
            @Override
            public void success(UserApi.AccountResp accountResp, Response response) {
                if (accountResp.code != 0) {
                    ToastHelper.showToast(accountResp.message);
                } else {


                    mAssets.setText(accountResp.asset);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }




}
