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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujj on 17-10-24.
 */
public class YouHuiQuanAdapter extends BaseAdapter {

    private List<UserApi.Youhuiquan> mData = new ArrayList<>();

    public YouHuiQuanAdapter(YouHuiQuanActivity youHuiQuanActivity) {
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
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.youhuiquan_list_item, parent, false);

            final TextView type = CommonUtils.findView(convertView, R.id.type);
            final TextView price = CommonUtils.findView(convertView, R.id.price);
            final TextView line1 = CommonUtils.findView(convertView, R.id.line1);
            final TextView line2 = CommonUtils.findView(convertView, R.id.line2);

            convertView.setTag(new ClosureMethod() {
                @Override
                public Object[] run(Object... args) {
                    UserApi.Youhuiquan youhuiquen = (UserApi.Youhuiquan) args[0];


                    type.setText(youhuiquen.couponType == 1 ? "增\n益\n券" : " 体\n验\n券");
                    price.setText(youhuiquen.discount + "");
                    line1.setText(String.format("%s  有效登录时间： %s天",
                            new SimpleDateFormat("yyyy年MM月dd日").format(youhuiquen.gmtCreated),
                            youhuiquen.validDays));
                    return null;
                }
            });
        }



        ((ClosureMethod)convertView.getTag()).run(getItem(position));

        return convertView;
    }

    public void setData(List<UserApi.Youhuiquan> data) {
        mData = data;
    }
}
