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
import com.ssc.weipan.base.BaseFragment;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.Topbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        String phoneNum = mPhone.getText().toString();
        // TODO add request

        // success
        ((LoginActivity)getActivity()).switchToStep2SMSCode(phoneNum);
    }

}
