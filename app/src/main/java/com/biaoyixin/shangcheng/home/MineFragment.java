package com.biaoyixin.shangcheng.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.trade.GoodsApi;
import com.biaoyixin.shangcheng.base.BaseFragment;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.biaoyixin.shangcheng.login.AccountManager;
import com.bumptech.glide.Glide;

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

    @BindView(R2.id.account)
    TextView mAccount;


    @BindViews({R2.id.entry_1, R2.id.entry_2, R2.id.entry_3, R2.id.entry_4, R2.id.entry_5, R2.id.entry_6,  R2.id.entry_7,  R2.id.entry_8, })
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

        TradeHomeFragment.requireUserInfo(new Runnable() {
            @Override
            public void run() {
                refreshUserInfoUI();
            }
        });
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

        String str = account.nickName;
        if (!TextUtils.isEmpty(account.phoneNum)) {
            str += ("("+ account.phoneNum + ")");
        }
        mAccount.setText(str);
    }

    private void loadYinhangkaData() {
//        ((BaseActivity)getActivity()).showLoadingDialog("加载中...", false);
        GoodsApi.IGood iGood = ServerAPI.getInterface(GoodsApi.IGood.class);
        iGood.getOutMoneyUIInfo(new Callback<GoodsApi.OutMoneyUIInfoResp>() {
            @Override
            public void success(GoodsApi.OutMoneyUIInfoResp resp, Response response) {
//                ((BaseActivity)getActivity()).dismissLoadingDialog();
                if (resp.code != 0) {

                    ServerAPI.handleCodeError(resp);
                } else {
                    mEntrys[0].setVisibility(TextUtils.isEmpty(resp.data.bank_account) ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
//                ((BaseActivity)getActivity()).dismissLoadingDialog();
            }
        });
    }

    private void initEntries() {
        for (int i = 0; i < entryProperties.size(); i++) {
            ((ImageView) CommonUtils.findView(mEntrys[i], R.id.icon)).setImageResource((int) entryProperties.get(i)[2]);
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
                    },
                    R.drawable.mine_fragment_entry_yinhangka
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
                    },
                    R.drawable.mine_fragment_entry_trade_history
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
                    },
                    R.drawable.mine_fragment_entry_bill_history
            });
            add(new Object[] {
                    "提货记录",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(getContext(), TiHuoHistoryActivity.class);
                            MineFragment.this.startActivity(it);
                        }
                    },
                    R.drawable.mine_fragment_entry_tihuo
            });
            add(new Object[] {
                    "我的优惠券",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(getContext(), HomeActivity.class);
                            it.putExtra("index", 1);
                            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(it);
//                            Intent it = new Intent(MineFragment.this.getContext(),
//                                    YouHuiQuanActivity.class);
//                            MineFragment.this.startActivity(it);
                        }
                    },
                    R.drawable.mine_fragment_entry_youhuiquan
            });
            add(new Object[] {
                    "推荐码",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent it = new Intent(MineFragment.this.getContext(),
                                    TuijianmaActivity.class);
                            MineFragment.this.startActivity(it);
                        }
                    },
                    R.drawable.mine_fragment_entry_tuijianma
            });
            add(new Object[] {
                    "个人设置",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(v.getContext(), SettingActivity.class);
                            v.getContext().startActivity(it);
                        }
                    },
                    R.drawable.mine_fragment_entry_settings
            });
            add(new Object[] {
                    "经纪人",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    },
                    R.drawable.mine_fragment_entry_jinjiren
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
