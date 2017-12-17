package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.Topbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by zhujj on 17-12-17.
 */

public class MessagesActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;
    @BindView(R2.id.listview)
    ListView mListView;
    private MessagesAdapter mMessageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.messages_activity);

        ButterKnife.bind(this, this);

        mTopbar.setTitle("消息中心");
        mTopbar.setBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        EventBus.getDefault().register(this);
        mListView.setAdapter(mMessageAdapter = new MessagesAdapter());

        MessagesCenter.fetchData(true, false);
    }

    public void onEventMainThread(MessagesCenter.MessageGetEvent event) {
        mMessageAdapter.getData();
        mMessageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
