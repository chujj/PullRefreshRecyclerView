package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.trade.GoodsApi;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.ToastHelper;
import com.ssc.weipan.base.Topbar;

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

    private List<GoodsApi.City> mCitys;
    private GoodsApi.OutMoneyUIInfo mUIInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.tixian_activity);

        ButterKnife.bind(this, this);

        mTopbar.setTitle("提现");

        loadData();
    }

    private void loadData() {

        final Runnable initUI = new Runnable() {
            int success_count = 0;
            @Override
            public void run() {
                success_count ++;

                if (success_count != 2) {
                    return;
                }

                dismissLoadingDialog();




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

    }
}
