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

import java.util.List;

/**
 * Created by zhujj on 17-11-22.
 */
public class DangDangYouHuiQuanAdapter extends BaseAdapter {


    private final List<UserApi.YHQ2> mData;

    public DangDangYouHuiQuanAdapter(List<UserApi.YHQ2> coupon_list) {
        mData = coupon_list;
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dangdang_youhui_quan_list_item, parent, false);

            final TextView name = CommonUtils.findView(convertView, R.id.name);
            final TextView price = CommonUtils.findView(convertView, R.id.price);


            convertView.setTag(new ClosureMethod() {
                @Override
                public Object[] run(Object... args) {
                    UserApi.YHQ2 yhq = (UserApi.YHQ2) args[0];

                    name.setText(yhq.name);
                    price.setText("￥" + yhq.value);

                    return null;
                }
            });
        }



        ((ClosureMethod) convertView.getTag()).run(getItem(position));

        return convertView;
    }
}
