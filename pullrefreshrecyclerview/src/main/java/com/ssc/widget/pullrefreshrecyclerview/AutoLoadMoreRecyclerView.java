package com.ssc.widget.pullrefreshrecyclerview;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

// 1. pull to refresh
// 2. auto loadmore
// 3. sticky header
// 4. init with status refreshing
public class AutoLoadMoreRecyclerView extends android.support.v7.widget.RecyclerView {

    public static abstract class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    }

    public AutoLoadMoreRecyclerView(Context context) {
        super(context);
    }

    public AutoLoadMoreRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoLoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }




}
