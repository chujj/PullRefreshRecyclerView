package com.biaoyixin.shangcheng.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.shangcheng.ShangChengAPI;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.ClosureMethod;
import com.biaoyixin.shangcheng.base.ToastHelper;
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
    @BindView(R2.id.sku_num)
    TextView mSKUNum;

    @BindView(R2.id.item_price_total)
    TextView mItemPriceTotal;
    @BindView(R2.id.yunfei)
    TextView mYunFei;
    @BindView(R2.id.youhuiquan)
    TextView mYouhuiquan;
    @BindView(R2.id.price_total)
    TextView mPriceTotal;

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

    private void refreshUI(final ShangChengAPI.OrderResp resp) {
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



        mSKUNum.setText("1");
        mSKUNum.setTag(new ClosureMethod() {
            int mCount = 1;
            @Override
            public Object[] run(Object... args) {
                int change = (int) args[0];
                int next = mCount + change;
                if (next <= 0) {
                    return null;
                }


                mCount = next;

                mSKUNum.setText("" + mCount);
                mItemPriceTotal.setText("￥" + mCount * resp.data.realPrice);


                mPriceTotal.setText("￥" + (mCount * resp.data.realPrice + resp.data.deliverPrice - resp.data.credit));
                return null;
            }
        });
        ((ClosureMethod)mSKUNum.getTag()).run(0);


        mYunFei.setText("￥" + resp.data.deliverPrice);
        if (resp.data.credit == 0) {
            mYouhuiquan.setText("无");
        } else {
            mYouhuiquan.setText("- ￥" + resp.data.credit);
        }

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


    @OnClick(R2.id.sku_down)
    public void clickSKUDown() {
        ((ClosureMethod)mSKUNum.getTag()).run(-1);
    }

    @OnClick(R2.id.sku_up)
    public void clickSKUUp() {
        ((ClosureMethod)mSKUNum.getTag()).run(1);
    }


    @OnClick(R2.id.confirm)
    public void clickConfirm() {
        String amount = mSKUNum.getText().toString();
        String goodsDetailsId = getIntent().getIntExtra("goodsDetailsId", 0) + "";


        ShangChengAPI.IShangCheng iShangCheng = ServerAPI.getInterface(ShangChengAPI.IShangCheng.class);
        iShangCheng.tihuo(goodsDetailsId, amount, new Callback<BaseModel>() {
            @Override
            public void success(BaseModel resp, Response response) {
                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);
                } else {
                    if (!TextUtils.isEmpty(resp.message)) {
                        ToastHelper.showToast(resp.message);
                    }

                    Intent it = new Intent(OrderActivity.this, HomeActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(it);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });
    }

}

