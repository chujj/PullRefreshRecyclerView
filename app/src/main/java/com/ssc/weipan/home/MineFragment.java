package com.ssc.weipan.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.trade.GoodsApi;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.BaseFragment;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.ToastHelper;
import com.ssc.weipan.login.AccountManager;

import java.util.ArrayList;
import java.util.List;

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
public class MineFragment extends BaseFragment {

    @BindView(R2.id.avatar)
    ImageView mAvatar;
    @BindView(R2.id.assets)
    TextView mAssets;
    @BindView(R2.id.free_asset)
    TextView mFreeAsset;
    @BindView(R2.id.lock_asset)
    TextView mLockAsset;


    @BindViews({R2.id.entry_1, R2.id.entry_2, R2.id.entry_3, R2.id.entry_4, R2.id.entry_5, R2.id.entry_6, })
    View[] mEntrys;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.mine_fragment, container, false);
        return viewRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);


        refreshUserInfoUI();

        initEntries();

        mEntrys[0].setVisibility(View.GONE);


        loadYinhangkaData();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            refreshUserInfoUI();
        }
    }


    private  void refreshUserInfoUI() {

        AccountManager.Account account = AccountManager.getAccount();
        Glide.with(this).load(account.avatar).into(mAvatar);
        mAssets.setText(account.asset);
        mFreeAsset.setText(account.free_asset);
        mLockAsset.setText(account.lock_asset);
    }

    private void loadYinhangkaData() {
        ((BaseActivity)getActivity()).showLoadingDialog("加载中...", false);
        GoodsApi.IGood iGood = ServerAPI.getInterface(GoodsApi.IGood.class);
        iGood.getOutMoneyUIInfo(new Callback<GoodsApi.OutMoneyUIInfoResp>() {
            @Override
            public void success(GoodsApi.OutMoneyUIInfoResp resp, Response response) {
                ((BaseActivity)getActivity()).dismissLoadingDialog();
                if (resp.code != 0) {
                    ToastHelper.showToast(resp.message);
                    ServerAPI.handleCodeError(resp);
                } else {
                    mEntrys[0].setVisibility(TextUtils.isEmpty(resp.data.bank_account) ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
                ((BaseActivity)getActivity()).dismissLoadingDialog();
            }
        });
    }

    private void initEntries() {
        for (int i = 0; i < entryProperties.size(); i++) {
            ((ImageView) CommonUtils.findView(mEntrys[i], R.id.icon)).setImageResource(R.drawable.trade_home_user_header_tixian);
            ((TextView)CommonUtils.findView(mEntrys[i], R.id.name)).setText((String) entryProperties.get(i)[0]);
            mEntrys[i].setOnClickListener((View.OnClickListener) entryProperties.get(i)[1]);
        }
    }


    private List<Object[]> entryProperties = new ArrayList<Object[]>() {
        {
            add( new Object[] {
                    "银行卡",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(MineFragment.this.getContext(),
                                    BankCardActivity.class);
                            MineFragment.this.startActivity(it);
                        }
                    }
            });
            add(new Object[] {
                    "我的交易轨迹",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(MineFragment.this.getContext(),
                                    TradeHistoryActivity.class);
                            MineFragment.this.startActivity(it);
                        }
                    }
            });
            add(new Object[] {
                    "出入金记录",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent it = new Intent(MineFragment.this.getContext(),
                                    ChuRuJinHistoryActivity.class);
                            MineFragment.this.startActivity(it);
                        }
                    }
            });
            add(new Object[] {
                    "优惠券",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(MineFragment.this.getContext(),
                                    YouHuiQuanActivity.class);
                            MineFragment.this.startActivity(it);
                        }
                    }
            });
            add(new Object[] {
                    "推荐码",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


//                            UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
//                            iUser.getYouhuiquan(new Callback<BaseModel>() {
//                                @Override
//                                public void success(BaseModel baseModel, Response response) {
//
//                                }
//
//                                @Override
//                                public void failure(RetrofitError error) {
//
//                                }
//                            });


                            Intent it = new Intent(MineFragment.this.getContext(),
                                    TuijianmaActivity.class);
                            MineFragment.this.startActivity(it);
                        }
                    }
            });
            add(new Object[] {
                    "个人设置",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(v.getContext(), SettingActivity.class);
                            v.getContext().startActivity(it);
                        }
                    }
            });
        }
    };



    @OnClick(R2.id.chongzhi)
    public void clickChongZhi() {
        Intent it = new Intent(getContext(), ChongZhiActivity.class);
        startActivity(it);
    }


    @OnClick(R2.id.tixian)
    public void clickTixian() {
        Intent it = new Intent(getContext(), TixianActivity.class);
        startActivity(it);
    }


}
