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
import com.biaoyixin.shangcheng.base.BaseFragment;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-11-18.
 */
public class ShangChengFragment extends BaseFragment {



    @BindView(R2.id.container)
    ViewGroup mContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trade_shangcheng_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ButterKnife.bind(this, view);

        loadShangCheng();
    }

    private void loadShangCheng() {
        ShangChengAPI.IShangCheng iShangCheng = ServerAPI.getInterface(ShangChengAPI.IShangCheng.class);
        iShangCheng.index(new Callback<ShangChengAPI.ShangChengResp>() {
            @Override
            public void success(ShangChengAPI.ShangChengResp resp, Response response) {
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

    private void refreshUI(ShangChengAPI.ShangChengInfo data) {
        LayoutInflater inflator = LayoutInflater.from(this.getContext());

        { // banner
            if (data.banner != null) {
                View banner = inflator.inflate(R.layout.shangcheng_index_banner, mContainer, false );
                float computedHeight = 1f * 400 / 750 * getContext().getResources().getDisplayMetrics().widthPixels;
                banner.getLayoutParams().height = (int) computedHeight;
                Glide.with(ShangChengFragment.this).load(data.banner.icon).into(
                        (ImageView)CommonUtils.findView(banner, R.id.img));

                mContainer.addView(banner);
            }
        }


        { // category
            if (data.category != null && data.category.size() > 0) {

                View line = inflator.inflate(R.layout.shangcheng_index_category_line, mContainer, false);
                ImageView[] cates = new ImageView[] {
                        CommonUtils.findView(line, R.id.img1),
                        CommonUtils.findView(line, R.id.img2),
                        CommonUtils.findView(line, R.id.img3),
                };

                int width = getContext().getResources().getDisplayMetrics().widthPixels;

                int computedWidth = (width - (CommonUtils.dip2px(getContext(), 5f))) / 3;

                for (int i = 0; i < 3; i++) {
                    ViewGroup.LayoutParams lp = cates[i].getLayoutParams();
                    lp.width = lp.height = computedWidth;
                }


                for (int i = 0; i < 3; i++) {
                    if (i >= data.category.size()) {
                        continue;
                    }


                    Glide.with(this).load(data.category.get(i).icon).into(cates[i]);
                }

                mContainer.addView(line);
            }
        }


        {// items
            if (data.recommendList != null && data.recommendList.size() > 0) {
                inflator.inflate(R.layout.recommend_items_header, mContainer, true);

                for (int i = 0; i < data.recommendList.size(); i+=2) {
                    View line = inflator.inflate(R.layout.recommend_items_line, mContainer, false);
                    View[] items = new View[] {
                            CommonUtils.findView(line, R.id.left),
                            CommonUtils.findView(line, R.id.right),
                    };

                    for (int j = 0; j < 2; j++) {
                        if ((i + j) >= data.recommendList.size()) {
                            continue;
                        }

                        ImageView img = CommonUtils.findView(items[j], R.id.img);
                        img.getLayoutParams().width = img.getLayoutParams().height =
                                (getResources().getDisplayMetrics().widthPixels / 2 - CommonUtils.dip2px(getContext(), 15f) * 2);

                        TextView name = CommonUtils.findView(items[j], R.id.name);
                        TextView price = CommonUtils.findView(items[j], R.id.price);


                        ShangChengAPI.Item item = data.recommendList.get(i + j);
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
