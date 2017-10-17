package com.ssc.weipan.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.sms.SmsApi;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.BaseFragment;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.Topbar;
import com.ssc.weipan.model.BaseModel;

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
        // TODO add request

        ((BaseActivity)getActivity()).showLoadingDialog("加载中", true);

        SmsApi.ISMS iSms = ServerAPI.getInterface(SmsApi.ISMS.class);
        iSms.requereSMSCode("7", phoneNum, new Callback<BaseModel>() {
            @Override
            public void success(BaseModel baseModel, Response response) {

                if (baseModel.code == 0) {
                    // success
                    ((LoginActivity)getActivity()).switchToStep2SMSCode(phoneNum);
                }
                dissmiss();
            }

            @Override
            public void failure(RetrofitError error) {

                dissmiss();
            }

            private void dissmiss() {
                ((BaseActivity)getActivity()).dismissLoadingDialog();
            }
        });



    }

}
