package com.ssc.weipan.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ssc.weipan.R;


public class LoadingDialog extends Dialog {

    private String mLoadingMsg = null;

    private int mSeconds;

    private TextView tvLoading;

    /**
     * 背景透明的对话框.
     *
     * @param context 一般都是Activity.
     */
    public LoadingDialog(@NonNull Context context) {
        this(context, null);
    }

    /**
     * 背景透明的对话框.
     *
     * @param context 一般都是Activity.
     * @param msg     对话框显示的文案.
     */
    public LoadingDialog(@NonNull Context context, String msg) {
        this(context,0, msg);
    }

    @Deprecated
    public LoadingDialog(Context context, int styleID) {
        super(context, styleID);
    }

    @Deprecated
    public LoadingDialog(Context context, int styleID, int id) {
        this(context, styleID, context.getString(id));
    }

    @Deprecated
    public LoadingDialog(Context context, int styleID, String msg) {
        super(context, styleID);
        mLoadingMsg = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        setCanceledOnTouchOutside(false);// 防止用户点击内容区域外消失.
        tvLoading = (TextView) findViewById(R.id.text_loading);
        if (TextUtils.isEmpty(mLoadingMsg)) {
            tvLoading.setVisibility(View.GONE);
        } else {
            tvLoading.setVisibility(View.VISIBLE);
            tvLoading.setText(mLoadingMsg);
        }
    }

    @Override
    public void show() {
        super.show();
        if (mSeconds != 0) {
            mDismissHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSeconds--;
            if (mSeconds <= 0) {
                dismiss();
            } else {
                sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    public void setLoadingMsg(String loadingMsg) {
        this.mLoadingMsg = loadingMsg;
        if (tvLoading != null && !TextUtils.isEmpty(loadingMsg)) {
            tvLoading.setText(mLoadingMsg);
        }
    }

    public void setSeconds(int seconds) {
        mSeconds = seconds;
        if (mSeconds != 0) {
            setCancelable(false);
        }
    }

}
