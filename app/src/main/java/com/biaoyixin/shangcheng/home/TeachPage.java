package com.biaoyixin.shangcheng.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.base.BaseApp;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.biaoyixin.shangcheng.base.PreferencesUtil;

/**
 * Created by zhujj on 17-11-5.
 */
public class TeachPage extends FrameLayout implements View.OnClickListener {
    public TeachPage(Context context) {
        super(context);
    }

    public TeachPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TeachPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TeachPage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



    private int mCurrIndex;

    private int[] mTeachPages;

    private ImageView mImg;
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


        this.setOnClickListener(this);
        mCurrIndex = 0;
        mTeachPages = new int[] {
                R.drawable.teach_step1,
                R.drawable.teach_step2,
                R.drawable.teach_step3,
                R.drawable.teach_step4,
                R.drawable.teach_step5,
                R.drawable.teach_step6,
        };


        mImg = CommonUtils.findView(this, R.id.teach_page);

        mImg.setImageResource(mTeachPages[mCurrIndex]);
    }

    @Override
    public void onClick(View v) {
        if (mCurrIndex >= (mTeachPages.length -1)) {
            PreferencesUtil.putBoolean(BaseApp.getApp(), HomeActivity.KEY_TEACH_PAGES, true);
            ((ViewGroup) getParent()).removeView(this);
        } else {
            mCurrIndex++;
            mImg.setImageResource(mTeachPages[mCurrIndex]);
        }
    }
}
