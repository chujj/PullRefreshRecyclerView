package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.ClosureMethod;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhujj on 17-11-5.
 */
public class ShareActivity extends BaseActivity {


    public static ClosureMethod sCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.share_layout);

        ButterKnife.bind(this, this);
    }


    @OnClick(R2.id.cancel)
    public void clickCancel() {
        finish();
    }

    @OnClick(R2.id.wechat_session)
    public void clickWechatSession() {
        finish();
        sCallback.run(SendMessageToWX.Req.WXSceneSession);
    }

    @OnClick(R2.id.wechat_timeline)
    public void clickTimeline() {
        finish();
        sCallback.run(SendMessageToWX.Req.WXSceneTimeline);
    }
}
