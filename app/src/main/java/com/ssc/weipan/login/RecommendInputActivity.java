package com.ssc.weipan.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.SplashActivity;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.ToastHelper;
import com.ssc.weipan.base.Topbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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



    @OnClick(R2.id.next)
    public void clickCommit() {
        String recommend = mPhone.getText().toString();



        if (recommend.isEmpty()) {
            ToastHelper.showToast("请输入推荐人手机号");
            return;
        }



        finish();
        openHome();

    }

    @OnClick(R2.id.ignore)
    public void clickIgoner() {

        finish();
        openHome();
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
