package com.biaoyixin.shangcheng.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.api.user.UserApi;
import com.biaoyixin.shangcheng.base.ClosureMethod;
import com.biaoyixin.shangcheng.base.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujj on 17-10-24.
 */
public class ChuRuJinAdapter extends BaseAdapter {

    private List<UserApi.ChuRuJin> mData = new ArrayList<>();

    public ChuRuJinAdapter(ChuRuJinHistoryActivity chuRuJinHistoryActivity) {
    }

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.churujin_list_item, parent, false);

            final TextView type = CommonUtils.findView(view, R.id.type);
            final TextView name = CommonUtils.findView(view, R.id.name);
            final TextView order_num = CommonUtils.findView(view, R.id.order_num);
            final TextView create_time = CommonUtils.findView(view, R.id.create_time);
            final TextView money = CommonUtils.findView(view, R.id.money);

            view.setTag(new ClosureMethod() {
                @Override
                public Object[] run(Object... args) {
                    UserApi.ChuRuJin data = (UserApi.ChuRuJin) args[0];

                    type.setText(data.changeType == 1 ? "充值" : "提现");
                    name.setText(data.title);
                    order_num.setText("订单号： " + data.orderNo);
                    create_time.setText(data.createdOn);
                    money.setText(data.money);

                    return new Object[0];
                }
            });
            convertView = view;
        }


        ((ClosureMethod) convertView.getTag())
                .run(getItem(position));

        return convertView;
    }

    public void setData(List<UserApi.ChuRuJin> data) {
        mData = data;
        this.notifyDataSetChanged();
    }
}
