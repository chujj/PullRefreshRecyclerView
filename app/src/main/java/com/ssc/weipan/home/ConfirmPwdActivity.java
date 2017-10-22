package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.user.UserApi;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.ToastHelper;
import com.ssc.weipan.model.BaseModel;

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
                    ToastHelper.showToast(baseModel.message);
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
}
