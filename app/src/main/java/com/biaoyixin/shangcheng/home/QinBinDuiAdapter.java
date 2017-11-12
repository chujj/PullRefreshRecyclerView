package com.biaoyixin.shangcheng.home;

import android.content.Intent;
import android.text.TextUtils;
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
 * Created by zhujj on 17-11-9.
 */
public class QinBinDuiAdapter extends BaseAdapter {


    private List mData = new ArrayList();

    public void setData(List<UserApi.Broker> datas) {
        mData.clear();
        mData.addAll(datas);
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
        if (convertView == null ) {
            final View viewRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.qin_bin_dui_list_item, parent, false);
            convertView = viewRoot;

            final View gap = CommonUtils.findView(convertView, R.id.gap);
            final TextView name = CommonUtils.findView(convertView, R.id.name);
            final TextView phone = CommonUtils.findView(convertView, R.id.phone);


            convertView.setTag(new ClosureMethod() {
                @Override
                public Object[] run(Object... args) {
                    int pos = (int) args[1];
                    final UserApi.Broker broker = (UserApi.Broker) args[0];

                    gap.setVisibility(pos == 0 ? View.VISIBLE : View.GONE);


                    name.setText(broker.name);
                    if (TextUtils.isEmpty(broker.name)) {
                        name.setVisibility(View.GONE);
                    } else {
                        name.setVisibility(View.VISIBLE);
                    }


                    phone.setText(broker.phone);


                    viewRoot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent it = new Intent(viewRoot.getContext(),
                                    TradeHistoryActivity.class);
                            it.putExtra("customerId", broker.id);
                            viewRoot.getContext().startActivity(it);
                        }
                    });

                    return null;
                }
            });
        }


        ((ClosureMethod)convertView.getTag()).run(getItem(position), position);

        return convertView;
    }
}