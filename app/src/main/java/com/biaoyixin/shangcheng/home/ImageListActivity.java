package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.Topbar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhujj on 17-11-19.
 */
public class ImageListActivity extends BaseActivity {


    @BindView(R2.id.topbar)
    Topbar mTopbar;

    @BindView(R2.id.images_container)
    ViewGroup mImagesContainer;

    private String[] mImages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImages = getIntent().getStringArrayExtra("images");


        this.setContentView(R.layout.image_list_activity);

        ButterKnife.bind(this, this);
    }
}
