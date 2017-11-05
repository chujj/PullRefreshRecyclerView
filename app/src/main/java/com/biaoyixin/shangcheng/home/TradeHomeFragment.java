package com.biaoyixin.shangcheng.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.biaoyixin.shangcheng.Consts;
import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.trade.GoodsApi;
import com.biaoyixin.shangcheng.api.user.UserApi;
import com.biaoyixin.shangcheng.base.BaseApp;
import com.biaoyixin.shangcheng.base.BaseFragment;
import com.biaoyixin.shangcheng.base.ClosureMethod;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.biaoyixin.shangcheng.base.PreferencesUtil;
import com.biaoyixin.shangcheng.base.ToastHelper;
import com.biaoyixin.shangcheng.base.UnderlineIndicator;
import com.biaoyixin.shangcheng.login.AccountManager;
import com.biaoyixin.shangcheng.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
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


    @BindView(R2.id.ws_callback)
    WebView mWSWebview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

//        new WebApi().shareImage("http://b1.hucdn.com/upload/item/1706/28/45282501058500_800x800.jpg!250x250.jpg");
//        new WebApi().shareLink("http://m.baidu.com", "百度", "搜索引擎", "http://b1.hucdn.com/upload/item/1706/28/45282501058500_800x800.jpg!250x250.jpg");
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



        final String userID = PreferencesUtil.getString(BaseApp.getApp(), AccountManager.PREF_USER_ID, "");
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
//        });


        mWSWebview.loadUrl("http://time.168zhibo.cn/customer/futures_trade/internal");
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
                if (accountResp.code != 0 && accountResp.data != null) {
                    ServerAPI.handleCodeError(accountResp);
                    ToastHelper.showToast(accountResp.message);
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

                if (resp.code != 0 && resp.data != null) {
                    ServerAPI.handleCodeError(resp);
                    ToastHelper.showToast(resp.message);
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


    @OnClick(R2.id.chongzhi)
    public void clickChongZhi() {
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
        if (!AccountManager.isLogin()) {
            Intent it = new Intent(this.getContext(), LoginActivity.class);
            startActivity(it);
            return;
        }


        Intent it = new Intent(getContext(), TixianActivity.class);
        startActivity(it);
    }




}
