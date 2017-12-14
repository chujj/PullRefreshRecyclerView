package com.biaoyixin.shangcheng.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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
import com.biaoyixin.shangcheng.login.AccountManager;
import com.biaoyixin.shangcheng.login.LoginActivity;
import com.wordplat.ikvstockchart.InteractiveKLineView;
import com.wordplat.ikvstockchart.drawing.TimeLineDrawing;
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



    @BindView(R2.id.kline_container)
    ViewGroup mKlineContainer;


    ViewGroup[] mKLineViews;
    EntrySet[] mEntrySets;




    private final static int ViewSize = 4;

    private String mKey;

    private List<ClosureMethod> mUIUpdates = new ArrayList<>();

    private TimeLineRender mTimeLineRender;


    private Handler mHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mKey = getArguments().getString("key");

        mHandler = new Handler();

        EventBus.getDefault().register(this);
    }


    private Runnable redrawTimeLineViewRunnable;

    @Override
    public void onResume() {
        super.onResume();
        if (redrawTimeLineViewRunnable != null) {
            mHandler.post(redrawTimeLineViewRunnable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (redrawTimeLineViewRunnable != null) {
            mHandler.removeCallbacks(redrawTimeLineViewRunnable);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (redrawTimeLineViewRunnable != null){
            if (hidden) {
                mHandler.removeCallbacks(redrawTimeLineViewRunnable);
            } else {
                mHandler.post(redrawTimeLineViewRunnable);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(Message msg) {

        if (msg.what == Consts.BoardCast_PriceMsg || msg.what == Consts.BoardCast_TradeClose) {
            for(ClosureMethod call : mUIUpdates) {
                call.run();
            }
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

                final InteractiveKLineView klineView = CommonUtils.findView(mKLineViews[i], R.id.timeLineView);
                klineView.setEntrySet(mEntrySets[i]);
                klineView.setEnableLeftRefresh(false);
                klineView.setEnableRightRefresh(false);
                klineView.setRender(mTimeLineRender = new TimeLineRender());
                redrawTimeLineViewRunnable = new Runnable() {
                    @Override
                    public void run() {
                        klineView.postInvalidate();
                    }
                };
                mTimeLineRender.getTimeLineDrawing().drawAfterCb = new Runnable() {
                    @Override
                    public void run() {
                        redrawTimeLineViewRunnable.run();
                    }
                };
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
            mTimeDetail[i].setText(String.format("%s秒", (gName.point == null || i > (gName.point.length - 1)) ? 0 : gName.point[i]));
        }


        ClosureMethod priceUpdater = new ClosureMethod() {
            @Override
            public Object[] run(Object... args) {
                // 高低价初始化
                for (int i = 0; i < Data.sData.goods.size(); i++) {
                    if (TextUtils.equals(Data.sData.goods.get(i).label, mKey)) {

                        {
                            String str = "今开：" + CommonUtils.trimFloat(Data.sData.goods.get(i).open);
                            SpannableString ss = new SpannableString(str);
                            ss.setSpan(new ForegroundColorSpan(0xff333333), 3, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mOpenText.setText(ss);
                        }

                        {
                            String str = "最高：" + CommonUtils.trimFloat(Data.sData.goods.get(i).high);
                            SpannableString ss = new SpannableString(str);
                            ss.setSpan(new ForegroundColorSpan(0xff333333), 3, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mHighText.setText(ss);
                        }

                        {
                            String str = "最低：" + CommonUtils.trimFloat(Data.sData.goods.get(i).low);
                            SpannableString ss = new SpannableString(str);
                            ss.setSpan(new ForegroundColorSpan(0xff333333), 3, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mLowText.setText(ss);
                        }

                        break;
                    }
                }
                return null;
            }
        };
        priceUpdater.run();
        mUIUpdates.add(priceUpdater);

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


                        if (mLineIndex != i) continue;
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

                                float open = _data.get(0) == null ? 0 : ((Double)_data.get(0)).floatValue();
                                float close = _data.get(1) == null ? 0 : ((Double)_data.get(1)).floatValue();
                                float low = _data.get(2) == null ? 0 : ((Double)_data.get(2)).floatValue();
                                float high = _data.get(3) == null ? 0 : ((Double)_data.get(3)).floatValue();

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


        // default selection
        clickTime60();
        clickLine1();

    }




    public void onDataReady() {
        for (int i = 0; i < mKLineViews.length; i++) {
            InteractiveKLineView kLineView = CommonUtils.findView(mKLineViews[i], R.id.timeLineView);
            kLineView.notifyDataSetChanged();
        }

        if (redrawTimeLineViewRunnable != null) {
            redrawTimeLineViewRunnable.run();
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

    private int mLineIndex ;
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

                mLinesIndicator[i].setBackgroundColor(index == i ? 0xffffffff : 0xfff3f3f3);
                if (view instanceof TextView) {

                    ((TextView)view).setTextColor(index == i ? 0xff2093ec : 0xff2093ec);
                } else if (view instanceof View) {
                    view.setVisibility( index == i ? View.INVISIBLE : View.INVISIBLE);
                }
            }


            mKLineViews[i].setVisibility(index == i ? View.VISIBLE : View.GONE);
        }


        if (redrawTimeLineViewRunnable != null) {
            if (index == 0) {
                mHandler.post(redrawTimeLineViewRunnable);
            } else {
                mHandler.removeCallbacks(redrawTimeLineViewRunnable);
            }
        }

        mLineIndex  = index;
        for(ClosureMethod call : mUIUpdates) {
            call.run();
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
        if (!AccountManager.isLogin()) {
            Intent it = new Intent(this.getContext(), LoginActivity.class);
            this.startActivity(it);
            return;
        }

        View root = LayoutInflater.from(getContext()).inflate(R.layout.trade_buy_layout, null, false);
        BuyTradeView btv = CommonUtils.findView(root, R.id.buytradeview);
        btv.setKey(mKey);
        btv.setActivity((BaseActivity) getActivity());
        btv.setUpDown(up);

        btv.mTradeCB = new ClosureMethod() {
            @Override
            public Object[] run(Object... args) {
                GoodsApi.BuyTradeData trade = (GoodsApi.BuyTradeData) args[0];
                markTrade(trade);
                return null;
            }
        };
        btv.initUI();

        CommonUtils.addToActivity(getActivity(), root);
    }

    private void markTrade(final GoodsApi.BuyTradeData trade) {
        if (trade == null) {
            return;
        }

        mTimeLineRender.getTimeLineDrawing().mPriceMarkerProvider = new TimeLineDrawing.PriceMarkerProvider() {

            float[] win_line = new float[] {0, trade.open_price + (trade.up_down_type == 0 ? trade.stop_win_percent : -trade.stop_win_percent)};
            float[ ] loss_line = new float[] {0, trade.open_price + (trade.up_down_type == 0 ? -trade.stop_loss_percent : trade.stop_loss_percent)};
            float [] cache = new float[2];

//            String promt = "建仓价:" + trade.open_price;

            @Override
            public boolean needDraw() {
                if (Data.sData._closeTrade.containsKey(Long.valueOf(trade.trade_id))) {
                    return false;
                }
                return true;
            }

            @Override
            public float[] getLine1Y() {
                cache[0] = win_line[0];
                cache[1] = win_line[1];
                return cache;
            }

            @Override
            public float[] getLine2Y() {
                cache[0] = loss_line[0];
                cache[1] = loss_line[1];
                return cache;
            }

            @Override
            public String getLine1Promt() {
                return "止盈";
            }

            @Override
            public String getLine2Promt() {
                return "止损";
            }
        };
    }

}
