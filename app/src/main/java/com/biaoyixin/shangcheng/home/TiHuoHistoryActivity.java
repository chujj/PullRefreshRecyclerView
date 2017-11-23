package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.trade.GoodsApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.Topbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-11-23.
 */
public class TiHuoHistoryActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;
    @BindView(R2.id.listview)
    ListView mListView;
    private TihuoHistoryAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setContentView(R.layout.tihuo_activity);


        ButterKnife.bind(this, this);


        mTopbar.setTitle("提货记录");
        mTopbar.setBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mAdapter = new TihuoHistoryAdapter();
        mListView.setAdapter(mAdapter);


        loadData();
    }

    private void loadData() {
        GoodsApi.IGood iGood = ServerAPI.getInterface(GoodsApi.IGood.class);
        iGood.tihuoHistory(new Callback<GoodsApi.TihuoResp>() {
            @Override
            public void success(GoodsApi.TihuoResp resp, Response response) {
                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);
                } else {
                    mAdapter.setData(resp.data);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });


    }
}
