package com.biaoyixin.shangcheng.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.SplashActivity;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.login.LoginApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.biaoyixin.shangcheng.base.Topbar;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-11-25.
 */
public class AccountSelecterActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;

    @BindView(R2.id.container)
    ViewGroup mContainer;

    List<LoginApi.LoginData> mData = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String accounts = getIntent().getStringExtra("accouts");
        mData =
                new Gson().fromJson(accounts, new TypeToken<List<LoginApi.LoginData> >() {}.getType());



        this.setContentView(R.layout.account_selecter_activity);

        ButterKnife.bind(this, this);


        mTopbar.setTitle("选择机构");


        for (int i = 0; i < mData.size(); i+=2) {

            View root = LayoutInflater.from(this).inflate(R.layout.jigou_line, mContainer, false);
            View[] items = new View[] {
                    CommonUtils.findView(root, R.id.left),
                    CommonUtils.findView(root, R.id.right),
            };


            for (int j = 0; j < 2; j++) {
                if ((i + j) > (mData.size() - 1)) {
                    items[j].setVisibility(View.INVISIBLE);
                } else {
                    items[j].setVisibility(View.VISIBLE);
                }


                ImageView image = CommonUtils.findView(items[j], R.id.image);
                TextView l1 = CommonUtils.findView(items[j], R.id.l1);
                TextView l2 = CommonUtils.findView(items[j], R.id.l2);
                TextView l3 = CommonUtils.findView(items[j], R.id.l3);

                final LoginApi.LoginData data = mData.get(i+j);
                Glide.with(this).load(data.headPortrait).into(image);
                l1.setText(data.orgCode);
                l2.setText(data.nickname);
                l3.setText(String.format("资金%.2f元",  (data.asset  / 100f)));


                items[j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        login(data);
                    }
                });
            }


            mContainer.addView(root);
            
        }
    }

    private void login(LoginApi.LoginData data) {
        LoginApi.ILogin iLogin = ServerAPI.getInterface(LoginApi.ILogin.class);
        iLogin .loginDangdang(data.id + "", new Callback<LoginApi.LoginResp>() {
            @Override
            public void success(LoginApi.LoginResp resp, Response response) {
                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);
                } else {
                    final AccountManager.Account account = AccountManager.getAccount();
                    account.id = resp.data.id + "";
                    AccountManager.saveAccount(account);

                    SplashActivity.switchToMain(AccountSelecterActivity.this);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });
    }
}
