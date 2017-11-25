package com.biaoyixin.shangcheng.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.biaoyixin.shangcheng.api.login.LoginApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.FragmentUtils;
import com.google.gson.Gson;

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

    public void switchToStep3Recommend(Runnable callback) {
        RecommendInputActivity.successCb = callback;
        Intent it = new Intent(this, RecommendInputActivity.class);
        startActivity(it);
        finish();
    }

    public void switchToStep4JigouSelector(LoginApi.PreLoginInfo saveAccount) {
        Intent it = new Intent(this, AccountSelecterActivity.class);
        it.putExtra("accouts", new Gson().toJson(saveAccount.customers));
        startActivity(it);
    }

}
