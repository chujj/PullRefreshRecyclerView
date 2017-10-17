package com.ssc.weipan.login;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ssc.weipan.R;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.Topbar;

import butterknife.ButterKnife;

/**
 * Created by zhujj on 17-10-17.
 */
public class RecommendInputActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.recommend_input_activity);

        ButterKnife.bind(this, this);

        Topbar topbar = CommonUtils.findView(this, R.id.topbar);
        topbar.setTitle("填写推荐码");



    }


}
