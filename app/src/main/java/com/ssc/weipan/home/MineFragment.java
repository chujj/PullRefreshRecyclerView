package com.ssc.weipan.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.base.BaseFragment;
import com.ssc.weipan.base.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by zhujj on 17-10-22.
 */
public class MineFragment extends BaseFragment {


    @BindViews({R2.id.entry_1, R2.id.entry_2, R2.id.entry_3, R2.id.entry_4, R2.id.entry_5, R2.id.entry_6, })
    View[] mEntrys;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.mine_fragment, container, false);
        return viewRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        initEntries();
    }

    private void initEntries() {
        for (int i = 0; i < entryProperties.size(); i++) {
            ((ImageView) CommonUtils.findView(mEntrys[i], R.id.icon)).setImageResource(R.drawable.trade_home_user_header_tixian);
            ((TextView)CommonUtils.findView(mEntrys[i], R.id.name)).setText((String) entryProperties.get(i)[0]);
            mEntrys[i].setOnClickListener((View.OnClickListener) entryProperties.get(i)[1]);
        }
    }


    private List<Object[]> entryProperties = new ArrayList<Object[]>() {
            {
                add( new Object[] {
                        "银行卡",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent it = new Intent(MineFragment.this.getContext(),
                                        BankCardActivity.class);
                                MineFragment.this.startActivity(it);
                            }
                        }
                });
                add(new Object[] {
                        "我的交易轨迹",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }
                });
                add(new Object[] {
                        "出入金记录",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }
                });
                add(new Object[] {
                        "优惠券",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }
                });
                add(new Object[] {
                        "推荐码",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }
                });
                add(new Object[] {
                        "个人设置",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }
                });
            }
        };



}
