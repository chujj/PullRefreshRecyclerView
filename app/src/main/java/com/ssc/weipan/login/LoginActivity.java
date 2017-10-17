package com.ssc.weipan.login;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.FragmentUtils;

/**
 * Created by zhujj on 17-10-16.
 */
public class LoginActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentUtils.setActivityFragmentsHolderContent(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }

        FragmentUtils.switchFragment(
                getSupportFragmentManager(),
                this,
                Step1PhoneNumInputFragment.class.getName(), bundle);

    }


}
