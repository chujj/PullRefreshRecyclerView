package com.biaoyixin.shangcheng.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.broker.BrokerApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.Topbar;
import com.ssc.widget.pullrefreshrecyclerview.AutoLoadMoreRecyclerView;
import com.ssc.widget.pullrefreshrecyclerview.PullRefreshRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-11-25.
 */
public class ZhiShuActivity extends BaseActivity {

    @BindView(R2.id.count)
    TextView mCount;
    @BindView(R2.id.input)
    EditText mInput;
    @BindView(R2.id.loadmore_listview)
    AutoLoadMoreRecyclerView mListview;
    @BindView(R2.id.topbar)
    Topbar mTopbar;

    ZhiShuAdapter mAdapter;

    private int mPage = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.zhishu_activity);

        ButterKnife.bind(this, this);

        mTopbar.setTitle("直属客户");
        mTopbar.setBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdapter = new ZhiShuAdapter();

        mListview.setAdapter(mAdapter);

        mListview.setAutoLoadMoreListener(new PullRefreshRecyclerView.PullRefreshRecyclerViewListener() {
            @Override
            public void onLoadMore() {
                loadData(mPage + 1);
            }

            @Override
            public void onViewReady() {

            }

            @Override
            public void onRefresh() {

            }
        });
        mListview.setAutoLoadMoreEnable(true);

        loadData(0);
    }


    private void loadData(final int page) {

        BrokerApi.IBroker iBroker = ServerAPI.getInterface(BrokerApi.IBroker.class);
        iBroker.getZhiShu(mPage, 20, mInput.getText().toString(), new Callback<BrokerApi.ZhiShuResp>() {
            @Override
            public void success(BrokerApi.ZhiShuResp resp, Response response) {
                mListview.setLoadingMore(false);
                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);
                } else {
                    refreshUI(resp.data, page);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                mListview.setLoadingMore(false);
                ServerAPI.HandlerException(error);
            }
        });


    }

    private void refreshUI(BrokerApi.ZhiShuInfo data, int pageNum) {
        List<BrokerApi.ZhiShuItem> items =  mAdapter.getData();
        if (pageNum == 0) {
            mCount.setText("总人数 " + data.totalElements + "人");
            items .clear();
        }


        if ((pageNum + 1) >= data.totalPages) {
            mListview.setAutoLoadMoreEnable(false);
        }

        mPage = pageNum;


        items.addAll(data.content);
        mAdapter.notifyDataSetChanged();
    }


    @OnClick(R2.id.search)
    public void clickSearch() {
        mListview.setAutoLoadMoreEnable(true);
        loadData(0);

        closeSoftKeybord(mInput, this);
    }


    public static void closeSoftKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

}
