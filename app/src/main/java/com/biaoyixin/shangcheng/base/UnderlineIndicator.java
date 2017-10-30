package com.biaoyixin.shangcheng.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by zhujj on 17-10-24.
 */
public class UnderlineIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {

    Rect mBGRect;
    Rect mCacheRect;
    Paint mIndicatorPaint;
    private int mCount;
    private int mOffset = 0;
    private int mSingleWidth;


    public UnderlineIndicator(Context context) {
        super(context);
    }

    public UnderlineIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UnderlineIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UnderlineIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mBGRect = new Rect();
        mCacheRect = new Rect();

        mIndicatorPaint = new Paint();
        mIndicatorPaint.setColor(0xFFEBAD33);


        this.setWillNotDraw(false);
    }


    public static interface ChildProvider {
        public View onGetChild(ViewGroup parent, int position);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        mBGRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if (mCount == 0) return;

        mCacheRect.set(mBGRect);
        mCacheRect.top = mBGRect.bottom - 10;

        mCacheRect.left = mOffset;
        mCacheRect.right = mOffset + mSingleWidth;
        canvas.drawRect(mCacheRect, mIndicatorPaint);
    }


    public void setViewPager(ViewPager vp , ChildProvider childProvider) {
        vp.addOnPageChangeListener(this);
        mCount = vp.getAdapter().getCount();
        mSingleWidth = mBGRect.width() / mCount;


        if (childProvider != null) {
            for (int i = 0; i < mCount; i++) {
                this.addView(childProvider.onGetChild(this, i));
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mOffset = (int) ((mBGRect.width() / mCount) * (position + positionOffset));
        this.invalidate();
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



}
