package com.ssc.weipan.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.user.UserApi;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.Topbar;
import com.ssc.weipan.model.BaseModel;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.churujin_hisotry_layout);

        ButterKnife.bind(this, this);

        mTopbar.setTitle("出入金记录");
//        mListView.setAdapter(new YouHuiQuanAdapter(this));

        loadData();
    }

    private void loadData() {


        UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
        iUser.getChuRuJinHistory(new Callback<BaseModel>() {
            @Override
            public void success(BaseModel baseModel, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }
}
