package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.user.UserApi;
import com.ssc.weipan.base.BaseActivity;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.trade_password_setup_activity);

        ButterKnife.bind(this, this);

        mTopbar.setTitle("设置交易密码");

        mOk.setEnabled(false);


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
                    mOk.setEnabled(true);
                } else {
                    mOk.setEnabled(false);
                }
            }
        });
    }


    @OnClick(R2.id.ok)
    public void clickOk() {
        String pwd = mPwdConfirm.getText().toString();


        showLoadingDialog("加载中", false);

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
