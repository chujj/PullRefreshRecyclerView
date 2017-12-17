package com.biaoyixin.shangcheng.home;

import android.text.TextUtils;
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
    public int getViewTypeCount() {
        return 3;
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
            case TYPE_DIVIDER:
                convertView = getViewDivider(position, convertView, parent);
                break;
            default:
        }
        return convertView;
    }

    private View getViewDivider(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_item_divider, parent, false);

        }
        TextView tv = CommonUtils.findView(convertView, R.id.text);
        UserApi.PushItem item = ((UserApi.PushItem)getItem(position));
        tv.setText(item.message);

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
        List<UserApi.PushItem> _temp = new ArrayList<>(MessagesCenter.sMessages);
        if (_temp .size() == 0) {
            UserApi.PushItem pushItem = new UserApi.PushItem();
            pushItem._local_type = TYPE_EMPTY;
            _temp .add(pushItem);
        } else {
            boolean hasToday = false;
            boolean hasBefor = false;
            int lastTodayPos = 0;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String todayf = sdf.format(new Date(System.currentTimeMillis()));
            for (int i = 0; i < _temp .size(); i++) {
                UserApi.PushItem item = _temp .get(i);

                if (TextUtils.equals(sdf.format(item.gmtCreated), todayf )) {
                    hasToday = true;
                    lastTodayPos = i;
                } else {
                    hasBefor = true;
                }
            }


            if (hasBefor) {
                UserApi.PushItem before = new UserApi.PushItem();
                before ._local_type = TYPE_DIVIDER;
                before .message = "更早";
                _temp .add(lastTodayPos + (hasToday ? 1 : 0), before);
            }

            if (hasToday) {
                UserApi.PushItem today = new UserApi.PushItem();
                today._local_type = TYPE_DIVIDER;
                today.message = "今日";
                _temp .add(0, today);
            }



        }


        mMessages  = _temp;
    }


}
