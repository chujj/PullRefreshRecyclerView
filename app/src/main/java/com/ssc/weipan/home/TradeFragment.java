package com.ssc.weipan.home;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhujj on 17-10-18.
 */
public class TradeFragment extends BaseFragment {

    @BindView(R2.id.timeLineView)
    InteractiveKLineView mTimelineView;

    @BindView(R2.id.kline_container)
    ViewGroup mContainer;

    @BindViews({R2.id.time_180, R2.id.time_60, R2.id.time_300})
    ViewGroup[] mTimes;


    @BindViews({R2.id.line_1, R2.id.line_2, R2.id.line_3, R2.id.line_4})
    ViewGroup[] mLinesIndicator;

    private EntrySet timeLineEntrySet = new EntrySet();
    private EntrySet mKlineEntrySet_5 = new EntrySet();
    private EntrySet mKlineEntrySet_10 = new EntrySet();
    private boolean mKLineTest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mKLineTest = getArguments().getBoolean("is_keyline_test", false);
        }
    }

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


        clickTime60();
        clickLine1();

        if (mKLineTest) {
            mTimelineView.setEntrySet(mKlineEntrySet_5);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onKLineDataChanged();
                }
            }, 2000);
        } else {
            mTimelineView.setEntrySet(timeLineEntrySet);
            mTimelineView.setRender(new TimeLineRender());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onTimelineDataChanged();
                }
            }, 2000);
        }

    }


    @OnClick(R2.id.time_180)
    public void clickTime180() {
        setTimeSelected(0);
    }

    @OnClick(R2.id.time_60)
    public void clickTime60() {
        setTimeSelected(1);
    }

    @OnClick(R2.id.time_300)
    public void clickTime300() {
        setTimeSelected(2);
    }

    private void setTimeSelected(int index) {
        for (int i = 0; i < mTimes.length; i++) {
            mTimes[i].setSelected(index == i);
        }
    }

    @OnClick(R2.id.line_1)
    public void clickLine1() {
        selectLineIndex(0);
    }

    @OnClick(R2.id.line_2)
    public void clickLine2() {
        selectLineIndex(1);
    }

    @OnClick(R2.id.line_3)
    public void clickLine3() {
        selectLineIndex(2);
    }

    @OnClick(R2.id.line_4)
    public void clickLine4() {
        selectLineIndex(3);
    }

    private void selectLineIndex(int index) {
        for (int i = 0; i < mLinesIndicator.length; i++) {
            for (int j = 0; j < mLinesIndicator[i].getChildCount(); j++) {
                View view = mLinesIndicator[i].getChildAt(j);
                if (view instanceof TextView) {

                    ((TextView)view).setTextColor(index == i ? 0xFFEBAD33 : 0xFFCCCCCC);
                } else if (view instanceof View) {
                    view.setVisibility( index == i ? View.VISIBLE : View.INVISIBLE);
                }
            }
        }

    }

    private void onKLineDataChanged() {

        String data = "";
        EntrySet entrySet = new EntrySet();
        {
            String kLineData = "";
            try {
                InputStream in = getResources().getAssets().open("kline1.txt");
                int length = in.available();
                byte[] buffer = new byte[length];
                in.read(buffer);
                kLineData = new String(buffer, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            data = kLineData;
        }


        {
            entrySet = new EntrySet();

            final String[] candleDatas = data.split(",");

            for (String candleData : candleDatas) {
                String[] v = candleData.split("[|]");

                float open = Float.parseFloat(v[0]);
                float high = Float.parseFloat(v[1]);
                float low = Float.parseFloat(v[2]);
                float close = Float.parseFloat(v[3]);

                int volume = Integer.parseInt(v[4]);

                entrySet.addEntry(new Entry(open, high, low, close, volume, v[5]));
            }
        }


        mKlineEntrySet_5.addEntries(entrySet.getEntryList().subList(5500, 6000));
        mKlineEntrySet_5.computeStockIndex();

        mTimelineView.notifyDataSetChanged();
    }


    public void onTimelineDataChanged() {
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
            timeLineEntrySet.addEntry(entry);
        }
        timeLineEntrySet.getEntryList().get(0).setXLabel("09:30");
        timeLineEntrySet.getEntryList().get(2).setXLabel("11:30/13:00");
        timeLineEntrySet.getEntryList().get(4).setXLabel("15:00");

        mTimelineView.notifyDataSetChanged();
    }
}
