package com.ssc.weipan.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.SplashActivity;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.login.LoginApi;
import com.ssc.weipan.api.sms.SmsApi;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.BaseFragment;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.PreferencesUtil;
import com.ssc.weipan.base.ToastHelper;
import com.ssc.weipan.base.Topbar;
import com.ssc.weipan.home.PasswordSetupActivity;
import com.ssc.weipan.model.BaseModel;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-17.
 */
public class Step2SMSCodeFragment extends BaseFragment {
    //http://www.showdoc.cc/1676150?page_id=15434277


    @BindView(R2.id.phone)
    TextView mPhonePromt;

    @BindView(R2.id.debug_sms)
    EditText mInput;

    @BindViews({R2.id.t1, R2.id.t2, R2.id.t3, R2.id.t4, R2.id.t5})
    TextView[] mSmscodes;


    @BindView(R2.id.count_down)
    TextView mCounterDownText;


    CountDownTimer mTimer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(this.getContext())
                .inflate(R.layout.step2_smscode_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Topbar topbar = CommonUtils.findView(view, R.id.topbar);
        topbar.setTitle("填写验证码");


        ButterKnife.bind(this, view);

        {
            // promt
            String phone = getArguments().getString("phone_num", "");
            if (TextUtils.isEmpty(phone)) {
                mPhonePromt.setVisibility(View.INVISIBLE);
            } else {
                mPhonePromt.setVisibility(View.VISIBLE);
                StringBuilder sb = new StringBuilder();
                sb.append("验证码已发送到 ");
                int start = sb.length();
                for (int i = 0; i < phone.length(); i++) {
                    if (i == 3 || i == 7) {
                        sb.append(" ");
                    }
                    sb.append(phone.substring(i, i+1));
                }
                int end = sb.length();
                SpannableString ss = new SpannableString(sb.toString());
                ss.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mPhonePromt.setText(ss);
            }
        }


        {
            // mInput
            if (CommonUtils.isDebugBuild()) {
                mInput.setTextColor(0xffffffff);
            } else {
                mInput.setHeight(1);
                mInput.setTextColor(0x00ffffff);
                mInput.setBackgroundColor(Color.TRANSPARENT);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CommonUtils.showSoftKeyboard(mInput, getContext());
                }
            }, 1000);
            mInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String str = s.toString();


                    for(TextView t : mSmscodes) {
                        t.setText("");
                    }

                    boolean finished = false;

                    for (int i = 0; i < str.length(); i++) {

                        mSmscodes[i].setText(str.substring(i, i+1));


                        if (i == (mSmscodes.length - 1)) {
                            finished  = true;
                            break;
                        }
                    }


                    if (finished) {
                        mInput.setEnabled(false);
                        String smsCode = str.substring(0, mSmscodes.length);
                        if (getArguments().getBoolean("forget_pwd", false)) {
                            verifyForgetPwd(smsCode);
                        } else {
                            verifySMSCode(smsCode);
                        }
                    }

                }



            });
        }

        startCountDown();
    }

    private void startCountDown() {

        mTimer = new CountDownTimer(60 * 1000, 300) {
            @Override
            public void onTick(long millisUntilFinished) {
                mCounterDownText.setText("重新获取" + (millisUntilFinished / 1000) + "秒");
            }

            @Override
            public void onFinish() {
                mCounterDownText.setText("重新获取短信验证码");
                mCounterDownText.setOnClickListener(countDownTextClick);
            }
        };
        mTimer.start();
    }



    private View.OnClickListener countDownTextClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCounterDownText.setOnClickListener(null);


            if (getArguments().getBoolean("forget_pwd", false)) {
//                verifyForgetPwd(smsCode);
            } else {
                Step1PhoneNumInputFragment.requireSMSCode(
                        Step2SMSCodeFragment.this,
                        getArguments().getString("phone_num", ""),
                        new Runnable() {
                            @Override
                            public void run() {
                                startCountDown();
                            }
                        });
            }

        }
    };



    private void verifyForgetPwd(String smsCode) {

        String phone = getArguments().getString("phone_num", "");

        ((BaseActivity) getActivity()).showLoadingDialog("加载中", false);


        SmsApi.ISMS iSms = ServerAPI.getInterface(SmsApi.ISMS.class);
        iSms.verifySms("5", smsCode, new Callback<BaseModel>() {
            @Override
            public void success(BaseModel baseModel, Response response) {
                ((BaseActivity) getActivity()).dismissLoadingDialog();
                if (baseModel.code != 0) {
                    ServerAPI.handleCodeError(baseModel);
                    ToastHelper.showToast(baseModel.message);
                } else {
                    Intent it = new Intent(getContext(), PasswordSetupActivity.class);
                    it.putExtra("forget_pwd", true);
                    startActivity(it);
                    getActivity().finish();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                 ((BaseActivity) getActivity()).dismissLoadingDialog();
                ServerAPI.HandlerException(error);
            }
        });

    }

    private void verifySMSCode(String smsCode) {

        String phone = getArguments().getString("phone_num", "");

        ((BaseActivity) getActivity()).showLoadingDialog("加载中", false);


        LoginApi.ILogin iLogin = ServerAPI.getInterface(LoginApi.ILogin.class);

        iLogin.login(phone, smsCode,/* phone,*/ new Callback<LoginApi.LoginResp>() {
            @Override
            public void success(LoginApi.LoginResp baseModel, Response response) {

                if (baseModel.code == 0) {

                    PreferencesUtil.putString(getContext(), AccountManager.PREF_USER_ID, baseModel.data.id + "");
                    if (baseModel.data.needShowInputBroker()) {
                        ((LoginActivity)getActivity()).switchToStep3Recommend();
                    } else {
                        SplashActivity.switchToMain(getContext());
                    }
                    getActivity().finish();
                } else {
                    ToastHelper.showToast(baseModel.message);
                }
                ((BaseActivity) getActivity()).dismissLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                 ((BaseActivity) getActivity()).dismissLoadingDialog();
                ServerAPI.HandlerException(error);
            }
        });

    }

}
