package com.ssc.widget.pullrefreshrecyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


// 1. [X] pull to refresh
// 2. [X] auto loadmore
// 3. [X] sticky header
// 4. [X] init with status refreshing
public class AutoLoadMoreRecyclerView extends android.support.v7.widget.RecyclerView {

    private Adapter mAdapter;
    private PullRefreshRecyclerView.PullRefreshRecyclerViewListener mListener;
    private boolean loadingMore;
    private LinearLayoutManager mLayoutManager;

    public AutoLoadMoreRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public AutoLoadMoreRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AutoLoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.setLayoutManager(mLayoutManager = new LinearLayoutManager(context));
    }

    protected PullRefreshRecyclerView.PullRefreshRecyclerViewListener getListener() {
        return mListener;
    }
    public void setAutoLoadMoreListener(PullRefreshRecyclerView.PullRefreshRecyclerViewListener listener) {
        mListener = listener;

        this.addOnScrollListener(new OnScrollListener() {
            private int lastVisiableVisiableItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mAdapter.isFooterEnable &&
                    newState == RecyclerView.SCROLL_STATE_IDLE &&
                    lastVisiableVisiableItem + 1 == mAdapter.getItemCount() &&
                    !loadingMore) {

                    setLoadingMore(true);
                    mListener.onLoadMore();
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisiableVisiableItem = mLayoutManager.findLastVisibleItemPosition();
            }
        });

    }

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        mAdapter.setFooterAnimation(loadingMore);
    }

    public static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public  final static int TYPE_NORMAL_START = 0;
        private final static int TYPE_HEADER = -1;
        private final static int TYPE_FOOTER = -2;

        private final RecyclerView.Adapter mInternalAdapter;

        private boolean isHeaderEnable;
        private boolean isFooterEnable;

        private int mHeaderResId;
        private View mHeaderView;

        private FooterViewHolder mFooterView;

        public Adapter(RecyclerView.Adapter adapter) {
            mInternalAdapter = adapter;
            isHeaderEnable = false;
            isFooterEnable = false;
        }

        @Override
        public int getItemViewType(int position) {
            int headerPosition = 0;
            int footerPosition = getItemCount() - 1;

            if (headerPosition == position && isHeaderEnable) {
                return TYPE_HEADER;
            }
            if (footerPosition == position && isFooterEnable) {
                return TYPE_FOOTER;
            }
            return mInternalAdapter.getItemViewType(position - (isHeaderEnable ? 1 : 0));
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                if (mHeaderView != null) {
                    return new HeaderViewHolder(mHeaderView);
                }
                return new HeaderViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                        mHeaderResId, parent, false));
            } else if (viewType == TYPE_FOOTER) {
                return mFooterView = new FooterViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.footerview_layout, parent, false));
//                return new FooterViewHolder(new TextView(parent.getContext()));
            } else { // type normal
                return mInternalAdapter.onCreateViewHolder(parent, viewType);
            }
        }

        public void setFooterAnimation(boolean loadingMore) {
//            mFooterView.mProgressBar.
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder {

            private final ProgressBar mProgressBar;

            public FooterViewHolder(View itemView) {
                super(itemView);
                mProgressBar = (ProgressBar) itemView.findViewById(R.id.footer_view_progressbar);
            }

        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder {

            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int type =getItemViewType(position);
            if (type == TYPE_HEADER) {
//                ((TextView)holder.itemView).setText("I'm header View");
            } else if (type == TYPE_FOOTER) {
//                ((TextView)holder.itemView).setText("I'm footer View");
            } else {
                mInternalAdapter.onBindViewHolder(holder, position - (isHeaderEnable ? 1 : 0));
            }
        }


        @Override
        public int getItemCount() {
            int count = mInternalAdapter.getItemCount();
            if (isHeaderEnable) count++;
            if (isFooterEnable) count++;

            return count;
        }

        public void setHeaderEnable(boolean b, int layout_resid) {
            isHeaderEnable = b;
            mHeaderResId = layout_resid;
        }

        public void setHeaderEnable(boolean b, View v) {
            isHeaderEnable = b;
            mHeaderView = v;
        }

        public void setFooterEnable(boolean autoLoadMore) {
            isFooterEnable = autoLoadMore;
        }
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter != null) {
            mAdapter = createWrapHeader(adapter);
            adapter.registerAdapterDataObserver(new AdapterDataObserver() {
                @Override
                public void onChanged() {
                    mAdapter.notifyDataSetChanged();
                }
            });

        }
        super.setAdapter(mAdapter);
    }

    private Adapter createWrapHeader(RecyclerView.Adapter adapter) {
        return new Adapter(adapter);
    }

    public void setAutoLoadMoreEnable(boolean autoLoadMore) {
        setLoadingMore(false);
        mAdapter.setFooterEnable(autoLoadMore);
        mAdapter.notifyDataSetChanged();
    }

    public void setHeaderLayout(int layout_resid) {
        mAdapter.setHeaderEnable(true, layout_resid);
        mAdapter.notifyDataSetChanged();
    }

    public void setHeaderLayout(View v) {
        mAdapter.setHeaderEnable(true, v);
        mAdapter.notifyDataSetChanged();
    }

}
