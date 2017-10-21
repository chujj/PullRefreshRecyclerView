package com.ssc.weipan.base;



import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by songping on 15/3/12.
 */

/**
 * 横向自动换行瀑布流，用于实现选购商品时的标签
 * <p/>
 */
public abstract class WrapLabelLayout<T> extends ViewGroup {

    protected int marginRight;
    protected int marginTop;
    protected int marginLeft;
    protected int marginBotton;
    protected int viewWidth;
    protected Context context;

    protected List<T> items;

    public WrapLabelLayout(Context context) {
        this(context, null);
    }

    public WrapLabelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapLabelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        marginRight = CommonUtils.dip2px(context, 10f);
        marginTop = CommonUtils.dip2px(context, 10f);
        items = new ArrayList<T>();
    }

    public void setMargin(float margin) {
        setMargin(margin, margin, margin, margin);
    }

    public void setMargin(float left, float top, float right, float botton) {
        this.marginLeft = CommonUtils.dip2px(context, left);
        this.marginTop = CommonUtils.dip2px(context, top);
        this.marginRight = CommonUtils.dip2px(context, right);
        this.marginBotton = CommonUtils.dip2px(context, botton);
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }

    public void initView() {
        removeAllViews();
        for (T item : items) {
            addView(getItemView(item));
        }
//        for (int i = 0; i < 10; i++) {
//            addView(getItemView(null));
//        }
    }

    public abstract View getItemView(T t);

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        viewWidth = widthSize;
        int elementWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int elementHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int currentWidth = 0;
        int heightSize = 0;
        for (int i = 0, n = getChildCount(); i < n; i++) {
            View elementView = getChildAt(i);
            elementView.measure(elementWidthSpec, elementHeightSpec);
            //发生折行
            if (i == 0) {
                heightSize += elementView.getMeasuredHeight();
                currentWidth = 0;
            } else if (currentWidth + elementView.getMeasuredWidth() > widthSize) {
                heightSize += elementView.getMeasuredHeight() + marginTop + marginBotton;
                currentWidth = 0;
            }
            currentWidth += elementView.getMeasuredWidth() + marginRight + marginLeft;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int x = 0;
        int y = 0;
        for (int i = 0, n = getChildCount(); i < n; i++) {
            View elementView = getChildAt(i);
            //新开一行
            if (x + elementView.getMeasuredWidth() > viewWidth) {
                x = 0;
                y += elementView.getMeasuredHeight() + marginTop + marginBotton;
                elementView.layout(x, y, x + elementView.getMeasuredWidth(), y + elementView.getMeasuredHeight());
            } else {
                elementView.layout(x, y, x + elementView.getMeasuredWidth(), y + elementView.getMeasuredHeight());
            }
            x += elementView.getMeasuredWidth() + marginRight + marginLeft;
        }
    }
}

