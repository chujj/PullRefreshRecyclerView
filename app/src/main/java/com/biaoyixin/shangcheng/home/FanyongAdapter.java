package com.biaoyixin.shangcheng.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.broker.BrokerApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by zhujj on 17-11-25.
 */
public class FanyongAdapter extends RecyclerView.Adapter{
    private List<BrokerApi.FanyongItem> mData = new ArrayList<>();

    public List<BrokerApi.FanyongItem> getData() {
        return mData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.zhishu_list_item, parent, false);
        return new MyHolder(root);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyHolder)holder).updateUI(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyHolder extends RecyclerView.ViewHolder {


        @BindViews({R2.id.t1, R2.id.t2, R2.id.t3, R2.id.t4, })
        TextView[] ts;

        public MyHolder(View itemView) {
            super(itemView);


            ButterKnife.bind(this, itemView);
        }

        public void updateUI(BrokerApi.FanyongItem s) {
            ts[0].setText(s.nickname);
            ts[1].setText(s.goodsName);
            ts[2].setText("" + s.earnedServeFee);
            ts[3].setText(s.closeTime);
        }
    }
}
