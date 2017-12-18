package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

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
 * Created by zhujj on 17-12-17.
 */

public class FeedbackActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;

    @BindView(R2.id.edittext)
    EditText mEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.feedback_activity);


        ButterKnife.bind(this, this);


        mTopbar.setTitle("意见反馈");
        mTopbar.setBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @OnClick(R2.id.confirm)
    public void clickConfirm() {
        String content = mEditText.getText().toString();

        if (TextUtils.isEmpty(content)) {
            ToastHelper.showToast("请输入您的建议");
            return ;
        }

        content = ("意见反馈@" + content);
        UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
        iUser.feedback(content, "1000", new Callback<BaseModel>() {
            @Override
            public void success(BaseModel resp, Response response) {
                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);
                } else {
                    if (!TextUtils.isEmpty(resp.message)) {
                        ToastHelper.showToast(resp.message);
                    } else {
                        ToastHelper.showToast("您的反馈已经提交，谢谢！");
                    }
                    finish();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }


}
