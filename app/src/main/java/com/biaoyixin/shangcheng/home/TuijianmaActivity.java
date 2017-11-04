package com.biaoyixin.shangcheng.home;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.ToastHelper;
import com.biaoyixin.shangcheng.login.AccountManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhujj on 17-10-24.
 */
public class TuijianmaActivity extends BaseActivity {

    @BindView(R2.id.code)
    TextView mCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setContentView(R.layout.tuijianma_layout);

        ButterKnife.bind(this, this);



        AccountManager.Account account = AccountManager.getAccount();
        mCode.setText(account.invite_code);
    }


    @OnClick(R2.id.container)
    public void clickBG() {

    }
    @OnClick(R2.id.ok)
    public void clickOK() {

        ClipboardManager cmb = (ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(mCode.getText());

        ToastHelper.showToast("已复制邀请码到剪切板中，快去发给好友吧～");

        this.finish();
    }
}