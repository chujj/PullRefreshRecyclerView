package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.broker.BrokerApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.ToastHelper;
import com.biaoyixin.shangcheng.base.Topbar;
import com.biaoyixin.shangcheng.login.AccountManager;
import com.biaoyixin.shangcheng.model.BaseModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-11-26.
 */
public class BrokerRegistActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;

    @BindView(R2.id.phone)
    EditText mPhone;

    @BindView(R2.id.name)
    EditText mName;

    @BindView(R2.id.id)
    EditText mId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.broker_regist_activity);

        ButterKnife.bind(this, this);


        mTopbar.setTitle("申请经纪人");
        mTopbar.setBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        AccountManager.Account account = AccountManager.getAccount();
        mPhone.setText(account.phoneNum);
        mPhone.setEnabled(false);


        if (!TextUtils.isEmpty(account.orgCode)) {
            mId.setText(account.orgCode);
            mId.setEnabled(false);
        }
    }

    @OnClick(R2.id.confirm)
    public void clickConfirm() {
//        String phone = mPhone.getText().toString();
        String name = mName.getText().toString();
        String id = mId.getText().toString();


//        if (TextUtils.isEmpty(phone)) {
//            ToastHelper.showToast("请输入您的号码");
//            return;
//        }

        if (TextUtils.isEmpty(name)) {
            ToastHelper.showToast("请输入您的姓名");
            return;
        }

        BrokerApi.IBroker iBroker = ServerAPI.getInterface(BrokerApi.IBroker.class);
        iBroker.brokerRegist(name, id, new Callback<BaseModel>() {
            @Override
            public void success(BaseModel resp, Response response) {
                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);
                } else {
                    ToastHelper.showToast(resp.message);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });

    }
}
