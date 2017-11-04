package com.biaoyixin.shangcheng.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.account.AccountHelper;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.BaseApp;
import com.biaoyixin.shangcheng.base.PreferencesUtil;
import com.biaoyixin.shangcheng.base.Topbar;
import com.biaoyixin.shangcheng.login.AccountManager;
import com.biaoyixin.shangcheng.login.LoginActivity;
import com.google.gson.Gson;

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
        mTopbar.setBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @OnClick(R2.id.change_pwd)
    public void clickChangePwd() {
        Intent it = new Intent(this, PasswordSetupActivity.class);
        it.putExtra("reset_pwd", true);
        this.startActivity(it);
    }


    @OnClick(R2.id.logout)
    public void clickLogout() {

        final Runnable callRunnable = new Runnable() {
            @Override
            public void run() {

                AccountHelper.getCookieStore().removeAll();

                PreferencesUtil.putString(BaseApp.getApp(), AccountManager.PREF_USER_ID, "");

                AccountManager.Account account = new Gson().fromJson("{}", AccountManager.Account.class);
                AccountManager.saveAccount(account);

                Intent it = new Intent(BaseApp.getApp(), LoginActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);

            }
        };


        new AlertDialog.Builder(this).setTitle("提示").setMessage("确认退出登录")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callRunnable.run();
                    }
                }).create().show();

    }
}
