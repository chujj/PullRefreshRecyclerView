package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.base.BaseFragment;

/**
 * Created by zhujj on 17-11-19.
 */
public class YouHuiQuanFragment extends BaseFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.youhuiquan_fragment, container, false);
    }
}
