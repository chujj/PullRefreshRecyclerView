package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.shangcheng.ShangChengAPI;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.model.BaseModel;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-11-19.
 */
public class OrderActivity extends BaseActivity {
    @BindView(R2.id.all_container)
    View mAllContainer;
    @BindView(R2.id.shouhuo_edit_container)
    View mEditContainer;
    @BindView(R2.id.shouhuo_show_container)
    View mShowContainer;

    @BindView(R2.id.tihuo_name)
    TextView mTihuoName;
    @BindView(R2.id.tihuo_phone)
    TextView mTihuoPhone;
    @BindView(R2.id.tihuo_province)
    TextView mTihuoProvince;
    @BindView(R2.id.tihuo_city)
    TextView mTihuoCity;
    @BindView(R2.id.tihuo_addr)
    TextView mTihuoAdd;



    @BindView(R2.id.shouhuoren)
    TextView mShouhuoren;
    @BindView(R2.id.shouhuodizhi)
    TextView mShouhuodizhi;
    @BindView(R2.id.image)
    ImageView mImage;
    @BindView(R2.id.name)
    TextView mName;
    @BindView(R2.id.price)
    TextView mPrice;
//@BindView(R2.id.sku_down)
//TextView mSkudown;

//R2.id.sku_num
//R2.id.sku_up
//R2.id.item_price_total
//R2.id.yunfei
//R2.id.youhuiquan
//R2.id.price_total
//R2.id.confirm


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.order_activity);
        ButterKnife.bind(this, this);

        loadData();
    }

    private void loadData() {
        ShangChengAPI.IShangCheng iShangCheng = ServerAPI.getInterface(ShangChengAPI.IShangCheng.class);
        iShangCheng.order(getIntent().getIntExtra("goodsDetailsId", 0), new Callback<ShangChengAPI.OrderResp>() {
            @Override
            public void success(ShangChengAPI.OrderResp resp, Response response) {
                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);
                } else {
                    refreshUI(resp);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void refreshUI(ShangChengAPI.OrderResp resp) {
        mAllContainer.setVisibility(View.VISIBLE);

        boolean hasAddr = resp.data.address != null;
        mEditContainer.setVisibility(hasAddr ? View.GONE : View.VISIBLE);
        mShowContainer.setVisibility(hasAddr ? View.VISIBLE : View.GONE);

        mShowContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean show =mShowContainer.getVisibility() == View.VISIBLE;

                mEditContainer.setVisibility(show ? View.VISIBLE : View.GONE);
                mShowContainer.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        if (hasAddr) {
            mTihuoName.setText(resp.data.address.name);
            mTihuoPhone.setText(resp.data.address.mobile);
            mTihuoProvince.setText(resp.data.address.province);
            mTihuoCity.setText(resp.data.address.city);
            mTihuoAdd.setText(resp.data.address.detail);

            mShouhuoren.setText(resp.data.address.name);
            mShouhuodizhi.setText(resp.data.address.detail);
        }


        Glide.with(this).load(resp.data.icon).into(mImage);
        mName.setText(resp.data.name);
        mPrice.setText("￥" + resp.data.realPrice);
    }




    @OnClick(R2.id.tihuo_confirm)
    public void clickTihuoConfirm(View view) {
        final String province = mTihuoProvince.getText().toString();
        final String city = mTihuoCity.getText().toString();
        final String name = mTihuoName.getText().toString();
        final String phone = mTihuoPhone.getText().toString();
        final String addr = mTihuoAdd.getText().toString();


        ShangChengAPI.IShangCheng iShangCheng = ServerAPI.getInterface(ShangChengAPI.IShangCheng.class);
        iShangCheng.changeTihuoAddr(
                province,
                city,
                name,
                phone,
                addr,
                new Callback<BaseModel>() {
                    @Override
                    public void success(BaseModel resp, Response response) {
                        if (resp.code != 0) {
                            ServerAPI.handleCodeError(resp);
                        } else {

                            mShouhuoren.setText(name);
                            mShouhuodizhi.setText(addr);

                            boolean show = false;
                            mEditContainer.setVisibility(show ? View.VISIBLE : View.GONE);
                            mShowContainer.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ServerAPI.HandlerException(error);
                    }
                }
        );
    }
}
