package com.biaoyixin.shangcheng.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.base.BaseFragment;
import com.biaoyixin.shangcheng.base.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by zhujj on 17-11-19.
 */
public class HelperEntryFragment extends BaseFragment {


    @BindViews({R2.id.entry_1, R2.id.entry_2, R2.id.entry_3, R2.id.entry_4})
    View[] mEntries;



    List<Object[]> mEntriesConfig = new ArrayList() {
        {
            add(new Object[] {"交易规则"});
            add(new Object[] {"充值流程"});
            add(new Object[] {"操作指南"});
            add(new Object[] {"注册御木轩"});
        }};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.helper_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        for (int i = 0; i < mEntries.length; i++) {

            ((TextView) CommonUtils.findView(mEntries[i], R.id.name)).setText(
                    (String)mEntriesConfig.get(i)[0]
            );


            final String[] images = (String[]) mEntriesConfig.get(i)[1];
            mEntries[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getContext(), ImageListActivity.class);
                    it.putExtra("images", images);
                    HelperEntryFragment.this.startActivity(it);
                }
            });
        }
    }

}
