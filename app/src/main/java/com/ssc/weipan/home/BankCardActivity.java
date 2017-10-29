package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.trade.GoodsApi;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.ToastHelper;
import com.ssc.weipan.base.Topbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-22.
 */
public class BankCardActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;


    @BindView(R2.id.bankname)
    TextView mBankName;
    @BindView(R2.id.bank_account)
    TextView mBankAccount;

    @BindView(R2.id.card_owner)
    TextView mCardOwner;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setContentView(R.layout.bankcard_activity);

        ButterKnife.bind(this, this);

        mTopbar.setTitle("银行卡");


        loadData();
    }

    private void loadData() {
        showLoadingDialog("加载中...", false);
        GoodsApi.IGood iGood = ServerAPI.getInterface(GoodsApi.IGood.class);
        iGood.getOutMoneyUIInfo(new Callback<GoodsApi.OutMoneyUIInfoResp>() {
            @Override
            public void success(GoodsApi.OutMoneyUIInfoResp resp, Response response) {
                dismissLoadingDialog();

                if (resp.code != 0) {
                    ToastHelper.showToast(resp.message);
                    ServerAPI.handleCodeError(resp);
                } else {
                    mBankName.setText(resp.data.bank_name);
                    mBankAccount.setText(resp.data.bank_account);
                    mCardOwner.setText("持卡人  " + resp.data.realname);
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
