package com.ssc.weipan.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.EditText;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.SplashActivity;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.user.UserApi;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.ToastHelper;
import com.ssc.weipan.base.Topbar;
import com.ssc.weipan.model.BaseModel;

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



    @BindView(R2.id.phone)
    EditText mPhone;

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
        iUser.setBroker(recommend, new Callback<BaseModel>() {
            @Override
            public void success(BaseModel baseModel, Response response) {

                dismissLoadingDialog();

                if (baseModel.code != 0) {
                    ToastHelper.showToast(baseModel.message);
                    ServerAPI.handleCodeError(baseModel);
                } else {
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
    public void onBackPressed() {
        super.onBackPressed();

        openHome();
    }



    public void openHome() {
        SplashActivity.switchToMain(this);
    }
}
