package com.ssc.weipan.base;

import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by zhujj on 17-10-17.
 */
public class ToastHelper {

    private static Toast mToast;

    /**
     * 为解决 http://mobile.umeng.com/apps/161e9078c5b0426594dc3a35/error_types/show?error_type_id=53a3cd4956240b5c8709e161_7212916063408484620_4.7.0<p/>
     * 增加try...catch...
     * 显示Toast
     *
     * @param text
     */
    public static void showToast(CharSequence text) {
        if (TextUtils.isEmpty(text)) return;

        try {
            if (mToast == null) {
                mToast = Toast.makeText(BaseApp.getApp(), text, Toast.LENGTH_LONG);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setDuration(Toast.LENGTH_LONG);
                mToast.setText(text);
            }
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 为解决 http://mobile.umeng.com/apps/161e9078c5b0426594dc3a35/error_types/show?error_type_id=53a3cd4956240b5c8709e161_7212916063408484620_4.7.0<p/>
     * 增加try...catch...
     * 显示Toast
     *
     * @param text
     */
    public static void showToastShort(CharSequence text) {
        if (TextUtils.isEmpty(text)) return;

        try {
            if (mToast == null) {
                mToast = Toast.makeText(BaseApp.getApp(), text, Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setDuration(Toast.LENGTH_SHORT);
                mToast.setText(text);
            }
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToast(int resId) {
        showToast(BaseApp.getApp().getResources().getText(resId));
    }

    public static void closeToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }
}