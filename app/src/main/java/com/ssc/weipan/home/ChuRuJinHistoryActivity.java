package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.user.UserApi;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.ToastHelper;
import com.ssc.weipan.base.Topbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-24.
 */
public class ChuRuJinHistoryActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;
    @BindView(R2.id.listview)
    ListView mListView;
    private ChuRuJinAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.churujin_hisotry_layout);

        ButterKnife.bind(this, this);

        mTopbar.setTitle("出入金记录");
        mListView.setAdapter(mAdapter = new ChuRuJinAdapter(this));

        loadData();
    }

    private void loadData() {
        showLoadingDialog("加载中...", false);
        UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
        iUser.getChuRuJinHistory(new Callback<UserApi.ChuRuJinHistoryResp>() {
            @Override
            public void success(UserApi.ChuRuJinHistoryResp resp, Response response) {
                ChuRuJinHistoryActivity.this.dismissLoadingDialog();
                if (resp.code != 0) {
                    ToastHelper.showToast(resp.message);
                    ServerAPI.handleCodeError(resp);
                } else {
                    mAdapter.setData(resp.data);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
                ChuRuJinHistoryActivity.this.dismissLoadingDialog();
            }
        });

    }
}
