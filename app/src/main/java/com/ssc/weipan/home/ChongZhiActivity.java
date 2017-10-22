package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.trade.GoodsApi;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.ToastHelper;
import com.ssc.weipan.base.Topbar;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-22.
 */
public class ChongZhiActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;

    @BindViews({R2.id.chongzhi, R2.id.tixian})
    View[] mView2hide;
    @BindView(R2.id.container)
    View mContainer;
    @BindView(R2.id.chips)
    ChipLabelsLayout mChips;
    @BindView(R2.id.channel_container)
    ViewGroup mChannelContainer;
    @BindView(R2.id.service_fee)
    TextView mServiceFee;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chongzhi_activity);


        ButterKnife.bind(this, this);

        mTopbar.setTitle("充值");

        for (int i = 0; i < mView2hide.length; i++) {
            mView2hide[i].setVisibility(View.GONE);
        }

        mContainer.setVisibility(View.GONE);


        requireData();
    }


    private GoodsApi.InMoneyUIInfo mInMoneyUIInfo;
    private List<GoodsApi.Channel> mInMoneyChannels;

    private void requireData() {
        final Runnable updateUI = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                count++;
                if (count < 2) {
                    return;
                }

                {
                    mChips.setItems(mInMoneyUIInfo.pays);
                    mChips.initView();

                    mChips.setOnChipSelected(new ChipLabelsLayout.OnChipSelected() {
                        @Override
                        public void onChipSelected(String chip) {

                        }
                    });


                    mChips.getChildAt(0).performClick();
                }


                {
                    String prefix = "每笔充值收取 ";
                    String subfix = " 的手续费";
                    SpannableString ss = new SpannableString(prefix + mInMoneyUIInfo.cashInFee + subfix);
                    ss.setSpan(new ForegroundColorSpan(0xffed5631),
                            prefix.length(),
                            (prefix + mInMoneyUIInfo.cashInFee).length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mServiceFee.setText(ss);
                }


                mChannelContainer.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(ChongZhiActivity.this);
                for (int i = 0; i < mInMoneyChannels.size(); i++) {
                    View _itemRoot = inflater.inflate(R.layout.in_money_channel, mChannelContainer, false);
                    ((TextView) CommonUtils.findView(_itemRoot, R.id.name))
                            .setText(mInMoneyChannels.get(i).name);
                    mChannelContainer.addView(_itemRoot);
                }

                mContainer.setVisibility(View.VISIBLE);
            }
        };


        GoodsApi.IGood iGoods = ServerAPI.getInterface(GoodsApi.IGood.class);
        iGoods.getInMoneyUIInfo(new Callback<GoodsApi.InMoneyUIInfoResp>() {
            @Override
            public void success(GoodsApi.InMoneyUIInfoResp resp, Response response) {
                if (resp.code != 0) {
                    ToastHelper.showToast(resp.message);
                    ServerAPI.handleCodeError(resp);
                } else {
                    mInMoneyUIInfo = resp.data;
                    updateUI.run();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });


        iGoods.igetInMoneyChannelList(new Callback<GoodsApi.InMoneyChannelListResp>() {
            @Override
            public void success(GoodsApi.InMoneyChannelListResp resp, Response response) {
                if (resp.code != 0) {
                    ToastHelper.showToast(resp.message);
                    ServerAPI.handleCodeError(resp);
                } else {
                    mInMoneyChannels = resp.data;
                    updateUI.run();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });
    }


}
