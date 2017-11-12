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
import com.biaoyixin.shangcheng.api.user.UserApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.BaseFragment;
import com.biaoyixin.shangcheng.login.AccountManager;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-23.
 */
public class ZhanduiFragment extends BaseFragment {


    @BindView(R2.id.avatar)
    ImageView mAvatar;
    @BindView(R2.id.nickname)
    TextView mNickname;
    private UserApi.BrokerReport mData;



    @BindView(R2.id.jin_ri_yong_jin)
    TextView jin_ri_yong_jin;
    @BindView(R2.id.lei_ji_yong_jin)
    TextView lei_ji_yong_jin;
    @BindView(R2.id.qin_bin_dui_total)
    TextView qin_bin_dui_total;
    @BindView(R2.id.hu_wei_dui_total)
    TextView hu_wei_dui_total;
    @BindView(R2.id.q1)
    TextView q1;
    @BindView(R2.id.q2)
    TextView q2;
    @BindView(R2.id.q3)
    TextView q3;
    @BindView(R2.id.q4)
    TextView q4;
    @BindView(R2.id.h1)
    TextView h1;
    @BindView(R2.id.h2)
    TextView h2;
    @BindView(R2.id.h3)
    TextView h3;
    @BindView(R2.id.h4)
    TextView h4;


    @BindView(R2.id.promt)
    TextView mPromt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.zhandui_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ButterKnife.bind(this, view);


        AccountManager.Account account = AccountManager.getAccount();
        Glide.with(this).load(account.avatar).into(mAvatar);

        mNickname.setText(account.nickName);

        loadData();

    }

    private void loadData() {
        final BaseActivity baseActivity = (BaseActivity) getActivity();





        UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
        iUser.brokerReport(new Callback<UserApi.BrokerReportResp>() {
            @Override
            public void success(UserApi.BrokerReportResp resp, Response response) {
                if (resp.code != 0) {

                    ServerAPI.handleCodeError(resp);
                } else {
                    mData = resp.data;
                    updateUI();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);

            }
        });


        iUser.getSystemInfo(new Callback<UserApi.SystemInfoResp>() {
            @Override
            public void success(UserApi.SystemInfoResp resp, Response response) {
                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);
                } else {
                    if (!TextUtils.isEmpty(resp.data.settleTime)) {
                        mPromt.setText("战队佣金每日"+ resp.data.settleTime + "点结算");
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void updateUI() {

        jin_ri_yong_jin.setText(
                deRound(
                        mData.todayBrokerageLv1 + mData.todayBrokerageLv2,
                        100)
        );
        lei_ji_yong_jin.setText(
                deRound(
                        mData.totalBrokerageLv1 + mData.totalBrokerageLv2,
                        100)
        );
        qin_bin_dui_total.setText(
                mData.lv1Count + ""
//                deRound(
//                        mData.todayBrokerageLv1,
//                        100)
        );
        hu_wei_dui_total.setText(
                mData.lv2Count + ""
//                deRound(
//                        mData.todayBrokerageLv2,
//                        100)
        );
        q1.setText(
                deRound(
                        mData.todayTradeMoneyLv1,
                        100)
        );
        q2.setText(
                deRound(
                        mData.totalTradeMoneyLv1,
                        100)
        );
        q3.setText(
                deRound(
                        mData.todayBrokerageLv1,
                        100)
        );
        q4.setText(
                deRound(
                        mData.totalBrokerageLv1,
                        100)
        );
        h1.setText(
                deRound(
                        mData.todayTradeMoneyLv2,
                        100)
        );
        h2.setText(
                deRound(
                        mData.totalTradeMoneyLv2,
                        100)
        );
        h3.setText(
                deRound(
                        mData.todayBrokerageLv2,
                        100)
        );
        h4.setText(
                deRound(
                        mData.totalBrokerageLv2,
                        100)
        );
    }

    public static String deRound(int value, int unit) {
        String ret = deRound(value, unit, 2);
        boolean point = false;
        if(ret.indexOf(".") >= 0) {
            int tail;
            for(tail = ret.length() - 1; ret.charAt(tail) == 48; --tail) {
                ;
            }

            ret = ret.substring(0, tail + 1);
        }

        return ret;
    }

    public static String deRound(int value, int unit, int pres) {
        if(value % unit == 0) {
            return String.valueOf(value / unit);
        } else {
            String format = "%." + pres + "f";
            return String.format(format, new Object[]{Double.valueOf((double)value / (double)unit)});
        }
    }



    @OnClick(R2.id.qin_bin_dui_layout)
    public void clickQinBinDui() {
        Intent it = new Intent(this.getContext(), QinBinDuiActivity.class);
        startActivity(it);
    }


    @OnClick(R2.id.hu_wei_dui_layout)
    public void clickHuWeiDui() {
        Intent it = new Intent(this.getContext(), HuiWeiDuiActivity.class);
        startActivity(it);
    }

}
