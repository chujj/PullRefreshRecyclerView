package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.user.UserApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.Topbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-11-9.
 */
public class QinBinDuiActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;
    @BindView(R2.id.listview)
    ListView mListView;

    QinBinDuiAdapter mAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.qin_bin_dui_activity);


        ButterKnife.bind(this, this);


        mTopbar.setTitle("亲兵队");
        mTopbar.setBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdapter = new QinBinDuiAdapter();
        mListView.setAdapter(mAdapter);

        loadData();
    }

    private void loadData() {


        UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
        iUser.getBrokerList(new Callback<UserApi.BrokerListResp>() {
            @Override
            public void success(UserApi.BrokerListResp resp, Response response) {
                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);
                } else {
                    mAdapter.setData(resp.data.content);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

//        List<UserApi.Broker> brokers = new ArrayList<>();
//        try {
//            brokers = new Gson().fromJson(new InputStreamReader(getAssets().open("mock.qinweidui")),
//                    new TypeToken<List<UserApi.Broker>>() {}.getType());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        final List<UserApi.Broker> finalBrokers = brokers;
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mAdapter.setData(finalBrokers);
//                mAdapter.notifyDataSetChanged();
//            }
//        }, 2000);


    }
}
