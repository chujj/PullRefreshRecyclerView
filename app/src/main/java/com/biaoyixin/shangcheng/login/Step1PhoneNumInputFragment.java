package com.biaoyixin.shangcheng.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.account.YiDun163;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.sms.SmsApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.BaseFragment;
import com.biaoyixin.shangcheng.base.ClosureMethod;
import com.biaoyixin.shangcheng.base.CommonUtils;
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
 * Created by zhujj on 17-10-17.
 */
public class Step1PhoneNumInputFragment extends BaseFragment {

    @BindView(R2.id.phone)
    EditText mPhone;
    @BindView(R2.id.next)
    View mNext;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(this.getContext())
                .inflate(R.layout.step1_phonenuminput_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Topbar topbar = CommonUtils.findView(view, R.id.topbar);
        topbar.setTitle("登录");


        ButterKnife.bind(this, view);

        mPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mNext.setEnabled(s.toString().length() == 11);
            }
        });
        mNext.setEnabled(false);
    }

    @OnClick(R2.id.next)
    public void clickNext() {
        final String phoneNum = mPhone.getText().toString();



        requireSMSCode(this, phoneNum, new Runnable() {
            @Override
            public void run() {
                ((LoginActivity)getActivity()).switchToStep2SMSCode(phoneNum);
            }
        });
    }



    public static void requireSMSCode(final BaseFragment baseFragment, final String phoneNum, final Runnable successCB) {

        ClosureMethod successCb = new ClosureMethod() {
            @Override
            public Object[] run(Object... args) {
                final String captchaId = (String) args[0];
                final String validate = (String) args[1];

                ((BaseActivity)baseFragment.getActivity()).showLoadingDialog("加载中", true);

                SmsApi.ISMS iSms = ServerAPI.getInterface(SmsApi.ISMS.class);
                iSms.requereSMSCode("7", phoneNum, captchaId, validate, new Callback<BaseModel>() {
                    @Override
                    public void success(BaseModel baseModel, Response response) {
                        dissmiss();

                        if (baseModel.code == 0) {
//                    // success
                            if (successCB != null) {
                                successCB.run();
                            }
                        } else {
                            ToastHelper.showToast(baseModel.message);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ServerAPI.HandlerException(error);
                        dissmiss();
                    }

                    private void dissmiss() {
                        ((BaseActivity) baseFragment.getActivity()).dismissLoadingDialog();
                    }
                });


                return null;
            }
        };

        new YiDun163(baseFragment.getContext(), successCb).start();

    }

}
