package com.ssc.widget.pullrefreshrecyclerview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;


public class PullRefreshRecyclerView extends SwipeRefreshLayout {

    private AutoLoadMoreRecyclerView mAutoLoadMoreRecyclerView;

    public PullRefreshRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public PullRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mAutoLoadMoreRecyclerView = new AutoLoadMoreRecyclerView(context);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addView(mAutoLoadMoreRecyclerView, lp);
    }

    public void setListener(PullRefreshRecyclerViewListener listener) {
        this.setOnRefreshListener(listener);
        mAutoLoadMoreRecyclerView.setAutoLoadMoreListener(listener);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mAutoLoadMoreRecyclerView.setAdapter(adapter);
    }

    public void setAutoLoadMoreEnable(boolean autoLoadMore) {
        mAutoLoadMoreRecyclerView.setAutoLoadMoreEnable(autoLoadMore);
    }

    public void setHeaderLayout(int i) {
        mAutoLoadMoreRecyclerView.setHeaderLayout(i);
    }

    public void notifyDateSetChanged() {
        mAutoLoadMoreRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void setLoadingMore(boolean loadingMore) {
        mAutoLoadMoreRecyclerView.setLoadingMore(loadingMore);
    }

    public static interface PullRefreshRecyclerViewListener extends SwipeRefreshLayout.OnRefreshListener {
        public abstract void onLoadMore();
    }

}
