package com.biaoyixin.shangcheng.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.biaoyixin.shangcheng.R;

/**
 * Created by zhujj on 17-10-17.
 */
public class FragmentUtils {


    public static void setActivityFragmentsHolderContent(BaseActivity baseActivity) {
        baseActivity.setContentView(R.layout.fragments_activity);
    }

    public static void switchFragment(FragmentManager fg, Context context, String key, Bundle bundle) {
        switchFragment(fg, context, key, bundle, false);
    }

    public static void switchFragment(FragmentManager fg, Context context, String key, Bundle bundle, boolean addBackStack) {
        Fragment fragment = fg.findFragmentByTag(key);
        if(fragment == null) {
            fragment = Fragment.instantiate(context, key, bundle);
        }

        FragmentTransaction ft = fg.beginTransaction();
        ft.replace(R.id.ll_main, fragment, key);

        if(addBackStack) {
            ft.addToBackStack((String)null);
        }

        ft.commitAllowingStateLoss();
        fg.executePendingTransactions();
    }
}
