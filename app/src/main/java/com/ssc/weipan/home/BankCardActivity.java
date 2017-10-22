package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.Topbar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhujj on 17-10-22.
 */
public class BankCardActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setContentView(R.layout.bankcard_activity);

        ButterKnife.bind(this, this);

        mTopbar.setTitle("银行卡");
    }
}
