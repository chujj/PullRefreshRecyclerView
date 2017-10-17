package com.ssc.weipan.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssc.weipan.R;
import com.ssc.weipan.base.BaseFragment;
import com.ssc.weipan.base.CommonUtils;
import com.ssc.weipan.base.Topbar;

/**
 * Created by zhujj on 17-10-17.
 */
public class Step1PhoneNumInputFragment extends BaseFragment {


    @Nullable
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
    }
}
