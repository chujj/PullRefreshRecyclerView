package com.ssc.weipan.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.ssc.weipan.R;
import com.ssc.weipan.R2;
import com.ssc.weipan.api.ServerAPI;
import com.ssc.weipan.api.trade.GoodsApi;
import com.ssc.weipan.base.BaseActivity;
import com.ssc.weipan.base.ClosureMethod;
import com.ssc.weipan.base.ToastHelper;
import com.ssc.weipan.base.Topbar;
import com.ssc.weipan.model.BaseModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-25.
 */
public class TixianActivity extends BaseActivity {

    @BindView(R2.id.topbar)
    Topbar mTopbar;

    @BindView(R2.id.promt)
    TextView mHeaderPromt;
    @BindView(R2.id.tixian)
    View mTixian;
    @BindView(R2.id.chongzhi)
    View mChongZhi;
    @BindView(R2.id.banks_container)
    ViewGroup mBanksContainer;

    @BindView(R2.id.tixian_price)
    EditText mTiXianPrice;

    @BindView(R2.id.bank_name)
    EditText mBankName;
    @BindView(R2.id.banks_select_container)
    ViewGroup mBankSelectContainer;

    @BindView(R2.id.privince_name)
    EditText mProvinceName;
    @BindView(R2.id.province_container)
    ViewGroup mProvinceContainer;

    @BindView(R2.id.city_name)
    EditText mCityName;
    @BindView(R2.id.city_container)
    ViewGroup mCityContainer;

    @BindView(R2.id.card_id)
    EditText mCardId;

    @BindView(R2.id.card_owner_name)
    EditText mCardOwnerName;

    @BindView(R2.id.card_owner_id)
    EditText mCardOwnerId;

    @BindView(R2.id.sms_code)
    EditText mSMSCode;

    @BindView(R2.id.require_sms_code)
    TextView mRequireSMSCode;

    @BindView(R2.id.confirm)
    View mConfirm;

    private List<GoodsApi.City> mCitys;
    private GoodsApi.OutMoneyUIInfo mUIInfo;
    private List<GoodsApi.OutChannel> mOutChannels;
    private HashMap<GoodsApi.OutChannel, List<GoodsApi.Bank>> mBankCache = new HashMap<>();

    // TODO 各种click事件的关联
    private GoodsApi.OutChannel mSelectedOutChannel;
    private GoodsApi.City mSelectedProvince;
    private GoodsApi.City mSelectedCity;
    private GoodsApi.Bank mSelectedBank;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.tixian_activity);

        ButterKnife.bind(this, this);

        mTixian.setVisibility(View.GONE);
        mChongZhi.setVisibility(View.GONE);
        mHeaderPromt.setVisibility(View.VISIBLE);

        mTopbar.setTitle("提现");

        loadData();
    }

    private void loadData() {

        final Runnable initUI = new Runnable() {
            int success_count = 0;

            private ArrayList<ClosureMethod> bankChangeCBs = new ArrayList<>();

            @Override
            public void run() {
                success_count ++;

                if (success_count != 3) {
                    return;
                }

                dismissLoadingDialog();

                { // 初始化
                    mBanksContainer.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(mBanksContainer.getContext());
                    for (int i = 0; i < mOutChannels.size(); i++) {
                        final GoodsApi.OutChannel myChannel = mOutChannels.get(i);

                        View tixianBank = inflater.inflate(R.layout.tixian_bank_item, mBanksContainer, false);
                        ((TextView) tixianBank.findViewById(R.id.name)).setText(myChannel.name);
                        final ImageView checkbox = (ImageView) tixianBank.findViewById(R.id.checkbox);
                        checkbox.setSelected(false);

                        bankChangeCBs.add(new ClosureMethod() {
                            @Override
                            public Object[] run(Object... args) {
                                checkbox.setSelected(args[0] == myChannel);

                                return new Object[0];
                            }
                        });

                        final int index = i;
                        tixianBank.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setSelectedChannel(myChannel);
                            }
                        });

                        mBanksContainer.addView(tixianBank);
                    }
                }

                setSelectedChannel(mOutChannels.get(0));

            }

            private void setSelectedChannel(GoodsApi.OutChannel channel) {
                for (ClosureMethod cb : bankChangeCBs) {
                    cb.run(channel);
                }
                onChannelChanged(channel);
            }
        };


        showLoadingDialog("加载中...", false);
        GoodsApi.IGood iGood = ServerAPI.getInterface(GoodsApi.IGood.class);
        iGood.getCityList(new Callback<List<GoodsApi.City>>() {
            @Override
            public void success(List<GoodsApi.City> cityListResp, Response response) {

                mCitys = cityListResp;
                initUI.run();
            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
                dismissLoadingDialog();
            }
        });

        iGood.getOutMoneyUIInfo(new Callback<GoodsApi.OutMoneyUIInfoResp>() {
            @Override
            public void success(GoodsApi.OutMoneyUIInfoResp resp, Response response) {
                if (resp.code != 0) {
                    ToastHelper.showToast(resp.message);
                    ServerAPI.handleCodeError(resp);
                } else {
                    mUIInfo = resp.data;
                    initUI.run();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
                dismissLoadingDialog();
            }
        });


        iGood.getOutMoneyChannelList(new Callback<GoodsApi.OutChannelResp>() {
            @Override
            public void success(GoodsApi.OutChannelResp resp, Response response) {
                if (resp.code != 0) {
                    ToastHelper.showToast(resp.message);
                    ServerAPI.handleCodeError(resp);
                } else {
                    mOutChannels = resp.data;
                    initUI.run();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ServerAPI.HandlerException(error);
                dismissLoadingDialog();
            }
        });


        mBankName.setOnTouchListener(new MyTouchEventListener(new Runnable() {
            @Override
            public void run() {
                clickBank();
            }
        }));
        mProvinceName.setOnTouchListener(new MyTouchEventListener(new Runnable() {
            @Override
            public void run() {
                clickProvince();
            }
        }));
        mCityName.setOnTouchListener(new MyTouchEventListener(new Runnable() {
            @Override
            public void run() {
                clickCity();
            }
        }));
    }


    private class MyTouchEventListener implements View.OnTouchListener {

        private final Runnable mCB;
        int count = 0;
        MyTouchEventListener(Runnable calback) {
            mCB = calback;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                count ++;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                count ++;
            }

            if (count == 2){
                count = 0;
                if (mCB != null) {
                    mCB.run();
                }
            }

            return false;
        }
    }

    private void onChannelChanged(GoodsApi.OutChannel channel) {
        mSelectedOutChannel = channel;
        mBankName.setText("");
        mBankSelectContainer.removeAllViews();
    }

    public void clickBank() {
        if (mBankSelectContainer.getChildCount() == 0) {
            final ClosureMethod initView = new ClosureMethod() {
                @Override
                public Object[] run(Object... args) {

                    List<GoodsApi.Bank> banks = mBankCache.get(mSelectedOutChannel);
                    LayoutInflater inflater = LayoutInflater.from(mBankSelectContainer.getContext());
                    for (GoodsApi.Bank bank : banks) {
                        View root = inflater.inflate(R.layout.tixian_bank_item_bank, mBankSelectContainer, false);
                        ((TextView) root.findViewById(R.id.name)).setText(bank.bankName);
                        final GoodsApi.Bank bankModel = bank;
                        final String bankName = bank.bankName;
                        mBankSelectContainer.addView(root);

                        root.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mSelectedBank = bankModel;
                                mBankName.setText(bankName);
                                mBankSelectContainer.setVisibility(View.GONE);
                            }
                        });
                    }

                    mBankSelectContainer.setVisibility(View.VISIBLE);

                    return new Object[0];
                }
            };


            if (mBankCache.get(mSelectedOutChannel) == null){

                showLoadingDialog("加载中", false);

                new AsyncTask<Void, Void, Void>() {

                    com.squareup.okhttp.Response response;
                    GoodsApi.BankResp wcpr = null;

                    @Override
                    protected Void doInBackground(Void... params) {

                        try {
                            Request request = new Request.Builder().url(mSelectedOutChannel.bank_url).get().build();
                            Call call = ServerAPI.getInstance().mOKClient.newCall(request);
                            response = call.execute();

                            wcpr = new Gson().fromJson(response.body().string(), GoodsApi.BankResp.class);

                        } catch (Exception e) {
                            ServerAPI.HandlerException(RetrofitError.unexpectedError(mSelectedOutChannel.bank_url, e));
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
                            mBankCache.put(mSelectedOutChannel, wcpr.data);
                            initView.run();
                        }

                    }
                }.execute();
            } else {
                initView.run();
            }

        } else {
            mBankSelectContainer.setVisibility(View.VISIBLE);
        }
    }

    public void clickProvince() {
        if (mProvinceContainer.getChildCount() == 0) {
            LayoutInflater inflater = LayoutInflater.from(mProvinceContainer.getContext());
            for (GoodsApi.City city : mCitys) {
                View root = inflater.inflate(R.layout.tixian_bank_item_bank, mProvinceContainer, false);
                ((TextView) root.findViewById(R.id.name)).setText(city.name);
                final String cityName = city.name;
                final GoodsApi.City cityMode = city;
                mProvinceContainer.addView(root);

                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedProvince = cityMode;
                        mProvinceName.setText(cityName);
                        mProvinceContainer.setVisibility(View.GONE);


                        mCityName.setText("");
                        mCityContainer.removeAllViews();
                    }
                });
            }

            mProvinceContainer.setVisibility(View.VISIBLE);
        } else {
            mProvinceContainer.setVisibility(View.VISIBLE);
        }
    }

    public void clickCity() {
        if (mSelectedProvince == null ) {
            ToastHelper.showToast("请先选择省份");
            return;
        }

        if (mCityContainer.getChildCount() == 0) {
            LayoutInflater inflater = LayoutInflater.from(mCityContainer.getContext());
            for (GoodsApi.City city : mSelectedProvince.children) {
                View root = inflater.inflate(R.layout.tixian_bank_item_bank, mCityContainer, false);
                ((TextView) root.findViewById(R.id.name)).setText(city.name);
                final String cityName = city.name;
                final GoodsApi.City cityMode = city;
                mCityContainer.addView(root);

                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedCity = cityMode;
                        mCityName.setText(cityName);
                        mCityContainer.setVisibility(View.GONE);
                    }
                });
            }

            mCityContainer.setVisibility(View.VISIBLE);
        } else {
            mCityContainer.setVisibility(View.VISIBLE);
        }
    }


    @OnClick(R2.id.confirm)
    public void clickConfirm() {
        final String realname = mCardOwnerName.getText().toString(); // 开户名
        final String id_card	= mCardOwnerId.getText().toString(); // 是	string	身份证
        final String bank_account = mCardId.getText().toString()	; // 是	string	银行账户
        final String bank_province = mSelectedProvince == null ? "" : mSelectedProvince.code; // 是	string	省份
        final String bank_city  = mSelectedCity == null ? "" : mSelectedCity.code; // 	是	string	城市
        final String bank_code = mSelectedBank == null ? "" : mSelectedBank.bankCode; // 	是	string	银行的编码
        final String sms_code = mSMSCode.getText().toString() ; // 	是	string	提现短信验证码
        final String money = mTiXianPrice.getText().toString(); // 	是	string	金额（元）

        if (TextUtils.isEmpty(realname)) {
            ToastHelper.showToast("请输入开户名");
            return;
        }

        if (TextUtils.isEmpty(id_card)) {
            ToastHelper.showToast("请输入身份证");
            return;
        }

        if (TextUtils.isEmpty(bank_account)) {
            ToastHelper.showToast("请输入银行账户");
            return;
        }

        if (TextUtils.isEmpty(bank_province)) {
            ToastHelper.showToast("请输入省份");
            return;
        }

        if (TextUtils.isEmpty(bank_city)) {
            ToastHelper.showToast("请输入城市");
            return;
        }

        if (TextUtils.isEmpty(bank_code)) {
            ToastHelper.showToast("请输入银行");
            return;
        }

        if (TextUtils.isEmpty(sms_code)) {
            ToastHelper.showToast("请输入提现短信验证码");
            return;
        }

        if (TextUtils.isEmpty(money)) {
            ToastHelper.showToast("请输入金额（元）");
            return;
        }


        showLoadingDialog("加载中", false);

        new AsyncTask<Void, Void, Void>() {

            com.squareup.okhttp.Response response;
            BaseModel wcpr = null;

            @Override
            protected Void doInBackground(Void... params) {

                try {

                    MultipartBuilder b = new MultipartBuilder();
                    b.addFormDataPart("realname", realname);
                    b.addFormDataPart("id_card", id_card);
                    b.addFormDataPart("bank_account", bank_account);
                    b.addFormDataPart("bank_province", bank_province);
                    b.addFormDataPart("bank_city", bank_city);
                    b.addFormDataPart("bank_code", bank_code);
                    b.addFormDataPart("sms_code", sms_code);
                    b.addFormDataPart("money", money);

                    Request request = new Request.Builder().url(mSelectedOutChannel.extract_url).post(b.build()).build();
                    Call call = ServerAPI.getInstance().mOKClient.newCall(request);
                    response = call.execute();

                    BaseModel resp = new Gson().fromJson(response.body().string(), BaseModel.class);
                    wcpr = resp;

                } catch (final Exception e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            ServerAPI.HandlerException(RetrofitError.unexpectedError(mSelectedOutChannel.extract_url, e));
                        }
                    });
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
                    ToastHelper.showToast("提现成功");
                }

            }
        }.execute();
    }

}
