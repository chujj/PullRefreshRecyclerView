package com.biaoyixin.shangcheng.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biaoyixin.shangcheng.Consts;
import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.trade.GoodsApi;
import com.biaoyixin.shangcheng.api.user.UserApi;
import com.biaoyixin.shangcheng.base.BaseFragment;
import com.biaoyixin.shangcheng.base.ClosureMethod;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.biaoyixin.shangcheng.base.ToastHelper;
import com.biaoyixin.shangcheng.base.UnderlineIndicator;
import com.biaoyixin.shangcheng.login.AccountManager;
import com.biaoyixin.shangcheng.login.LoginActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-18.
 */
public class TradeHomeFragment extends BaseFragment {


    @BindView(R2.id.viewpager)
    ViewPager mViewPager;
    @BindView(R2.id.indicator)
    UnderlineIndicator mIndicator;

    @BindView(R2.id.avatar)
    ImageView mAvatar;
    @BindView(R2.id.assets)
    TextView mAssets;


    @BindView(R2.id.count_down_layout)
    ViewGroup mCountDownLayout;
    @BindView(R2.id.count_down_text)
    TextView mCountDownText;

    @BindView(R2.id.trades_layout)
    ViewGroup mTradesListLayout;


    @BindView(R2.id.ws_callback)
    WebView mWSWebview;


    @BindViews({R2.id.trade_list1, R2.id.trade_list2})
    ViewGroup[] mTradeLists;
    @BindView(R2.id.trades_container)
    ViewGroup mTradesContainer;
    @BindView(R2.id.trades_all_container)
    ViewGroup mTradesAllContainer;
    @BindViews({R2.id.trades_current_view_group, R2.id.trades_mine_view_group, })
    ViewGroup[] mTradesViewGroup;

    private Handler mHandler;

    private ClosureMethod mCurrentKey ;

    private Runnable mCounterDownOpenTimeIntervalRunnable = new Runnable() {


        private long lastTimeStamp = System.currentTimeMillis();

        private boolean mLastChange = false;

        @Override
        public void run() {

            long current = System.currentTimeMillis();
            boolean changed = false;
            for(GoodsApi.BuyTradeData btd : Data.sTradings) {
//                System.out.println(btd.open_time_interval + "");
                if (btd.open_time_interval < 0) {
                    continue;
                }


                float diff = (current - lastTimeStamp) / 1000f;
                btd.open_time_interval -= diff;


                changed  = true;
            }


            if (changed || mLastChange) {
                EventBus.getDefault().post(Consts.getBoardCastMessage(Consts.BoardCast_Trade_OpenTimeIntervalChange));
            }

            mLastChange = changed;
            lastTimeStamp = current;
            mHandler.postDelayed(this, 300);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);



        mHandler = new Handler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mWSWebview.removeJavascriptInterface("WebAPI");
        mWSWebview.destroy();

        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(AccountManager.Account account) {
        Glide.with(TradeHomeFragment.this).load(account.avatar).into(mAvatar);
        mAssets.setText((TextUtils.isEmpty(account.asset) ? 0 : account.asset) + "元");
    }


    @Override
    public void onResume() {
        super.onResume();
        mHandler.post(mCounterDownOpenTimeIntervalRunnable);
        getTradeAllList();
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mCounterDownOpenTimeIntervalRunnable);
        mHandler.removeCallbacks(refreshArrTradeList);
    }

    private List<ClosureMethod> mIndicatorUpdate = new ArrayList<>();
    public void onEventMainThread(Message msg) {


        if (msg.what == Consts.BoardCast_PriceMsg) {
            for(ClosureMethod call : mIndicatorUpdate) {
                call.run();
            }
        } else if (msg.what == Consts.BoardCast_TradeClose) {
            getUserStatus();
        } else if (msg.what == Consts.BoardCast_ChongZhi_Refresh) {
            getUserStatus();
        } else if (msg.what == Consts.BoardCast_TradingListChange) {
            refreshTradingList();
        } else if (msg.what == Consts.BoardCast_TradingAllListChange) {
            refreshAllTradingList();
        } else if (msg.what == Consts.BoardCast_Trade_OpenTimeIntervalChange) {
            if (mCurrentKey  == null) return;
            String key = (String) mCurrentKey .run()[0];

            mCountDownLayout.setVisibility(View.GONE);


            for(GoodsApi.BuyTradeData btd: Data.sTradings) {
                if (!TextUtils.equals(btd.label, key)) {
                    continue;
                }

                if (btd.open_time_interval <= 0) {
                    mCountDownLayout.setVisibility(View.GONE);
                } else {
                    mCountDownLayout.setVisibility(View.VISIBLE);
                    int min = ((int) btd.open_time_interval)  / 60;
                    int sec = ((int) btd.open_time_interval) % 60;
                    mCountDownText.setText(String.format("%02d:%02d", min, sec));
                }
            }

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trade_home_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ButterKnife.bind(this, view);

        setupWebView();

        requireGoodsInfo();

        requireUserInfo();
        getUserStatus();

        refreshTradingList();
        refreshAllTradingList();
        mTradesViewGroup[0].setVisibility(View.GONE);
        mTradesViewGroup[1].setVisibility(View.GONE);
        mTradesListLayout.setVisibility(View.GONE);
    }

    private void refreshAllTradingList() {
        mTradesAllContainer.removeAllViews();


        for(GoodsApi.BuyTradeData btd : Data.sAllTrading) {
            View root =
                    LayoutInflater.from(getContext()).inflate(R.layout.trade_all_trades_header_layout, mTradesAllContainer, false);

            TextView openTime = CommonUtils.findView(root, R.id.open_time);
            TextView key = CommonUtils.findView(root, R.id.key);
            TextView buyType = CommonUtils.findView(root, R.id.buy_type);
            TextView dingJin = CommonUtils.findView(root, R.id.ding_jin);


            View price_mark  = CommonUtils.findView(root, R.id.price_mark);

            openTime.setText(btd.open_time);
            key.setText(btd.goods_name);

            buyType.setText(btd.up_down_type == 0 ? "买涨" : "买跌");
            buyType.setTextColor(btd.up_down_type == 0 ? 0xFFF35833 : 0xFF2CB545);


            try {
                price_mark.setBackgroundColor(btd.up_down_type == 0 ? 0xFFF35833 : 0xFF2CB545);
                int maxWidth = (getContext().getResources().getDisplayMetrics().widthPixels / 5) -
                        CommonUtils.dip2px(getContext(), 15 * 2);

                int chip = Integer.parseInt(btd.chip);
                chip = Math.min(3000, chip);
                price_mark.getLayoutParams().width = (int) (1f * maxWidth / 3000 * chip);
                price_mark.setVisibility(View.VISIBLE);
            } catch (Exception e) {

            }



            dingJin.setText(btd.chip);

            mTradesAllContainer.addView(root);

        }

    }

    private ClosureMethod mTradingUpdater;

    private void refreshTradingList() {
        mIndicatorUpdate.remove(mTradingUpdater);

        final List<ClosureMethod> listUpdaters = new ArrayList<>();

        mTradesContainer.removeAllViews();
        for(GoodsApi.BuyTradeData btd : Data.sTradings) {
//            if (TextUtils.equals(btd.label, mKey)) {
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

            final String mKey = btd.label;


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
//            }
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
        mIndicatorUpdate.add(mTradingUpdater);
    }


    @OnClick(R2.id.trade_list1)
    public void clickTradeList1() {
        selectTradeListIndex(0);
    }

    @OnClick(R2.id.trade_list2)
    public void clickTradeList2() {
        selectTradeListIndex(1);
    }

    private void selectTradeListIndex(int index) {
        for (int i = 0; i < mTradeLists.length; i++) {
            for (int j = 0; j < mTradeLists[i].getChildCount(); j++) {
                View view = mTradeLists[i].getChildAt(j);
                if (view instanceof TextView) {

                    ((TextView)view).setTextColor(index == i ? 0xFFEBAD33 : 0xFFCCCCCC);
                } else if (view instanceof View) {
                    view.setVisibility( index == i ? View.VISIBLE : View.INVISIBLE);
                }
            }


            mTradesViewGroup[i].setVisibility(index == i ? View.VISIBLE : View.GONE);
        }

    }

    private void setupWebView() {
        WebSettings setting = mWSWebview.getSettings();
        setting.setBuiltInZoomControls(false);
        setting.setSupportZoom(false);
        setting.setAllowFileAccess(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setJavaScriptEnabled(true);
        setting.setTextSize(WebSettings.TextSize.NORMAL);
        setting.setDatabaseEnabled(true);
        setting.setDomStorageEnabled(true);



        final String userID = AccountManager.getAccount().id;
        mWSWebview.addJavascriptInterface(new WebApi(), "WebAPI");
        mWSWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWSWebview.loadUrl("javascript:WebAPI.userID=" + userID);
                mWSWebview.loadUrl("javascript:document.dispatchEvent(new Event(\"readyApp\"))");
            }
        });
//        mWSWebview.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                Log.e(WebApi.TAG, message);
////                return super.onJsAlert(view, url, message, result);
//
//                return false;
//            }
//
//
//            @Override
//            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//                Log.e(WebApi.TAG, consoleMessage.message());
//                return super.onConsoleMessage(consoleMessage);
//            }
//        });



        mWSWebview.loadUrl(Consts.HOST + "customer/futures_trade/internal");
//        mWSWebview.loadUrl("http://192.168.43.242:9004/customer/futures_trade/internal");
//        mWSWebview.loadUrl("http://192.168.43.124:8000/test.html");
    }


    private void requireGoodsInfo() {
        GoodsApi.IGood iGood = ServerAPI.getInterface(GoodsApi.IGood.class);
        iGood.goods(new Callback<GoodsApi.GoodsResp>() {
            @Override
            public void success(GoodsApi.GoodsResp goodsResp, Response response) {
                if (goodsResp.code != 0) {
                    ToastHelper.showToast(goodsResp.message);
                } else {
                    updateViewPager(goodsResp.data);
                    mTradesListLayout.setVisibility(View.VISIBLE);
                    selectTradeListIndex(0);
                }

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    private void updateViewPager(GoodsApi.GoodsModel data) {

        Data.sData = data;

        final Map<String, GoodsApi.GoodName> names = data.names;
        Object[] temp = names.keySet().toArray();

        final String[] keys = new String[temp.length];
        for (int i = 0; i < temp.length; i++) {
            keys[i] = (String) temp[i];
        }

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                Bundle args = new Bundle();
                args.putString("key", keys[position]);
                return Fragment.instantiate(getContext(), TradeFragment.class.getName(), args);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return names.get(keys[position]).goods_name;
            }

            @Override
            public int getCount() {
                return keys.length;
            }
        });

        mCurrentKey = new ClosureMethod() {
            @Override
            public Object[] run(Object... args) {
                return new Object[] {
                        keys[mViewPager.getCurrentItem()],
                };
            }
        };


        mIndicator.setViewPager(mViewPager, new UnderlineIndicator.ChildProvider() {
            @Override
            public View onGetChild(ViewGroup parent, final int position) {
                View view = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.trade_home_good_indicator_item, parent, false);


                ((TextView) CommonUtils.findView(view, R.id.name)).setText(
                        mViewPager.getAdapter().getPageTitle(position));

                final TextView price = CommonUtils.findView(view, R.id.price);

                ClosureMethod update = new ClosureMethod() {

                    String label_name =  keys[position];

                    @Override
                    public Object[] run(Object... args) {

                        for (GoodsApi.Good good : Data.sData.goods) {
                            if (TextUtils.equals(good.label,label_name)) {
                                price.setText("" + good.newPrice);
                                price.setTextColor( (good.newPrice - good.open) > 0 ? 0xFFF35833 : 0xFF2CB545);
                            }
                        }

                        return new Object[0];
                    }
                };

                mIndicatorUpdate.add(update);
                update.run();


                view .getLayoutParams().width = 0;
                ((LinearLayout.LayoutParams)view.getLayoutParams()).weight = 1;

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(position);
                    }
                });

                return view;
            }
        });
    }

    private void requireUserInfo() {
        UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
        iUser.account(new Callback<UserApi.AccountResp>() {
            @Override
            public void success(UserApi.AccountResp accountResp, Response response) {
                if (accountResp.code != 0) {
                    ServerAPI.handleCodeError(accountResp);

                } else {
                    AccountManager.Account account =AccountManager.getAccount();

                    boolean changede = false;
                    if (!TextUtils.isEmpty(accountResp.data.head_portrait)) {
                        changede = true;
                        account.avatar = accountResp.data.head_portrait;
                    }

                    if (!TextUtils.isEmpty(accountResp.data.asset)) {
                        changede = true;
                        account.asset = accountResp.data.asset;
                    }

                    if (!TextUtils.isEmpty(accountResp.data.nickname)) {
                        changede = true;
                        account.nickName = accountResp.data.nickname;
                    }

                    if (!TextUtils.isEmpty(accountResp.data.free_asset)) {
                        changede = true;
                        account.free_asset = accountResp.data.free_asset;
                    }

                    if (!TextUtils.isEmpty(accountResp.data.lock_asset)) {
                        changede = true;
                        account.lock_asset = accountResp.data.lock_asset;
                    }

                    if (!TextUtils.isEmpty(accountResp.data.mobile)) {
                        changede = true;
                        account.phoneNum = accountResp.data.mobile;
                    }


                    if (changede) {
                        AccountManager.saveAccount(account);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });

    }

    public void getUserStatus() {
        UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
        iUser.status(new Callback<UserApi.StatusResp>() {
            @Override
            public void success(UserApi.StatusResp resp, Response response) {

                if (resp.code != 0) {
                    ServerAPI.handleCodeError(resp);

                } else {

                    Data.sTradings = resp.data.trades;

                    EventBus.getDefault().post(
                            Consts.getBoardCastMessage(Consts.BoardCast_TradingListChange)
                    );

                    AccountManager.Account account =AccountManager.getAccount();

                    boolean changede = false;
                    if (!TextUtils.isEmpty(resp.data.head_portrait)) {
                        changede = true;
                        account.avatar = resp.data.head_portrait;
                    }

                    if (!TextUtils.isEmpty(resp.data.asset)) {
                        changede = true;
                        account.asset = resp.data.asset;
                    }

                    if (!TextUtils.isEmpty(resp.data.inviteCode)) {
                        changede = true;
                        account.invite_code = resp.data.inviteCode;
                    }


                    if (resp.data.id != 0 ) {
                        changede = true;
                        account.id = resp.data.id + "";
                    }


                    if (changede) {
                        AccountManager.saveAccount(account);
                    }

                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });
    }



    private Runnable refreshArrTradeList = new Runnable() {


        private void refreshAllTradeList() {
            mHandler.removeCallbacks(refreshArrTradeList );
            mHandler.postDelayed(refreshArrTradeList , 15 * 1000);
        }

        @Override
        public void run() {

            System.out.println("zhujj refreshArrTradeList");
            GoodsApi.IGood iGood = ServerAPI.getInterface(GoodsApi.IGood.class);
            iGood.getTradeAllList(new Callback<GoodsApi.TradeAllListResp>() {

                @Override
                public void success(GoodsApi.TradeAllListResp resp, Response response) {
                    if (resp.code != 0){
                        ServerAPI.handleCodeError(resp);
                    } else {
                        Data.sAllTrading = resp.data;
                        EventBus.getDefault().post(
                                Consts.getBoardCastMessage(Consts.BoardCast_TradingAllListChange)
                        );
                    }

                    refreshAllTradeList();
                }

                @Override
                public void failure(RetrofitError error) {
                    ServerAPI.HandlerException(error);

                    refreshAllTradeList();
                }
            });
        }
    };

    public void getTradeAllList() {
        refreshArrTradeList .run();
    }


    @OnClick(R2.id.chongzhi)
    public void clickChongZhi() {
//        WebApi webapi = new WebApi();
//        webapi.mActivity = (BaseActivity) getActivity();
//        webapi.shareImage("http://b1.hucdn.com/upload/item/1706/28/45282501058500_800x800.jpg!250x250.jpg");

        if (!AccountManager.isLogin()) {
            Intent it = new Intent(this.getContext(), LoginActivity.class);
            startActivity(it);
            return;
        }

        Intent it = new Intent(getContext(), ChongZhiActivity.class);
        startActivity(it);
    }


    @OnClick(R2.id.tixian)
    public void clickTiXian() {
//        WebApi webapi = new WebApi();
//        webapi.mActivity = (BaseActivity) getActivity();
//        webapi.shareLink("http://m.baidu.com", "百度", "搜索引擎", "");

        if (!AccountManager.isLogin()) {
            Intent it = new Intent(this.getContext(), LoginActivity.class);
            startActivity(it);
            return;
        }


        Intent it = new Intent(getContext(), TixianActivity.class);
        startActivity(it);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getUserStatus();
        }
    }
}
