package com.biaoyixin.shangcheng.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.shangcheng.ShangChengAPI;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R2.id.images_container)
    ViewGroup mImagesContainer;
    @BindView(R2.id.bottom_bar)
    ViewGroup mBottomBar;

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


        final int screenWidth = getResources().getDisplayMetrics().widthPixels;

        for (int i = 0; i < data.images.size(); i++) {
            final ImageView imageView = new ImageView(this);
            imageView.setBackgroundColor(0xffff0000);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            Glide.with(this).load(data.images.get(i)).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    int width = resource.getIntrinsicWidth();
                    int height = resource.getIntrinsicHeight();

                    int computedHeight = (int) (1f * height / width * screenWidth);
                    imageView.getLayoutParams().height = computedHeight;

                    return false;
                }
            }).into(imageView);

            mImagesContainer.addView(imageView, lp);
        }


        // bottom bar

        mBottomBar.setVisibility(View.VISIBLE);

    }



    @OnClick(R2.id.shangcheng)
    public void clickShangCheng(View view) {
        finish();
    }

    @OnClick(R2.id.tihuo)
    public void clickTihuo(View view) {
//        finish();
        Intent it = new Intent(this, HomeActivity.class);
        it.putExtra("index", 2);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(it);
    }

    @OnClick(R2.id.buy)
    public void clickBuy(View view) {
        Intent it = new Intent(this, OrderActivity.class);
        startActivity(it);
    }
}
