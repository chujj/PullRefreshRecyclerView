package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhujj on 17-10-24.
 */
public class TuijianmaActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setContentView(R.layout.tuijianma_layout);

        ButterKnife.bind(this, this);
    }


    @OnClick(R2.id.container)
    public void clickBG() {

    }
    @OnClick(R2.id.ok)
    public void clickOK() {
        this.finish();
    }
}
