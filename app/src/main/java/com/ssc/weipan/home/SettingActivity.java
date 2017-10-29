package com.ssc.weipan.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.BaseApp;
import com.ssc.weipan.base.PreferencesUtil;
import com.ssc.weipan.base.Topbar;
import com.ssc.weipan.login.AccountManager;
import com.ssc.weipan.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhujj on 17-10-23.
 */
public class SettingActivity extends BaseActivity {


    @BindView(R2.id.topbar)
    Topbar mTopbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setContentView(R.layout.settings_layout);
        ButterKnife.bind(this, this);


        mTopbar.setTitle("个人设置");
    }


    @OnClick(R2.id.change_pwd)
    public void clickChangePwd() {
        Intent it = new Intent(this, PasswordSetupActivity.class);
        it.putExtra("reset_pwd", true);
        this.startActivity(it);
    }


    @OnClick(R2.id.logout)
    public void clickLogout() {

        PreferencesUtil.putString(BaseApp.getApp(), AccountManager.PREF_USER_ID, "");

        AccountManager.Account account = new Gson().fromJson("{}", AccountManager.Account.class);
        AccountManager.saveAccount(account);

        Intent it = new Intent(BaseApp.getApp(), LoginActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(it);
    }
}
