package com.ssc.weipan.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ssc.weipan.R;

/**
 * Created by zhujj on 17-10-17.
 */
public class Topbar extends LinearLayout {

    public final static int Layout = R.layout.topbar_layout;

    private TextView mTitleTV;

    public Topbar(Context context) {
        super(context);
    }

    public Topbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Topbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Topbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


        mTitleTV = CommonUtils.findView(this, R.id.title);
    }


    public void setTitle(String title) {
        mTitleTV.setText(title);
    }
}