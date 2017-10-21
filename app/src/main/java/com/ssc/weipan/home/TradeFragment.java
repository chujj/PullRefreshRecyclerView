package com.ssc.weipan.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.trade.GoodsApi;
import com.ssc.weipan.base.BaseFragment;
import com.ssc.weipan.base.CommonUtils;
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

    @BindViews({R2.id.time_180, R2.id.time_60, R2.id.time_300})
    ViewGroup[] mTimes;
    @BindViews({R2.id.time1, R2.id.time2, R2.id.time3, })
    TextView[] mTimeDetail;


    @BindView(R2.id.open_text)
    TextView mOpenText;
    @BindView(R2.id.high_text)
    TextView mHighText;
    @BindView(R2.id.low_text)
    TextView mLowText;

    @BindViews({R2.id.line_1, R2.id.line_2, R2.id.line_3, R2.id.line_4})
    ViewGroup[] mLinesIndicator;
    @BindViews({R2.id.line_detail_1, R2.id.line_detail_2, R2.id.line_detail_3, R2.id.line_detail_4,})
    TextView[] mLinesDetail;


    @BindView(R2.id.kline_container)
    ViewGroup mKlineContainer;


    ViewGroup[] mKLineViews;
    EntrySet[] mEntrySets;


    private final static int ViewSize = 4;

    private String mKey;

    private List<GoodsApi.ChartData> mChardata;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mKey = getArguments().getString("key");
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

        mEntrySets = new EntrySet[ViewSize];
        for (int i = 0; i < mEntrySets.length; i++) {
            mEntrySets[i] = new EntrySet();
        }

        mKLineViews = new ViewGroup[ViewSize];
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < mKLineViews.length; i++) {
            ViewGroup newView = null;
            if (i == 0) {
                newView = (ViewGroup) inflater.inflate(R.layout.trade_timeline_layout, mKlineContainer, false);
                mKlineContainer.addView(newView);
                mKLineViews[i] = newView;

                InteractiveKLineView klineView = CommonUtils.findView(mKLineViews[i], R.id.timeLineView);
                klineView.setEntrySet(mEntrySets[i]);
                klineView.setRender(new TimeLineRender());
            } else {
                newView = (ViewGroup) inflater.inflate(R.layout.trade_kline_layout, mKlineContainer, false);
                mKlineContainer.addView(newView);
                mKLineViews[i] = newView;

                InteractiveKLineView klineView = CommonUtils.findView(mKLineViews[i], R.id.timeLineView);
                klineView.setEntrySet(mEntrySets[i]);
//                klineView.setRender(new TimeLineRender());
            }

            mKLineViews[i].setVisibility(View.GONE);
        }


        // 时间初始化
        for (int i = 0; i < mTimeDetail.length; i++) {
            GoodsApi.GoodName gName = Data.sData.names.get(mKey);
            mTimeDetail[i].setText(String.format("%s秒", i > (gName.point.length - 1) ? 0 : gName.point[i]));
        }

        // 高低价初始化
        for (int i = 0; i < Data.sData.goods.size(); i++) {
            if (TextUtils.equals(Data.sData.goods.get(i).label, mKey)) {
                mOpenText.setText(String.format("今开：%.2f", Data.sData.goods.get(i).open));
                mHighText.setText(String.format("最高：%.2f", Data.sData.goods.get(i).high));
                mLowText.setText(String.format("最低：%.2f", Data.sData.goods.get(i).low));
                break;
            }
        }


        // 初始化表单
        for (int i = 0; i < Data.sData.charts.size(); i++) {
            if (TextUtils.equals(Data.sData.charts.get(i).label, mKey)) {
                mChardata = Data.sData.charts.get(i).list;
                break;
            }
        }

        for (int i = 0; i < mLinesDetail.length; i++) {
            if (i > (mChardata.size() -1)) {
                mLinesIndicator[i].setVisibility(View.GONE);
            } else {
                mLinesDetail[i].setText(mChardata.get(i).name);

                // load data into set

                boolean isTimelineChart = (mChardata.get(i).data.get(0) instanceof Double);


                if (isTimelineChart) {
                    for (int j = 0; j < mChardata.get(i).data.size(); j++) {
                        Entry entry = new Entry(((Double) mChardata.get(i).data.get(j)).floatValue(), 0, "");
                        mEntrySets[i].addEntry(entry);

                    }
                } else {
                    for (int j = 0; j < mChardata.get(i).data.size(); j++) {
                        List _data = (List) mChardata.get(i).data.get(j);

                        float open = ((Double)_data.get(0)).floatValue();
                        float high = ((Double)_data.get(2)).floatValue();
                        float low =  ((Double)_data.get(3)).floatValue();
                        float close =((Double)_data.get(1)).floatValue();
                        mEntrySets[i].addEntry(new Entry(open, high, low, close, 0, ""));

                    }

                    mEntrySets[i].computeStockIndex();

                }

            }
        }




        clickTime60();
        clickLine1();


//        loadData();
        onDataReady();
    }

    private void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < mEntrySets.length; i++) {
                    if (i == 0) {
                        mEntrySets[i].getEntryList().clear();
                        mEntrySets[i].getEntryList().addAll(onTimelineDataChanged().getEntryList());
                    } else {
                        mEntrySets[i].getEntryList().clear();
                        mEntrySets[i].getEntryList().addAll(onKLineDataChanged().getEntryList());
                        mEntrySets[i].computeStockIndex();
                    }
                }


                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        onDataReady();
                    }
                });

            }
        }).start();
    }


    public void onDataReady() {
        for (int i = 0; i < mKLineViews.length; i++) {
            InteractiveKLineView kLineView = CommonUtils.findView(mKLineViews[i], R.id.timeLineView);
            kLineView.notifyDataSetChanged();
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


            mKLineViews[i].setVisibility(index == i ? View.VISIBLE : View.GONE);
        }

    }

    private EntrySet onKLineDataChanged() {
        EntrySet timeLineEntrySet = new EntrySet();


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


        timeLineEntrySet.addEntries(entrySet.getEntryList().subList(5500, 6000));
        timeLineEntrySet.computeStockIndex();

        return timeLineEntrySet;
    }


    public EntrySet onTimelineDataChanged() {
        EntrySet timeLineEntrySet = new EntrySet();


        List<BtcBean>  beans = null;
        try {
            beans = new Gson().fromJson(new InputStreamReader(getContext().getAssets().open("timeline.json")),
                    new TypeToken<List<BtcBean>>() {}.getType());

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (beans == null) {
            return timeLineEntrySet;
        }

        for (BtcBean btcBean : beans ) {
            Entry entry = new Entry(btcBean.price, (int) btcBean.amount, "");
            timeLineEntrySet.addEntry(entry);
        }
        timeLineEntrySet.getEntryList().get(0).setXLabel("09:30");
        timeLineEntrySet.getEntryList().get(2).setXLabel("11:30/13:00");
        timeLineEntrySet.getEntryList().get(4).setXLabel("15:00");

        return timeLineEntrySet;
    }
}
