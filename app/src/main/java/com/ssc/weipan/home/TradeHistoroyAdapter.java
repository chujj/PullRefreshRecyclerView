package com.ssc.weipan.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ssc.weipan.R;
import com.ssc.weipan.api.trade.GoodsApi;
import com.ssc.weipan.base.ClosureMethod;
import com.ssc.weipan.base.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujj on 17-10-24.
 */
public class TradeHistoroyAdapter extends BaseAdapter {
    private List<GoodsApi.BuyTradeData> mData = new ArrayList<>();

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<GoodsApi.BuyTradeData> open) {
        mData = open;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            View root = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.trade_history_list_item, parent, false);


            final TextView type = CommonUtils.findView(root, R.id.type);
            final TextView name = CommonUtils.findView(root, R.id.name);
//            final TextView amount = CommonUtils.findView(root, R.id.amount);
//            final TextView trade_detail = CommonUtils.findView(root, R.id.trade_detail);


            root.setTag(new ClosureMethod() {
                @Override
                public Object[] run(Object... args) {
                    GoodsApi.BuyTradeData data = (GoodsApi.BuyTradeData) args[0];


                    name.setText(data.goods_name);


                    return new Object[0];
                }
            });

            convertView = root;
        }



        ((ClosureMethod) convertView.getTag())
                .run(getItem(position));

        return convertView;
    }

}
