package com.ssc.weipan.login;

import android.content.Intent;
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


        if (getIntent().getBooleanExtra("forget_pwd", false)) {
            switchToStep2SMSCode("");
        } else {
            FragmentUtils.switchFragment(
                    getSupportFragmentManager(),
                    this,
                    Step1PhoneNumInputFragment.class.getName(), bundle);
        }
    }


    public void switchToStep2SMSCode(String phoneNum) {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }

        bundle.putString("phone_num", phoneNum);

        FragmentUtils.switchFragment(
                getSupportFragmentManager(),
                this,
                Step2SMSCodeFragment.class.getName(), bundle, true);
    }

    public void switchToStep3Recommend() {
        Intent it = new Intent(this, RecommendInputActivity.class);
        startActivity(it);
        finish();
    }
}
