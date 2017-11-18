package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.shangcheng.ShangChengAPI;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-11-18.
 */
public class ItemDetailActivity extends BaseActivity {




    @BindView(R2.id.img)
    ImageView mImage;
    @BindView(R2.id.name)
    TextView mName;
    @BindView(R2.id.price)
    TextView mPrice;
    @BindView(R2.id.diliver)
    TextView mDiliver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.item_detail_layout);

        ButterKnife.bind(this, this);


        loadData();
    }

    private void loadData() {
        ShangChengAPI.IShangCheng iShangCheng = ServerAPI.getInterface(ShangChengAPI.IShangCheng.class);
        iShangCheng.detail(getIntent().getIntExtra("goodsDetailsId", 0), new Callback<ShangChengAPI.DetailResp>() {
            @Override
            public void success(ShangChengAPI.DetailResp resp, Response response) {
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

    private void refreshUI(ShangChengAPI.Item data) {

        Glide.with(this).load(data.icon).into(mImage);
        mName.setText(data.name);
        mPrice.setText(String.format("¥ %.2f", data.realPrice));
        mDiliver.setText(String.format("快递费： ¥%.2f", data.deliverPrice));


    }
}
