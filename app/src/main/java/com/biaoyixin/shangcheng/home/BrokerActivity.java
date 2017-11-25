package com.biaoyixin.shangcheng.home;

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

        ButterKnife.bind(this, this);

        Glide.with(this).load(data.headPortrait).into(image);


        name.setText(data.name);
        id_num.setText("经纪人编号：" + data.id);

        fanyong.setText("" + data.totalServeMoney);
        jiesuan_shouru.setText("" + data.settledServeMoney);
        weijiesuan_shouru.setText("" + data.unsettledServeMoney);
    }


    @OnClick(R2.id.zhishu_click)
    public void clickZhiShu() {

    }

    @OnClick(R2.id.qrcode_click)
    public void clickQrcode() {

    }

    @OnClick(R2.id.fanyong_click)
    public void clickFanyong() {

    }


}
