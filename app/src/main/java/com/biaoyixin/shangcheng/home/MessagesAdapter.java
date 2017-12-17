package com.biaoyixin.shangcheng.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.api.user.UserApi;
import com.biaoyixin.shangcheng.base.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhujj on 17-12-17.
 */

public class MessagesAdapter extends BaseAdapter {

    private final static int TYPE_NORMAL = 0;
    private final static int TYPE_EMPTY = 1;
    private final static int TYPE_DIVIDER = 2;

    private boolean mInited = false;
    private long mNow;
    private List<UserApi.PushItem> mMessages;


    @Override
    public int getCount() {
        if (!mInited) {
            return 0;
        } else {
            return mMessages.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return ((UserApi.PushItem)getItem(position))._local_type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_NORMAL:
                convertView = getViewNormal(position, convertView, parent);
                break;
            case TYPE_EMPTY:
                convertView = getViewEmpty(position, convertView, parent);
                break;
            default:
        }
        return convertView;
    }

    private View getViewEmpty(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item_empty, parent, false);
        }

        return convertView;
    }

    private View getViewNormal(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item, parent, false);
        }

        TextView message = CommonUtils.findView(convertView, R.id.message);
        TextView time = CommonUtils.findView(convertView, R.id.time);

        UserApi.PushItem item = ((UserApi.PushItem)getItem(position));
        message.setText(item.message);

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        String today = sdf.format(new Date(item.gmtCreated));
        time.setText(today);

        return convertView;
    }

    public void getData() {
        mInited = true;
        mNow = MessagesCenter.sNow;
        mMessages = new ArrayList<>(MessagesCenter.sMessages);
        if (mMessages.size() == 0) {
            UserApi.PushItem pushItem = new UserApi.PushItem();
            pushItem._local_type = TYPE_EMPTY;
            mMessages.add(pushItem);
        }
    }


}
