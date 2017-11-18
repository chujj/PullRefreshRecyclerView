package com.biaoyixin.shangcheng.account;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.biaoyixin.shangcheng.base.ClosureMethod;
import com.biaoyixin.shangcheng.base.ToastHelper;
import com.netease.nis.captcha.Captcha;
import com.netease.nis.captcha.CaptchaListener;

/**
 * Created by zhujj on 17-11-18.
 */
public class YiDun163 {

    public final static String captchaid = "01531427d5c245a99f1571dc30df0d01";

    private final ClosureMethod mSuccessCB;

    private Captcha mCaptcha;

    private UserLoginTask mLoginTask = null;

    public YiDun163(Context mContext, ClosureMethod successCB) {

        mSuccessCB = successCB;

        //初始化验证码SDK相关参数，设置CaptchaId、Listener最后调用start初始化。
        if (mCaptcha == null) {
            mCaptcha = new Captcha(mContext);
        }
        mCaptcha.setCaptchaId(captchaid);
        mCaptcha.setCaListener(myCaptchaListener);
        //可选：开启debug
        mCaptcha.setDebug(false);
        //可选：设置超时时间
        mCaptcha.setTimeout(10000);
    }


    public void start() {
        mLoginTask = new UserLoginTask();
        //关闭mLoginTask任务可以放在myCaptchaListener的onCancel接口中处理
        mLoginTask.execute();
        //必填：初始化 captcha框架
        mCaptcha.start();
        //可直接调用验证函数Validate()，本demo采取在异步任务中调用（见UserLoginTask类中）
        //mCaptcha.Validate();
    }

    CaptchaListener myCaptchaListener = new CaptchaListener() {

        @Override
        public void onValidate(String result, String validate, String message) {
            //验证结果，valiadte，可以根据返回的三个值进行用户自定义二次验证
            if (validate.length() > 0) {


                mSuccessCB.run(captchaid, validate);

//                toastMsg("验证成功，validate = " + validate);
            } else {
                toastMsg("验证失败：result = " + result + ", validate = " + validate + ", message = " + message);

            }
        }

        @Override
        public void closeWindow() {
            //请求关闭页面
            toastMsg("关闭页面");
        }

        @Override
        public void onError(String errormsg) {
            //出错
            toastMsg("错误信息：" + errormsg);
        }

        @Override
        public void onCancel() {
            toastMsg("取消线程");
            //用户取消加载或者用户取消验证，关闭异步任务，也可根据情况在其他地方添加关闭异步任务接口
            if (mLoginTask != null) {
                if (mLoginTask.getStatus() == AsyncTask.Status.RUNNING) {
                    Log.i("CaptchaListener", "stop mLoginTask");
                    mLoginTask.cancel(true);
                }
            }
        }

        @Override
        public void onReady(boolean ret) {
            //该为调试接口，ret为true表示加载Sdk完成
            if (ret) {
                toastMsg("验证码sdk加载成功");
            }
        }

    };

    private void toastMsg(String s) {
        ToastHelper.showToast(s);
    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        UserLoginTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //可选：简单验证DeviceId、CaptchaId、Listener值
            return mCaptcha.checkParams();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                //必填：开始验证
                mCaptcha.Validate();
            } else {
                toastMsg("验证码SDK参数设置错误,请检查配置");
            }
        }

        @Override
        protected void onCancelled() {
            mLoginTask = null;
        }
    }
}
