package com.shouhuxing.healthassist.wxapi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.biaoyixin.shangcheng.base.ToastHelper;
import com.biaoyixin.shangcheng.share.Share;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth.Resp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * Created by zhujj on 17-11-4.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Share.getInstance().getIWXAPI().handleIntent(getIntent(), this);
    }
    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        handleWxResp(this, baseResp);
        finish();
    }

    public static void handleWxResp(Context context, BaseResp baseResp) {
        if(baseResp instanceof Resp) {
            Bundle result = new Bundle();
            baseResp.toBundle(result);
//            EventBus.getDefault().post(new AuthEvent(2, result));
        } else {
            boolean result1 = false;
            int result2;
            switch(baseResp.errCode) {
            case -4:

//                Utils.postShareResultEvent(2, mShareModel, "WeChat");
                ToastHelper.showToast("分享返回");
//                if(sOnShareListener != null) {
//                    sOnShareListener.onShared(false, TAG, mShareModel, "分享返回");
//                }
                break;
            case -3:
            case -1:
            default:
                ToastHelper.showToast("分享被拒绝");
//                result2 = string.share_tip_deny;
//                Utils.postShareResultEvent(2, mShareModel, "WeChat");
//                Utils.showToast(context, result2);
//                if(sOnShareListener != null) {
//                    sOnShareListener.onShared(false, TAG, mShareModel, "分享被拒绝");
//                }
                break;
            case -2:
                ToastHelper.showToast("分享取消");
//                result2 = string.share_tip_cancel;
//                Utils.postShareResultEvent(1, mShareModel, "WeChat");
//                Utils.showToast(context, result2);
                break;
            case 0:
                ToastHelper.showToast("分享成功");
//                result2 = string.share_tip_success;
//                Utils.postShareResultEvent(0, mShareModel, "WeChat");
//                Utils.showToast(context, result2);
//                if(sOnShareListener != null) {
//                    sOnShareListener.onShared(true, TAG, mShareModel, (String)null);
//                }
            }
        }

    }
}
