package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biaoyixin.shangcheng.Consts;
import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.trade.GoodsApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.BaseFragment;
import com.biaoyixin.shangcheng.base.ClosureMethod;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.wordplat.ikvstockchart.InteractiveKLineView;
import com.wordplat.ikvstockchart.entry.Entry;
import com.wordplat.ikvstockchart.entry.EntrySet;
import com.wordplat.ikvstockchart.render.TimeLineRender;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

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


    @BindView(R2.id.trades_container)
    ViewGroup mTradesContainer;

    @BindView(R2.id.kline_container)
    ViewGroup mKlineContainer;


    ViewGroup[] mKLineViews;
    EntrySet[] mEntrySets;


    private final static int ViewSize = 4;

    private String mKey;

    private List<ClosureMethod> mUIUpdates = new ArrayList<>();
    private ClosureMethod mTradingUpdater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mKey = getArguments().getString("key");

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(Message msg) {
        if (msg.what != Consts.BoardCast_PriceMsg
                && msg.what != Consts.BoardCast_TradingListChange
                && msg.what != Consts.BoardCast_TradeClose) return;


        if (msg.what == Consts.BoardCast_PriceMsg || msg.what == Consts.BoardCast_TradeClose) {
            for(ClosureMethod call : mUIUpdates) {
                call.run();
            }
        } else if (msg.what == Consts.BoardCast_TradingListChange) {
            refreshTradingList();
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

        // init privates
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
                klineView.setEnableLeftRefresh(false);
                klineView.setEnableRightRefresh(false);
                klineView.setRender(new TimeLineRender());
            } else {
                newView = (ViewGroup) inflater.inflate(R.layout.trade_kline_layout, mKlineContainer, false);
                mKlineContainer.addView(newView);
                mKLineViews[i] = newView;

                InteractiveKLineView klineView = CommonUtils.findView(mKLineViews[i], R.id.timeLineView);
                klineView.setEntrySet(mEntrySets[i]);
                klineView.setEnableLeftRefresh(false);
                klineView.setEnableRightRefresh(false);
//                klineView.setRender(new TimeLineRender());
            }

            mKLineViews[i].setVisibility(View.GONE);
        }


        // 时间初始化
        for (int i = 0; i < mTimeDetail.length; i++) {
            GoodsApi.GoodName gName = Data.sData.names.get(mKey);
            mTimeDetail[i].setText(String.format("%s秒", i > (gName.point.length - 1) ? 0 : gName.point[i]));
        }


        ClosureMethod priceUpdater = new ClosureMethod() {
            @Override
            public Object[] run(Object... args) {
                // 高低价初始化
                for (int i = 0; i < Data.sData.goods.size(); i++) {
                    if (TextUtils.equals(Data.sData.goods.get(i).label, mKey)) {
                        mOpenText.setText(String.format("今开：%.2f", Data.sData.goods.get(i).open));
                        mHighText.setText(String.format("最高：%.2f", Data.sData.goods.get(i).high));
                        mLowText.setText(String.format("最低：%.2f", Data.sData.goods.get(i).low));
                        break;
                    }
                }
                return null;
            }
        };
        priceUpdater.run();
        mUIUpdates.add(priceUpdater);


        // default selection
        clickTime60();
        clickLine1();


        ClosureMethod dataUpdate = new ClosureMethod() {
            @Override
            public Object[] run(Object... args) {
                // 初始化表单
                List<GoodsApi.ChartData> mChardata = new ArrayList<>();
                for (int i = 0; i < Data.sData.charts.size(); i++) {
                    if (TextUtils.equals(Data.sData.charts.get(i).label, mKey)) {
                        mChardata = Data.sData.charts.get(i).list;
                        break;
                    }
                }

                // load data
                for (int i = 0; i < mLinesDetail.length; i++) {
                    if (i > (mChardata.size() -1)) {
                        mLinesIndicator[i].setVisibility(View.GONE);
                    } else {
                        mLinesDetail[i].setText(mChardata.get(i).name);


                        if (mChardata.get(i).data.size() == 0) continue;

                        // load data into set

                        boolean isTimelineChart = (mChardata.get(i).data.get(0) instanceof Double);


                        mEntrySets[i].getEntryList().clear();
//                        System.out.println(String.format("size %d %d %d %d",
//                                mEntrySets[0].getEntryList().size(),
//                                mEntrySets[1].getEntryList().size(),
//                                mEntrySets[2].getEntryList().size(),
//                                mEntrySets[3].getEntryList().size()
//                        ));
                        if (isTimelineChart) {
                            for (int j = 0; j < mChardata.get(i).data.size(); j++) {
                                String xLabel = (new SimpleDateFormat("mm:ss")).format(new Date(mChardata.get(i).xAxis.get(j).longValue()));

                                Entry entry = new Entry(((Double) mChardata.get(i).data.get(j)).floatValue(), 0, xLabel);
                                mEntrySets[i].addEntry(entry);

                            }
                        } else {
                            if (i == 1) {
                                int size = mChardata.get(i).data.size();
//                                System.out.println("debug2: " + size + " : " + mChardata.get(i).data.get( mChardata.get(i).data.size() - 1));
                            }
                            for (int j = 0; j < mChardata.get(i).data.size(); j++) {
                                List _data = (List) mChardata.get(i).data.get(j);

                                float open = ((Double)_data.get(0)).floatValue();
                                float close =((Double)_data.get(1)).floatValue();
                                float high = ((Double)_data.get(2)).floatValue();
                                float low =  ((Double)_data.get(3)).floatValue();

                                String xLabel = (new SimpleDateFormat("HH:mm")).format(new Date(mChardata.get(i).xAxis.get(j).longValue()));
                                mEntrySets[i].addEntry(new Entry(open, high, low, close, 0, xLabel));

                            }

                            mEntrySets[i].computeStockIndex();

                        }

                    }
                }

                // update charts ui
                onDataReady();
                return null;
            }
        };

        dataUpdate.run();
        mUIUpdates.add(dataUpdate);



        refreshTradingList();

    }


    private void refreshTradingList() {

        mUIUpdates.remove(mTradingUpdater);

        final List<ClosureMethod> listUpdaters = new ArrayList<>();

        mTradesContainer.removeAllViews();
        for(GoodsApi.BuyTradeData btd : Data.sTradings) {
            if (TextUtils.equals(btd.label, mKey)) {
                View root =
                        LayoutInflater.from(getContext()).inflate(R.layout.trade_trades_header_layout, mTradesContainer, false);

                TextView name = CommonUtils.findView(root, R.id.name);
                TextView buyType = CommonUtils.findView(root, R.id.buy_type);
                TextView openTime = CommonUtils.findView(root, R.id.open_time);
                TextView openPrice = CommonUtils.findView(root, R.id.open_price);
                final TextView newPrice = CommonUtils.findView(root, R.id.new_price);
                TextView dingJin = CommonUtils.findView(root, R.id.ding_jin);

                name.setText(btd.goods_name);
                buyType.setText(btd.up_down_type == 0 ? "买涨" : "买跌");
                buyType.setTextColor(btd.up_down_type == 0 ? 0xFFF35833 : 0xFF2CB545);
                openTime.setText(btd.open_time);
                openPrice.setText(btd.open_price + "");
                newPrice.setText(btd.close_price + "");
                dingJin.setText(btd.chip);


                listUpdaters.add(new ClosureMethod() {
                    @Override
                    public Object[] run(Object... args) {
                        for (int i = 0; i < Data.sData.goods.size(); i++) {

                            if (TextUtils.equals(Data.sData.goods.get(i).label, mKey)) {
                                newPrice.setText(Data.sData.goods.get(i).newPrice + "");
                            }

                        }
                        
                        return null;
                    }
                });

                mTradesContainer.addView(root);
            }
        }


        mTradingUpdater = new ClosureMethod() {
            @Override
            public Object[] run(Object... args) {

                for(ClosureMethod call : listUpdaters) {
                    call.run();
                }

                return null;
            }
        };
        mUIUpdates.add(mTradingUpdater);
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


    @OnClick(R2.id.buy_up)
    public void clickBuyUp() {
        showBuyLayout(true);
    }

    @OnClick(R2.id.buy_down)
    public void clickBuyDown() {
        showBuyLayout(false);
    }

    private void showBuyLayout(boolean up) {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.trade_buy_layout, null, false);
        BuyTradeView btv = CommonUtils.findView(root, R.id.buytradeview);
        btv.setKey(mKey);
        btv.setActivity((BaseActivity) getActivity());
        btv.setUpDown(up);
        btv.setTimeIntervalProvider(new BuyTradeView.TimeIntervalProvider() {
            @Override
            public String timeInterval() {
                for (int i = 0; i < mTimes.length; i++) {
                    if (mTimes[i].isSelected()) {
                        return mTimeDetail[i].getText().toString();
                    }
                }
                return "";
            }
        });
        btv.initUI();

        CommonUtils.addToActivity(getActivity(), root);
    }

}