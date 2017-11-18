package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.shangcheng.ShangChengAPI;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.biaoyixin.shangcheng.base.Topbar;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-11-18.
 */
public class ShangChengCategoryActivity extends BaseActivity {



    @BindView(R2.id.topbar)
    Topbar mTopbar;

    @BindView(R2.id.container)
    ViewGroup mContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setContentView(R.layout.trade_shangcheng_category_fragment);

        ButterKnife.bind(this, this);

        mTopbar.setBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadData();
    }

    private void loadData() {
        ShangChengAPI.IShangCheng iShangCheng = ServerAPI.getInterface(ShangChengAPI.IShangCheng.class);
        iShangCheng.category(getIntent().getIntExtra("categoryId", 0), new Callback<ShangChengAPI.CategoryResp>() {
            @Override
            public void success(ShangChengAPI.CategoryResp resp, Response response) {
                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);
                } else {
                    initUI(resp.data);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });

    }

    private void initUI(ShangChengAPI.CategoryInfo data) {

        mTopbar.setTitle(data.title);

        LayoutInflater inflator = LayoutInflater.from(this);

        { // banner

                View banner = inflator.inflate(R.layout.shangcheng_index_banner, mContainer, false );
//                float computedHeight = 1f * 400 / 750 * getResources().getDisplayMetrics().widthPixels;
//                banner.getLayoutParams().height = (int) computedHeight;
                Glide.with(this).load(data.image).into(
                        (ImageView)CommonUtils.findView(banner, R.id.img));

                mContainer.addView(banner);

        }

        {// items
            if (data.list != null && data.list.size() > 0) {
                inflator.inflate(R.layout.recommend_items_header, mContainer, true);

                for (int i = 0; i < data.list.size(); i+=2) {
                    View line = inflator.inflate(R.layout.recommend_items_line, mContainer, false);
                    View[] items = new View[] {
                            CommonUtils.findView(line, R.id.left),
                            CommonUtils.findView(line, R.id.right),
                    };

                    for (int j = 0; j < 2; j++) {
                        if ((i + j) >= data.list.size()) {
                            continue;
                        }

                        ImageView img = CommonUtils.findView(items[j], R.id.img);
                        img.getLayoutParams().width = img.getLayoutParams().height =
                                (getResources().getDisplayMetrics().widthPixels / 2 - CommonUtils.dip2px(this, 15f) * 2);

                        TextView name = CommonUtils.findView(items[j], R.id.name);
                        TextView price = CommonUtils.findView(items[j], R.id.price);


                        ShangChengAPI.Item item = data.list.get(i + j);
                        Glide.with(this).load(item.icon).into(img);
                        name.setText(item.name);
                        price.setText("¥" + (int) item.realPrice);

                    }
                    mContainer.addView(line);
                }

            }

        }
    }
}
