package com.biaoyixin.shangcheng.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.user.UserApi;
import com.biaoyixin.shangcheng.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-11-19.
 */
public class YouHuiQuanFragment extends BaseFragment {

    @BindView(R2.id.weihebing)
    TextView mWeiHeBing;
    @BindView(R2.id.yihebing)
    TextView mYiHeBing;


    @BindView(R2.id.empty_view)
    View mEmptyView;
    @BindView(R2.id.listview)
    ListView mListView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.youhuiquan_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);


        loadData();
    }

    private void loadData() {
        UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
        iUser.getYouHuiQuanV2(new Callback<UserApi.YHQ2Resp>() {
            @Override
            public void success(UserApi.YHQ2Resp resp, Response response) {
                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);
                } else {
                    refreshUI(resp);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });
    }

    private void refreshUI(UserApi.YHQ2Resp resp) {

        mWeiHeBing.setText("未合并券: " + resp.data.left + "元");
        mYiHeBing.setText("已合并券: " + resp.data.used + "元");



        if (resp.data.coupon_list == null || resp.data.coupon_list.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);


            DangDangYouHuiQuanAdapter adapter = new DangDangYouHuiQuanAdapter(resp.data.coupon_list);
            mListView.setAdapter(adapter);
        }

    }



    @OnClick(R2.id.lingqu)
    public void lingqu() {
        Intent it = new Intent(this.getContext(), HomeActivity.class);
        it.putExtra("index", 2);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(it);
    }

}
