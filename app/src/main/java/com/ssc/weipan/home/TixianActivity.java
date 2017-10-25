package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.trade.GoodsApi;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.ClosureMethod;
import com.ssc.weipan.base.ToastHelper;
import com.ssc.weipan.base.Topbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-25.
 */
public class TixianActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;

    @BindView(R2.id.promt)
    TextView mHeaderPromt;
    @BindView(R2.id.tixian)
    View mTixian;
    @BindView(R2.id.chongzhi)
    View mChongZhi;
    @BindView(R2.id.banks_container)
    ViewGroup mBanksContainer;

    @BindView(R2.id.tixian_price)
    EditText mTiXianPrice;

    @BindView(R2.id.bank_name)
    EditText mBankName;

    @BindView(R2.id.privince_name)
    EditText mProvinceName;

    @BindView(R2.id.city_name)
    EditText mCityName;

    @BindView(R2.id.card_id)
    EditText mCardId;

    @BindView(R2.id.card_owner_name)
    EditText mCardOwnerName;

    @BindView(R2.id.card_owner_id)
    EditText mCardOwnerId;

    @BindView(R2.id.sms_code)
    EditText mSMSCode;

    @BindView(R2.id.require_sms_code)
    TextView mRequireSMSCode;

    @BindView(R2.id.confirm)
    View mConfirm;

    private List<GoodsApi.City> mCitys;
    private GoodsApi.OutMoneyUIInfo mUIInfo;
    private List<GoodsApi.OutChannel> mOutChannels;

    // TODO 各种click事件的关联
    private GoodsApi.OutChannel mSelectedOutChannel;
    private GoodsApi.City mSelectedProvince;
    private GoodsApi.City mSelectedCity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.tixian_activity);

        ButterKnife.bind(this, this);

        mTixian.setVisibility(View.GONE);
        mChongZhi.setVisibility(View.GONE);
        mHeaderPromt.setVisibility(View.VISIBLE);

        mTopbar.setTitle("提现");

        loadData();
    }

    private void loadData() {

        final Runnable initUI = new Runnable() {
            int success_count = 0;

            private ArrayList<ClosureMethod> bankChangeCBs = new ArrayList<>();

            @Override
            public void run() {
                success_count ++;

                if (success_count != 3) {
                    return;
                }

                dismissLoadingDialog();

                { // 初始化
                    mBanksContainer.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(mBanksContainer.getContext());
                    for (int i = 0; i < mOutChannels.size(); i++) {
                        final GoodsApi.OutChannel myChannel = mOutChannels.get(i);

                        View tixianBank = inflater.inflate(R.layout.tixian_bank_item, mBanksContainer, false);
                        ((TextView) tixianBank.findViewById(R.id.name)).setText(myChannel.name);
                        final ImageView checkbox = (ImageView) tixianBank.findViewById(R.id.checkbox);
                        checkbox.setSelected(false);

                        bankChangeCBs.add(new ClosureMethod() {
                            @Override
                            public Object[] run(Object... args) {
                                checkbox.setSelected(args[0] == myChannel);

                                return new Object[0];
                            }
                        });

                        final int index = i;
                        tixianBank.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setSelectedChannel(myChannel);
                            }
                        });

                        mBanksContainer.addView(tixianBank);
                    }
                }

                setSelectedChannel(mOutChannels.get(0));

            }

            private void setSelectedChannel(GoodsApi.OutChannel channel) {
                for (ClosureMethod cb : bankChangeCBs) {
                    cb.run(channel);
                }
            }
        };


        showLoadingDialog("加载中...", false);
        GoodsApi.IGood iGood = ServerAPI.getInterface(GoodsApi.IGood.class);
        iGood.getCityList(new Callback<List<GoodsApi.City>>() {
            @Override
            public void success(List<GoodsApi.City> cityListResp, Response response) {

                mCitys = cityListResp;
                initUI.run();
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
                dismissLoadingDialog();
            }
        });

        iGood.getOutMoneyUIInfo(new Callback<GoodsApi.OutMoneyUIInfoResp>() {
            @Override
            public void success(GoodsApi.OutMoneyUIInfoResp resp, Response response) {
                if (resp.code != 0) {
                    ToastHelper.showToast(resp.message);
                    ServerAPI.handleCodeError(resp);
                } else {
                    mUIInfo = resp.data;
                    initUI.run();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
                dismissLoadingDialog();
            }
        });


        iGood.getOutMoneyChannelList(new Callback<GoodsApi.OutChannelResp>() {
            @Override
            public void success(GoodsApi.OutChannelResp resp, Response response) {
                if (resp.code != 0) {
                    ToastHelper.showToast(resp.message);
                    ServerAPI.handleCodeError(resp);
                } else {
                    mOutChannels = resp.data;
                    initUI.run();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
                dismissLoadingDialog();
            }
        });

    }
}
