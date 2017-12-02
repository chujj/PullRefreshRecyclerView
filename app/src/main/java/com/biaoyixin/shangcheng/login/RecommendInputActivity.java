package com.biaoyixin.shangcheng.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.SplashActivity;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.user.UserApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.biaoyixin.shangcheng.base.ToastHelper;
import com.biaoyixin.shangcheng.base.Topbar;
import com.biaoyixin.shangcheng.home.SettingActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-17.
 */
public class RecommendInputActivity extends BaseActivity {


    public static Runnable successCb;
    @BindView(R2.id.phone)
    EditText mPhone;

    boolean success = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.recommend_input_activity);

        ButterKnife.bind(this, this);

        Topbar topbar = CommonUtils.findView(this, R.id.topbar);
        topbar.setTitle("填写推荐码");
    }

    @OnClick(R2.id.ignore)
    public void clickIgoner() {

        finish();
        openHome();
    }


    @OnClick(R2.id.next)
    public void clickNext() {
        String recommend = mPhone.getText().toString().trim();
        if (TextUtils.isEmpty(recommend)) {
            ToastHelper.showToast("请输入推荐码");
            return;
        }


        showLoadingDialog("加载中...", true);
        UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
        iUser.setBroker(recommend, new Callback<UserApi.SetBrokerResp>() {
            @Override
            public void success(UserApi.SetBrokerResp baseModel, Response response) {

                dismissLoadingDialog();

                if (baseModel.code != 0) {

                    ServerAPI.handleCodeError(baseModel);
                } else {
                    if (successCb != null) {
                        successCb.run();
                    }

                    final AccountManager.Account account = AccountManager.getAccount();
                    account.id = baseModel.data.id + "";
                    AccountManager.saveAccount(account);

                    success = true;
                    finish();
                    openHome();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
                dismissLoadingDialog();
            }
        });


    }

    @Override

    protected void onDestroy() {
        if (!success) {
            SettingActivity.clearAccount();
        }
        super.onDestroy();
    }

    private long mLastClick;
    @Override
    public void onBackPressed() {

        long current = System.currentTimeMillis();
        if (current - mLastClick > 1000) {
            mLastClick = current;
            ToastHelper.showToast("请输入推荐码后继续使用");
            return;
        }

        super.onBackPressed();


        openHome();
    }



    public void openHome() {
        SplashActivity.switchToMain(this);
    }
}
