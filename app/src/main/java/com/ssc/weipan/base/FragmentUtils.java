package com.ssc.weipan.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ssc.weipan.R;

/**
 * Created by zhujj on 17-10-17.
 */
public class FragmentUtils {


    public static void setActivityFragmentsHolderContent(BaseActivity baseActivity) {
        baseActivity.setContentView(R.layout.fragments_activity);
    }

    public static void switchFragment(FragmentManager fg, Context context, String key, Bundle bundle) {
        Fragment fragment = fg.findFragmentByTag(key);
        if(fragment == null) {
            fragment = Fragment.instantiate(context, key, bundle);
        }

        FragmentTransaction ft = fg.beginTransaction();
        ft.replace(R.id.ll_main, fragment, key);

        ft.commitAllowingStateLoss();
        fg.executePendingTransactions();
    }
}
