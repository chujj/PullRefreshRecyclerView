package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.broker.BrokerApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-11-25.
 */
public class QRCodeDisplayActivity extends BaseActivity {


    @BindView(R2.id.image)
    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.qrcode_display);

        ButterKnife.bind(this, this);

        loadData();
    }

    private void loadData() {
        BrokerApi.IBroker iBroker = ServerAPI.getInterface(BrokerApi.IBroker.class);
        iBroker.getQRCode(new Callback<BrokerApi.QRCodeResp>() {
            @Override
            public void success(BrokerApi.QRCodeResp resp, Response response) {
                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);
                } else {
                    Glide.with(QRCodeDisplayActivity.this).load(resp.data).into(imageView);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });
    }


}
