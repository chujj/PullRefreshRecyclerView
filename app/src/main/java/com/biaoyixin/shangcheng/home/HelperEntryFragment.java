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
            add(new Object[] {"交易规则", new String[] {"assets://h1.jpg"}});
            add(new Object[] {"充值流程", new String[] {"assets://h2.jpg"}});
            add(new Object[] {"操作指南", new String[] {"assets://h3.jpg"}});
            add(new Object[] {"注册御木轩", new String[] {"assets://h4.jpg"}});
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

            final String title =(String)mEntriesConfig.get(i)[0];
            ((TextView) CommonUtils.findView(mEntries[i], R.id.name)).setText(title);


            final String[] images = (String[]) mEntriesConfig.get(i)[1];
            mEntries[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getContext(), ImageListActivity.class);
                    it.putExtra("images", images);
                    it.putExtra("title", title);
                    HelperEntryFragment.this.startActivity(it);
                }
            });
        }
    }

}
