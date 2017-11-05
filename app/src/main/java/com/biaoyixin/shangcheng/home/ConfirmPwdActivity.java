package com.biaoyixin.shangcheng.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.sms.SmsApi;
import com.biaoyixin.shangcheng.api.user.UserApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.ToastHelper;
import com.biaoyixin.shangcheng.login.LoginActivity;
import com.biaoyixin.shangcheng.model.BaseModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-22.
 */
public class ConfirmPwdActivity extends BaseActivity {


    @BindView(R2.id.pwd)
    EditText mPwd;
    @BindView(R2.id.ok)
    View mOkBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.confirm_pwd_activity);


        ButterKnife.bind(this, this);

        mPwd.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(12),
        });
        mPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pwd = mPwd.getText().toString().trim();

                if (!TextUtils.isEmpty(pwd) && pwd.length() >= 6) {
                    mOkBtn.setEnabled(true);
                } else {
                    mOkBtn.setEnabled(false);
                }
            }
        });


        mOkBtn.setEnabled(false);
    }


    @OnClick(R2.id.ok)
    public void clickOk() {
        String pwd = mPwd.getText().toString();

        UserApi.IUser iuser = ServerAPI.getInterface(UserApi.IUser.class);
        iuser.authPawd(pwd, new Callback<BaseModel>() {
            @Override
            public void success(BaseModel baseModel, Response response) {
                if (baseModel.code != 0) {

                    ServerAPI.handleCodeError(baseModel);
                } else {
                    ToastHelper.showToast("验证成功");
                    ConfirmPwdActivity.this.finish();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });

    }


    @OnClick(R2.id.forget_pwd)
    public void clickForgetPwd() {

        requireSMSCode(this, new Runnable() {
            @Override
            public void run() {
                Intent it = new Intent(ConfirmPwdActivity.this, LoginActivity.class);
                it.putExtra("forget_pwd", true);
                ConfirmPwdActivity.this.startActivity(it);
            }
        });
    }



    public static void requireSMSCode(final BaseActivity baseActivity, final Runnable successCB) {
        baseActivity.showLoadingDialog("加载中", true);

        SmsApi.ISMS iSms = ServerAPI.getInterface(SmsApi.ISMS.class);
        iSms.requereSMSCode2("5", new Callback<BaseModel>() {
            @Override
            public void success(BaseModel baseModel, Response response) {
                dissmiss();

                if (baseModel.code == 0) {
//                    // success
                    if (successCB != null) {
                        successCB.run();
                    }
//                    (this).switchToStep2SMSCode(phoneNum);
                } else {
                    ToastHelper.showToast(baseModel.message);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
                dissmiss();
            }

            private void dissmiss() {
                baseActivity.dismissLoadingDialog();
            }
        });
    }
}
