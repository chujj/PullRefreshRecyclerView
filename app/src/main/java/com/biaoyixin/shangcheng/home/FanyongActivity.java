package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.broker.BrokerApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.Topbar;
import com.bigkoo.pickerview.TimePickerView;
import com.ssc.widget.pullrefreshrecyclerview.AutoLoadMoreRecyclerView;
import com.ssc.widget.pullrefreshrecyclerview.PullRefreshRecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
public class FanyongActivity extends BaseActivity {


    @BindView(R2.id.count)
    TextView mCount;
    @BindView(R2.id.left)
    TextView mLeft;
    @BindView(R2.id.right)
    TextView mRight;
    @BindView(R2.id.loadmore_listview)
    AutoLoadMoreRecyclerView mListview;
    @BindView(R2.id.topbar)
    Topbar mTopbar;
    @BindView(R2.id.header_layout)
    View mHeaderLayout;
    private FanyongAdapter mAdapter;

    private int mPage = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.fanyong_activity);

        ButterKnife.bind(this, this);

        String price = getIntent().getStringExtra("price");
        SpannableString ss = new SpannableString("返佣总金额 " + price + " , 注:以实际到账为准");
        ss.setSpan(new ForegroundColorSpan(0xfff35833), 6, 6 + price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mCount.setText(ss);

        mTopbar.setTitle("直属客户");
        mTopbar.setBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date(System.currentTimeMillis()));
        mLeft.setText(today + " 00:00");
        mRight.setText(today + " 23:59");


        mAdapter = new FanyongAdapter();

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
        iBroker.getFanyong(mPage, 20, mLeft.getText().toString(), mRight.getText().toString(), new Callback<BrokerApi.FanyongResp>() {
            @Override
            public void success(BrokerApi.FanyongResp resp, Response response) {
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


    private void refreshUI(BrokerApi.FanyongInfo data, int pageNum) {
        List<BrokerApi.FanyongItem> items =  mAdapter.getData();
        if (pageNum == 0) {
            items .clear();
        }


        if ((pageNum + 1) >= data.totalPages) {
            mListview.setAutoLoadMoreEnable(false);
        }

        mPage = pageNum;


        items.addAll(data.content);
        mAdapter.notifyDataSetChanged();
    }


    @OnClick(R2.id.left)
    public void clickLeft() {
        showDateDialog(mLeft);
    }

    @OnClick(R2.id.right)
    public void clickRight() {
        showDateDialog(mRight);
    }

    private void showDateDialog(final TextView tv) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = sdf.parse(tv.getText().toString());

            TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date,View v) {//选中事件回调
                    tv.setText(sdf.format(date));
                }
            })
                    .setCancelText("取消")
                    .setSubmitText("确定")
                    .build();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            pvTime.setDate(cal);
            pvTime.show();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @OnClick(R2.id.swap)
    public void clickSwap() {
        String tmp = mRight.getText().toString();
        mRight.setText(mLeft.getText().toString());
        mLeft.setText(tmp);
    }


    @OnClick(R2.id.search)
    public void clickSearch() {
        mListview.setAutoLoadMoreEnable(true);
        loadData(0);
    }

}
