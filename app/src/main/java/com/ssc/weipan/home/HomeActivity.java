package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.Topbar;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhujj on 17-10-16.
 */
public class HomeActivity extends BaseActivity {


    @BindView(R2.id.topbar)
    Topbar mTopbar;
    @BindView(R2.id.ll_main)
    ViewGroup mContainer;

    @BindViews({R2.id.home_icon_1, R2.id.home_icon_2 , R2.id.home_icon_3 , R2.id.home_icon_4})
    ViewGroup[] mHomeButtons;

    private String[] mFragmentNames = new String[] {
            TradeHomeFragment.class.getName(),
            WebViewFragment.class.getName(),
            ZhanduiFragment.class.getName(),
            MineFragment.class.getName(),
    };

    private int mIndex = 0; // default 0

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.home_activity);

        ButterKnife.bind(this, this);

        mTopbar.setTitle("时间盘");

        switchToIndex(mIndex);
    }


    private String mLastFragmentKey = null;
    private void switchToIndex(int index) {
        mIndex = index;


        for (int i = 0; i < mHomeButtons.length; i++) {
            for (int j = 0; j < mHomeButtons[i].getChildCount(); j++) {
                mHomeButtons[i].getChildAt(j).setSelected(i == mIndex);
            }
        }


        String key = mFragmentNames[mIndex];
        FragmentManager fg = getSupportFragmentManager();
        FragmentTransaction ft = fg.beginTransaction();

        if (mLastFragmentKey != null) {
            ft.hide(fg.findFragmentByTag(mLastFragmentKey));
        }

        Fragment fragment = fg.findFragmentByTag(key);
        if(fragment == null) {
            fragment = Fragment.instantiate(this, key, null);
            ft.add(R.id.ll_main, fragment, key);
        } else {
            ft.show(fragment);
        }

        mLastFragmentKey = key;

        ft.commitAllowingStateLoss();
        fg.executePendingTransactions();
    }


    @OnClick(R2.id.home_icon_1)
    public void clickBtn1() {
        switchToIndex(0);
    }

    @OnClick(R2.id.home_icon_2)
    public void clickBtn2() {
        switchToIndex(1);
    }

    @OnClick(R2.id.home_icon_3)
    public void clickBtn3() {
        switchToIndex(2);
    }

    @OnClick(R2.id.home_icon_4)
    public void clickBtn4() {
        switchToIndex(3);
    }



}
