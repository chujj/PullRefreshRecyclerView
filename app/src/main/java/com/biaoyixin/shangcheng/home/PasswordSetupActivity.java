package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.user.UserApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.ToastHelper;
import com.biaoyixin.shangcheng.base.Topbar;
import com.biaoyixin.shangcheng.model.BaseModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-21.
 */
public class PasswordSetupActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;

    @BindView(R2.id.ok)
    TextView mOk;

    @BindView(R2.id.pwd)
    EditText mPwd;
    @BindView(R2.id.pwd_confirm)
    EditText mPwdConfirm;


    @BindView(R2.id.orig_pwd_layout)
    View mOriginPwdLayout;
    @BindView(R2.id.orig_pwd)
    EditText mOriPwd;

    private boolean mResetPwdMode;
    private boolean mForgetPwdMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.trade_password_setup_activity);


        mResetPwdMode = getIntent().getBooleanExtra("reset_pwd", false);
        mForgetPwdMode = getIntent().getBooleanExtra("forget_pwd", false);

        ButterKnife.bind(this, this);

        mTopbar.setTitle(mResetPwdMode ? "修改交易密码" : "设置交易密码");

        mOriginPwdLayout.setVisibility(mResetPwdMode ? View.VISIBLE : View.GONE);

        mOk.setEnabled(false);


        if (mResetPwdMode) {
            mOriPwd.setFilters(new InputFilter[] {
                    new InputFilter.LengthFilter(12),
            });
        }


        mPwd.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(12),
        });
        mPwdConfirm.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(12),
        });


        mPwdConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pwd = mPwd.getText().toString().trim();
                String pwd_c = s.toString().trim();

                if (!TextUtils.isEmpty(pwd) && pwd.length() >= 6 &&
                        !TextUtils.isEmpty(pwd_c) && pwd_c.length() >= 6&&
                        TextUtils.equals(pwd, pwd_c)) {

                    if (mResetPwdMode) {
                        String pwd_orig = mOriPwd.getText().toString().trim();
                        if (!TextUtils.isEmpty(pwd_orig) && pwd_orig.length() >= 6) {
                            mOk.setEnabled(true);
                        } else {
                            mOk.setEnabled(false);
                        }
                    } else {
                        mOk.setEnabled(true);
                    }


                } else {
                    mOk.setEnabled(false);
                }
            }
        });
    }


    @OnClick(R2.id.ok)
    public void clickOk() {
        if (mResetPwdMode) {
            String pwd = mPwdConfirm.getText().toString().trim();
            String pwd_orig = mOriPwd.getText().toString().trim();


            showLoadingDialog("加载中", false);

            UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
            iUser.modifyPassword(pwd_orig, pwd, new Callback<BaseModel>() {
                @Override
                public void success(BaseModel baseModel, Response response) {
                    dismissLoadingDialog();
                    if (baseModel.code != 0) {
                        ToastHelper.showToast(baseModel.message);
                    } else {
                        ToastHelper.showToast("修改成功");
                        finish();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    dismissLoadingDialog();
                    ServerAPI.HandlerException(error);
                }
            });

        } else {
            String pwd = mPwdConfirm.getText().toString();


            showLoadingDialog("加载中", false);

            if (mForgetPwdMode) {
                UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
                iUser.forgetPassword(pwd, new Callback<BaseModel>() {
                    @Override
                    public void success(BaseModel baseModel, Response response) {
                        dismissLoadingDialog();
                        if (baseModel.code != 0) {
                            ToastHelper.showToast(baseModel.message);
                        } else {
                            ToastHelper.showToast("设置成功");
                            finish();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dismissLoadingDialog();
                        ServerAPI.HandlerException(error);
                    }
                });
            } else {
                UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
                iUser.initPassword(pwd, new Callback<BaseModel>() {
                    @Override
                    public void success(BaseModel baseModel, Response response) {
                        dismissLoadingDialog();
                        if (baseModel.code != 0) {
                            ToastHelper.showToast(baseModel.message);
                        } else {
                            ToastHelper.showToast("设置成功");
                            finish();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        dismissLoadingDialog();
                        ServerAPI.HandlerException(error);
                    }
                });
            }


        }



    }
}
