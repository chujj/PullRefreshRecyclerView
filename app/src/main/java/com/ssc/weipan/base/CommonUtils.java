package com.ssc.weipan.base;

import android.app.Activity;
import android.view.View;

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

}
