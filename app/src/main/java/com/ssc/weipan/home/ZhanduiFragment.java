package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.base.BaseFragment;
import com.ssc.weipan.login.AccountManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhujj on 17-10-23.
 */
public class ZhanduiFragment extends BaseFragment {


    @BindView(R2.id.avatar)
    ImageView mAvatar;
    @BindView(R2.id.nickname)
    TextView mNickname;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.zhandui_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ButterKnife.bind(this, view);


        AccountManager.Account account = AccountManager.getAccount();
        Glide.with(this).load(account.avatar).into(mAvatar);

        mNickname.setText(account.nickName);

    }
}
