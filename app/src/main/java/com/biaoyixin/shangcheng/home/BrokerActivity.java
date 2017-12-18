package com.biaoyixin.shangcheng.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.broker.BrokerApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.Topbar;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-11-25.
 */
public class BrokerActivity extends BaseActivity {

    @BindView(R2.id.image)
    ImageView image;
    @BindView(R2.id.name)
    TextView name;
    @BindView(R2.id.id_num)
    TextView id_num;


    @BindView(R2.id.fanyong)
    TextView fanyong;
    @BindView(R2.id.jiesuan_shouru)
    TextView jiesuan_shouru;
    @BindView(R2.id.weijiesuan_shouru)
    TextView weijiesuan_shouru;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setContentView(R.layout.broker_activity);

        Topbar topbar = (Topbar) findViewById(R.id.topbar);
        topbar.setTitle("经纪人");
        topbar.setBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadData();
    }

    private void loadData() {
        BrokerApi.IBroker iBroker = ServerAPI.getInterface(BrokerApi.IBroker.class);
        iBroker.getBroker(new Callback<BrokerApi.BrokerResp>() {
            @Override
            public void success(BrokerApi.BrokerResp resp, Response response) {
                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);
                } else {
                    refreshUI(resp.data);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });

    }

    private void refreshUI(BrokerApi.Broker data) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && BrokerActivity.this.isDestroyed()) {
            return;
        }

        ButterKnife.bind(this, this);

        Glide.with(this).load(data.headPortrait).into(image);


        name.setText(data.name);
        id_num.setText("经纪人编号：" + data.id);

        fanyong.setText(String.format("%.2f", data.totalServeMoney / 100f));
        jiesuan_shouru.setText(String.format("%.2f", data.settledServeMoney / 100f));
        weijiesuan_shouru.setText(String.format("%.2f", data.unsettledServeMoney / 100f));
    }


    @OnClick(R2.id.zhishu_click)
    public void clickZhiShu() {
        Intent it = new Intent(this, ZhiShuActivity.class);
        startActivity(it);

    }

    @OnClick(R2.id.qrcode_click)
    public void clickQrcode() {
        Intent it = new Intent(this, QRCodeDisplayActivity.class);
        startActivity(it);
    }

    @OnClick(R2.id.fanyong_click)
    public void clickFanyong() {
        Intent it = new Intent(this, FanyongActivity.class);
        it.putExtra("price", fanyong.getText().toString() + "元");
        startActivity(it);
    }


}
