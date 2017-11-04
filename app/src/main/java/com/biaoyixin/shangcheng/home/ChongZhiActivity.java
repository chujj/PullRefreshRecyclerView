package com.biaoyixin.shangcheng.home;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.trade.GoodsApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.ClosureMethod;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.biaoyixin.shangcheng.base.ToastHelper;
import com.biaoyixin.shangcheng.base.Topbar;
import com.biaoyixin.shangcheng.login.AccountManager;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-22.
 */
public class ChongZhiActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;

    @BindViews({R2.id.chongzhi, R2.id.tixian})
    View[] mView2hide;
    @BindView(R2.id.container)
    View mContainer;
    @BindView(R2.id.chips)
    ChipLabelsLayout mChips;
    @BindView(R2.id.channel_container)
    ViewGroup mChannelContainer;
    @BindView(R2.id.service_fee)
    TextView mServiceFee;
    @BindView(R2.id.avatar)
    ImageView mAvatar;
    @BindView(R2.id.assets)
    TextView mAssets;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chongzhi_activity);


        ButterKnife.bind(this, this);

        mTopbar.setTitle("充值");

        for (int i = 0; i < mView2hide.length; i++) {
            mView2hide[i].setVisibility(View.GONE);
        }

        mContainer.setVisibility(View.GONE);

        AccountManager.Account account = AccountManager.getAccount();

        Glide.with(this).load(account.avatar).into(mAvatar);
        mAssets.setText(account.asset + "元");


        requireData();
    }

    private String mChipSelected;


    private Map<GoodsApi.Channel, Object[]> mChannelsClosures = new HashMap<>();

    private GoodsApi.Channel mSelectedChannel;
    private void onPayChannelClicked(GoodsApi.Channel channel) {
        mSelectedChannel = channel;
        for (Object[] os : mChannelsClosures.values()) {
            ((ClosureMethod)os[0]).run(channel);
        }

    }


    private Map<String, Object> mInMoneyMethodMap = new HashMap<String, Object>() {
        {
            put("scan_98pay", new ClosureMethod() {
                @Override
                public Object[] run(Object... args) {
                    LayoutInflater inflater = (LayoutInflater) args[0];
                    ViewGroup parent = (ViewGroup) args[1];
                    final GoodsApi.Channel channel = (GoodsApi.Channel) args[2];

                    View _itemRoot = inflater.inflate(R.layout.in_money_channel, parent, false);

                    ((TextView) CommonUtils.findView(_itemRoot, R.id.name))
                            .setText(channel.name);
                    final ImageView checkBox = CommonUtils.findView(_itemRoot, R.id.checkbox);


                    ClosureMethod[] closures = new ClosureMethod[] {
                            new ClosureMethod() { // update UI
                                @Override
                                public Object[] run(Object... args) {
                                    checkBox.setSelected(args[0] == channel);
                                    return new Object[0];
                                }
                            },
                            new ClosureMethod() { // callpay
                                @Override
                                public Object[] run(Object... args) {


                                    String url = channel.url;
                                    if (url.indexOf("?") > 0) {
                                        url += ("&money=" +mChipSelected);
                                    } else {
                                        url += ("?money=" +mChipSelected);
                                    }
                                    final String f_url = url;

                                    showLoadingDialog("加载中", false);
                                    new AsyncTask<Void, Void, Void>() {

                                        com.squareup.okhttp.Response response;
                                        GoodsApi.QRCodePayResp wcpr = null;

                                        @Override
                                        protected Void doInBackground(Void... params) {

                                            try {
                                                Request request = new Request.Builder().url(f_url).get().build();
                                                Call call = ServerAPI.getInstance().mOKClient.newCall(request);
                                                response = call.execute();

                                                wcpr = new Gson().fromJson(response.body().string(), GoodsApi.QRCodePayResp.class);

                                            } catch (Exception e) {
                                                ServerAPI.HandlerException(RetrofitError.unexpectedError(f_url, e));
                                            }
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {
                                            super.onPostExecute(aVoid);

                                            dismissLoadingDialog();
                                            if (wcpr == null) {
                                                ToastHelper.showToast("数据错误");
                                                return;
                                            }

                                            if (wcpr.code != 0) {
                                                ServerAPI.handleCodeError(wcpr);
                                                ToastHelper.showToast(wcpr.message);
                                            } else {
                                                Uri uri = Uri.parse(wcpr.data);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                ChongZhiActivity.this.startActivity(intent);
                                            }

                                        }
                                    }.execute();

                                    return new Object[0];
                                }
                            }
                    };

                    mChannelsClosures.put(channel, closures);

                    _itemRoot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onPayChannelClicked(channel);
                        }
                    });

                    return new Object[] {_itemRoot};
                }
            });
            put("unionpay", new ClosureMethod() {
                @Override
                public Object[] run(Object... args) {
                    final LayoutInflater inflater = (LayoutInflater) args[0];
                    ViewGroup parent = (ViewGroup) args[1];
                    final GoodsApi.Channel channel = (GoodsApi.Channel) args[2];

                    View _itemRoot = inflater.inflate(R.layout.in_money_channel_bank, parent, false);

                    ((TextView) CommonUtils.findView(_itemRoot, R.id.name))
                            .setText(channel.name);

                    final ViewGroup banks_container = CommonUtils.findView(
                            _itemRoot, R.id.banks_container);
                    final ViewGroup selectedBankContainer = CommonUtils.findView(
                            _itemRoot, R.id.focus_bank_container);
                    final TextView selectBankText = CommonUtils.findView(
                            _itemRoot, R.id.selected_bank);

                    final ImageView checkBox = CommonUtils.findView(_itemRoot, R.id.checkbox);


                    final ClosureMethod selectBankHolder = new ClosureMethod() {
                        private GoodsApi.Bank mSelectedBank = null;

                        @Override
                        public Object[] run(Object... args) {
                            if (args[0] == "set") {
                                mSelectedBank = (GoodsApi.Bank) args[1];
                            } else if (args[0] == "get") {
                                return new Object[] {
                                        mSelectedBank
                                };
                            }

                            return new Object[0];
                        }
                    };


                    ClosureMethod[] closures = new ClosureMethod[] {
                            new ClosureMethod() { // update UI

                                boolean bank_loaded = false;
                                private View.OnClickListener toggleEllipse = null;

                                @Override
                                public Object[] run(Object... args) {
                                    checkBox.setSelected(args[0] == channel);
                                    selectedBankContainer.setVisibility(args[0] == channel ? View.VISIBLE : View.GONE);

                                    if(args[0] != channel) {
                                        banks_container.setVisibility(View.GONE);
                                    }

                                    if (!bank_loaded) {
                                        bank_loaded = true;

                                        toggleEllipse = new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                boolean opend = banks_container.getVisibility() == View.VISIBLE;
                                                banks_container.setVisibility(opend ? View.GONE : View.VISIBLE);
                                            }
                                        };
                                        selectedBankContainer.setOnClickListener(toggleEllipse);

                                        getPayBank(channel.bank_url);
                                    }

                                    return new Object[0];
                                }


                                private void getPayBank(final String brank_url) {

                                    showLoadingDialog("加载中", false);
                                    new AsyncTask<Void, Void, Void>() {

                                        com.squareup.okhttp.Response response;
                                        GoodsApi.BankResp wcpr = null;

                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            try {
                                                Request request = new Request.Builder().url(brank_url).get().build();
                                                Call call = ServerAPI.getInstance().mOKClient.newCall(request);
                                                response = call.execute();

                                                wcpr = new Gson().fromJson(response.body().string(), GoodsApi.BankResp.class);

                                            } catch (Exception e) {
                                                ServerAPI.HandlerException(RetrofitError.unexpectedError(brank_url, e));
                                            }
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {
                                            super.onPostExecute(aVoid);

                                            dismissLoadingDialog();
                                            if (wcpr == null) {
                                                ToastHelper.showToast("数据错误");
                                                return;
                                            }

                                            if (wcpr.code != 0) {
                                                ServerAPI.handleCodeError(wcpr);
                                                ToastHelper.showToast(wcpr.message);
                                            } else {
                                                for (int i = 0; i < wcpr.data.size(); i++) {

                                                    View bank_item_view =
                                                            inflater.inflate(R.layout.chongzhi_bank_item, banks_container, false);
                                                    ((TextView) CommonUtils.findView(bank_item_view, R.id.name)).
                                                            setText(wcpr.data.get(i).bankName);

                                                    final GoodsApi.Bank my_bank = wcpr.data.get(i);

                                                    bank_item_view.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            selectBankText.setText(my_bank.bankName);
                                                            selectBankHolder.run("set", my_bank);
                                                            toggleEllipse.onClick(null);
                                                        }
                                                    });

                                                    banks_container.addView(bank_item_view);

                                                }


                                            }

                                        }
                                    }.execute();
                                }

                            },
                            new ClosureMethod() {


                                @Override
                                public Object[] run(Object... args) {
                                    GoodsApi.Bank bank = (GoodsApi.Bank) selectBankHolder.run("get")[0];

                                    if (bank == null) {
                                        ToastHelper.showToast("请选择银行");
                                        return null;
                                    }

                                    String url = channel.url;
                                    if (url.indexOf("?") > 0) {
                                        url += (String.format("&tranAmt=%s&bankCode=%s", mChipSelected, bank.bankCode));
                                    } else {
                                        url += (String.format("?tranAmt=%s&bankCode=%s", mChipSelected, bank.bankCode));
                                    }
                                    final String f_url = url;

                                    showLoadingDialog("加载中", false);
                                    new AsyncTask<Void, Void, Void>() {

                                        com.squareup.okhttp.Response response;
                                        GoodsApi.UnipayResp wcpr = null;
                                        String originBody = null;

                                        @Override
                                        protected Void doInBackground(Void... params) {

                                            try {
                                                Request request = new Request.Builder().url(f_url).get().build();
                                                Call call = ServerAPI.getInstance().mOKClient.newCall(request);
                                                response = call.execute();
                                                originBody = response.body().string();
                                                wcpr = new Gson().fromJson(originBody, GoodsApi.UnipayResp.class);

                                            } catch (Exception e) {
                                                ServerAPI.HandlerException(RetrofitError.unexpectedError(f_url, e));
                                            }
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {
                                            super.onPostExecute(aVoid);

                                            dismissLoadingDialog();
                                            if (wcpr == null) {
                                                ToastHelper.showToast("数据错误");
                                                return;
                                            }

                                            if (wcpr.code != 0) {
                                                ServerAPI.handleCodeError(wcpr);
                                                ToastHelper.showToast(wcpr.message);
                                            } else {

                                                String url = wcpr.data.gateway;
                                                try {
                                                    Map data = (Map) new Gson().fromJson(originBody, Map.class).get("data");
                                                    for(Object entry : data.keySet()) {
                                                        if (TextUtils.equals(entry.toString(), "gateway")) {

                                                        } else {

                                                            if (data.get(entry) == null) {
                                                                continue;
                                                            }

                                                            if (!url.contains("?")) {
                                                                url += "?" + entry.toString() + "=" + URLEncoder.encode(data.get(entry).toString());
                                                            } else {
                                                                url += "&" + entry.toString() + "=" + URLEncoder.encode(data.get(entry).toString());
                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {

                                                }
                                                Uri uri = Uri.parse(url);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                ChongZhiActivity.this.startActivity(intent);
                                            }

                                        }
                                    }.execute();


                                    return new Object[0];
                                }

                            }
                    };

                    mChannelsClosures.put(channel, closures);

                    _itemRoot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onPayChannelClicked(channel);
                        }
                    });

                    return new Object[] {_itemRoot};
                }
            });
            put("nonebank_unionpay", new ClosureMethod() {
                @Override
                public Object[] run(Object... args) {
                    LayoutInflater inflater = (LayoutInflater) args[0];
                    ViewGroup parent = (ViewGroup) args[1];
                    final GoodsApi.Channel channel = (GoodsApi.Channel) args[2];

                    View _itemRoot = inflater.inflate(R.layout.in_money_channel, parent, false);

                    ((TextView) CommonUtils.findView(_itemRoot, R.id.name))
                            .setText(channel.name);


                    final ImageView checkBox = CommonUtils.findView(_itemRoot, R.id.checkbox);


                    ClosureMethod[] closures = new ClosureMethod[] {
                            new ClosureMethod() { // update UI
                                @Override
                                public Object[] run(Object... args) {
                                    checkBox.setSelected(args[0] == channel);
                                    return new Object[0];
                                }
                            },
                            new ClosureMethod() {
                                @Override
                                public Object[] run(Object... args) {

                                    String url = channel.url;
                                    if (url.indexOf("?") > 0) {
                                        url += ("&tranAmt=" +mChipSelected);
                                    } else {
                                        url += ("?tranAmt=" +mChipSelected);
                                    }
                                    final String f_url = url;

                                    showLoadingDialog("加载中", false);
                                    new AsyncTask<Void, Void, Void>() {

                                        com.squareup.okhttp.Response response;
                                        GoodsApi.WeChatPayResp wcpr = null;

                                        @Override
                                        protected Void doInBackground(Void... params) {

                                            try {
                                                Request request = new Request.Builder().url(f_url).get().build();
                                                Call call = ServerAPI.getInstance().mOKClient.newCall(request);
                                                response = call.execute();

                                                wcpr = new Gson().fromJson(response.body().string(), GoodsApi.WeChatPayResp.class);

                                            } catch (Exception e) {
                                                ServerAPI.HandlerException(RetrofitError.unexpectedError(f_url, e));
                                            }
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {
                                            super.onPostExecute(aVoid);

                                            dismissLoadingDialog();

                                            if (wcpr == null) {
                                                ToastHelper.showToast("数据错误");
                                                return;
                                            }

                                            if (wcpr.code != 0) {
                                                ServerAPI.handleCodeError(wcpr);
                                                ToastHelper.showToast(wcpr.message);
                                            } else {
                                                Uri uri = Uri.parse(wcpr.data.gateway);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                ChongZhiActivity.this.startActivity(intent);
                                            }

                                        }
                                    }.execute();

                                    return new Object[0];
                                }
                            }
                    };

                    mChannelsClosures.put(channel, closures);

                    _itemRoot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onPayChannelClicked(channel);
                        }
                    });


                    return new Object[] {_itemRoot};
                }
            });
        }
    };


    private GoodsApi.InMoneyUIInfo mInMoneyUIInfo;
    private List<GoodsApi.Channel> mInMoneyChannels;

    private void requireData() {
        final Runnable updateUI = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                count++;
                if (count < 2) {
                    return;
                }

                {
                    mChips.setItems(mInMoneyUIInfo.pays);
                    mChips.initView();

                    mChips.setOnChipSelected(new ChipLabelsLayout.OnChipSelected() {
                        @Override
                        public void onChipSelected(String chip, boolean fromCoupon) {
                            mChipSelected = chip;
                        }
                    });


                    mChips.getChildAt(0).performClick();
                }


                {
                    String prefix = "每笔充值收取 ";
                    String subfix = " 的手续费";
                    SpannableString ss = new SpannableString(prefix + mInMoneyUIInfo.cashInFee + subfix);
                    ss.setSpan(new ForegroundColorSpan(0xffed5631),
                            prefix.length(),
                            (prefix + mInMoneyUIInfo.cashInFee).length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mServiceFee.setText(ss);
                }


                mChannelContainer.removeAllViews();
                LayoutInflater inflater = LayoutInflater.from(ChongZhiActivity.this);
                for (int i = 0; i < mInMoneyChannels.size(); i++) {

                    String type = mInMoneyChannels.get(i).type;

                    Object[] retval =
                            ((ClosureMethod) mInMoneyMethodMap.get(type)).run(
                                    inflater, mChannelContainer, mInMoneyChannels.get(i));

                    mChannelContainer.addView((View) retval[0]);
                }

                mContainer.setVisibility(View.VISIBLE);
            }
        };


        GoodsApi.IGood iGoods = ServerAPI.getInterface(GoodsApi.IGood.class);
        iGoods.getInMoneyUIInfo(new Callback<GoodsApi.InMoneyUIInfoResp>() {
            @Override
            public void success(GoodsApi.InMoneyUIInfoResp resp, Response response) {
                if (resp.code != 0) {
                    ToastHelper.showToast(resp.message);
                    ServerAPI.handleCodeError(resp);
                } else {
                    mInMoneyUIInfo = resp.data;
                    updateUI.run();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });


        iGoods.igetInMoneyChannelList(new Callback<GoodsApi.InMoneyChannelListResp>() {
            @Override
            public void success(GoodsApi.InMoneyChannelListResp resp, Response response) {
                if (resp.code != 0) {
                    ToastHelper.showToast(resp.message);
                    ServerAPI.handleCodeError(resp);
                } else {
                    mInMoneyChannels = resp.data;
                    updateUI.run();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
            }
        });
    }


    @OnClick(R2.id.confirm)
    public void clickConfirm() {

        if (mSelectedChannel == null) {
            ToastHelper.showToast("请选择支付方式");
        }


        ((ClosureMethod)mChannelsClosures.get(mSelectedChannel)[1])
                .run();

    }

}