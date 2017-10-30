package com.biaoyixin.shangcheng.base;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

/**
 * Created by zhujj on 17-10-16.
 */
public class BaseActivity extends AppCompatActivity {


    private LoadingDialog mLoadingDialog;
    private String mLastMsg = null;

    public void showLoadingDialog(String msg, boolean flag) {
        if(this.mLoadingDialog == null || !this.mLoadingDialog.isShowing() || !TextUtils.equals(msg, this.mLastMsg)) {
            this.dismissLoadingDialog();
            this.mLastMsg = msg;
            this.mLoadingDialog = new LoadingDialog(this, msg);
            this.mLoadingDialog.setCancelable(flag);
            this.mLoadingDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        if(this.mLoadingDialog != null && this.mLoadingDialog.isShowing()) {
            this.mLoadingDialog.dismiss();
            this.mLoadingDialog = null;
        }

    }
}
