package com.biaoyixin.shangcheng.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.api.trade.GoodsApi;
import com.biaoyixin.shangcheng.base.ClosureMethod;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujj on 17-11-23.
 */
public class TihuoHistoryAdapter extends BaseAdapter {
    private List<GoodsApi.TihuoRecord> mData = new ArrayList<>();

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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null ) {


            final Context contex = parent.getContext();

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tihuo_history_list_item, parent, false);


            final ImageView image = CommonUtils.findView(convertView, R.id.image);
            final TextView name = CommonUtils.findView(convertView, R.id.name);
            final TextView count = CommonUtils.findView(convertView, R.id.count);
            final TextView price = CommonUtils.findView(convertView, R.id.price);
            final TextView addr = CommonUtils.findView(convertView, R.id.addr);
            final TextView receiver = CommonUtils.findView(convertView, R.id.receiver);
            final TextView phonenum = CommonUtils.findView(convertView, R.id.phonenum);

            final TextView r1 = CommonUtils.findView(convertView, R.id.r_1);
            final TextView r2 = CommonUtils.findView(convertView, R.id.r_2);
            final TextView r3 = CommonUtils.findView(convertView, R.id.r_3);
            final TextView r4 = CommonUtils.findView(convertView, R.id.r_4);
            final TextView r5 = CommonUtils.findView(convertView, R.id.r_5);

            final TextView orderTime = CommonUtils.findView(convertView, R.id.order_time);
            final TextView numDesc = CommonUtils.findView(convertView, R.id.num_desc);
            final TextView priceTotal = CommonUtils.findView(convertView, R.id.price_total);


            convertView.setTag(new ClosureMethod() {
                @Override
                public Object[] run(Object... args) {
                    GoodsApi.TihuoRecord record = (GoodsApi.TihuoRecord) args[0];


                    Glide.with(contex).load(record.goodsIcon).into(image);
                    name.setText(record.goodsName);
                    count.setText("数量： " + record.amount);
                    price.setText("￥" + record.realPrice);


                    addr.setText("收货地址： " + record.deliverAddress);
                    receiver.setText("收货人： " + record.deliverName);
                    phonenum.setText("联系电话： " + record.deliverPhone);


                    String status_str = "未知";
                    String[] status = new String [] {
                            "未发货",
                            "已发货",
                            "已签收",
                            "已取消",
                    };
                    if (record.deliverStatus >= 0 && record.deliverStatus < status.length) {
                        status_str = status[(int) record.deliverStatus];
                    }
                    r1.setText("状态" + status_str);
                    r2.setText("￥" + record.totalPrice);
                    r3.setText("￥" + record.deliverPrice);
                    r4.setText("￥" + record.credit);
                    r5.setText(record.delivernNumber);

                    orderTime.setText("下单时间：" + record.createdOn);
                    numDesc.setText("共计" + record.amount + "件商品 合计");
                    priceTotal.setText("￥" + record.totalPrice);

                    return null;
                }
            });
        }

        ((ClosureMethod)convertView.getTag()).run(getItem(position));
        return convertView;
    }

    public void setData(List<GoodsApi.TihuoRecord> data) {
        mData = data;
    }
}
