package com.ssc.weipan.base;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.ssc.weipan.BuildConfig;

/**
 * Created by zhujj on 17-10-17.
 */
public class CommonUtils {

    public static <T> T findView(View parent, int id) {
        return (T) parent.findViewById(id);
    }

    public static <T> T findView(Activity activity, int id) {
        return (T) activity.findViewById(id);
    }

    public static boolean isDebugBuild() {
        return BuildConfig.DEBUG;
    }


    public static void showSoftKeyboard(View view, Context context) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void addToActivity(Activity activity, View v) {
        FrameLayout contentView = (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);

        if (contentView != null) {
            contentView.addView(v);
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }    //dp转px


}
