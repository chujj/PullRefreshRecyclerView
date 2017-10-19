package com.ssc.weipan.home;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.base.BaseFragment;
import com.wordplat.ikvstockchart.InteractiveKLineView;
import com.wordplat.ikvstockchart.entry.Entry;
import com.wordplat.ikvstockchart.entry.EntrySet;
import com.wordplat.ikvstockchart.render.TimeLineRender;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhujj on 17-10-18.
 */
public class TradeFragment extends BaseFragment {

    @BindView(R2.id.timeLineView)
    InteractiveKLineView mTimelineView;

    private final EntrySet entrySet = new EntrySet();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trade_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ButterKnife.bind(this, view);

        mTimelineView.setEntrySet(entrySet);
        mTimelineView.setRender(new TimeLineRender());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onDataChanged();
            }
        }, 2000);
    }


    public void onDataChanged() {
        List<BtcBean>  beans = null;
        try {
            beans = new Gson().fromJson(new InputStreamReader(getContext().getAssets().open("timeline.json")),
                    new TypeToken<List<BtcBean>>() {}.getType());

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (beans == null) {
            return;
        }

        for (BtcBean btcBean : beans ) {
            Entry entry = new Entry(btcBean.price, (int) btcBean.amount, "");
            entrySet.addEntry(entry);
        }
        entrySet.getEntryList().get(0).setXLabel("09:30");
        entrySet.getEntryList().get(2).setXLabel("11:30/13:00");
        entrySet.getEntryList().get(4).setXLabel("15:00");

        mTimelineView.notifyDataSetChanged();
    }
}
