package com.biaoyixin.shangcheng.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.user.UserApi;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.ClosureMethod;
import com.biaoyixin.shangcheng.base.CommonUtils;
import com.biaoyixin.shangcheng.base.PreferencesUtil;
import com.biaoyixin.shangcheng.base.Topbar;
import com.biaoyixin.shangcheng.login.AccountManager;
import com.biaoyixin.shangcheng.login.LoginActivity;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-10-16.
 */
public class HomeActivity extends BaseActivity {


    @BindView(R2.id.topbar)
    Topbar mTopbar;
    @BindView(R2.id.ll_main)
    ViewGroup mContainer;

    @BindViews({R2.id.home_icon_1, R2.id.home_icon_2 , R2.id.home_icon_3 , R2.id.home_icon_4})
    ViewGroup[] mHomeButtons;


    public final static String KEY_TEACH_PAGES = "KEY_TEACH_PAGES_showed";

    private String[] mFragmentNames = new String[] {
            TradeHomeFragment.class.getName(),
            WebViewFragment.class.getName(),
            ZhanduiFragment.class.getName(),
            MineFragment.class.getName(),
    };

    private int mIndex = 0; // default 0
    private boolean needShowUpgradeDialog = false;
    private Dialog mUpgradeDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.home_activity);

        ButterKnife.bind(this, this);

        mTopbar.setTitle("御木轩");

        switchToIndex(mIndex);


        checkUpgrade();

        checkTeachPages();

        checkNotice();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (needShowUpgradeDialog) {
            showUpgradeDialog();
        }
    }

    private void showUpgradeDialog() {
        if (!mUpgradeDialog.isShowing()) {
            mUpgradeDialog.show();
        }
    }

    private void checkUpgrade() {

        UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
        iUser.upgradeCheck(CommonUtils.getVersionCode(this), "android", new Callback<UserApi.UpgradeResp>() {
            @Override
            public void success(final UserApi.UpgradeResp resp, Response response) {
                if (resp == null || resp.code != 0) {

                } else {

                    try {
                        AlertDialog.Builder ab = new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("检查到新版本")
                                .setMessage(resp.data.desc)
                                .setCancelable(!resp.data.force)
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Uri uri = Uri.parse(resp.data.url);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        HomeActivity.this.startActivity(intent);
                                    }
                                });

                        if (!resp.data.force) {
                            ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            needShowUpgradeDialog = true;
                        }
                        mUpgradeDialog = ab.create();
                        showUpgradeDialog();
                    } catch (Throwable e) {

                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    private String mLastFragmentKey = null;
    private void switchToIndex(int index) {
        if (index != 0 && !AccountManager.isLogin()) {
            Intent it = new Intent(this, LoginActivity.class);
            startActivity(it);
            return;
        }


        mIndex = index;


        for (int i = 0; i < mHomeButtons.length; i++) {
            for (int j = 0; j < mHomeButtons[i].getChildCount(); j++) {
                mHomeButtons[i].getChildAt(j).setSelected(i == mIndex);
            }
        }


        String key = mFragmentNames[mIndex];
        FragmentManager fg = getSupportFragmentManager();
        FragmentTransaction ft = fg.beginTransaction();

        if (mLastFragmentKey != null) {
            ft.hide(fg.findFragmentByTag(mLastFragmentKey));
        }

        Fragment fragment = fg.findFragmentByTag(key);
        if(fragment == null) {
            fragment = Fragment.instantiate(this, key, null);
            ft.add(R.id.ll_main, fragment, key);
        } else {
            ft.show(fragment);
        }

        mLastFragmentKey = key;

        ft.commitAllowingStateLoss();
        fg.executePendingTransactions();
    }


    @OnClick(R2.id.home_icon_1)
    public void clickBtn1() {
        switchToIndex(0);
    }

    @OnClick(R2.id.home_icon_2)
    public void clickBtn2() {
        switchToIndex(1);
    }

    @OnClick(R2.id.home_icon_3)
    public void clickBtn3() {
        switchToIndex(2);
    }

    @OnClick(R2.id.home_icon_4)
    public void clickBtn4() {
        switchToIndex(3);
    }



    private void checkTeachPages() {
        if (!PreferencesUtil.getBoolean(this, KEY_TEACH_PAGES, false)) {
            View view = LayoutInflater.from(this).inflate(R.layout.teach_page_layout, null, false);
            CommonUtils.addToActivity(this, view);
        }
    }



    private void checkNotice() {
        UserApi.IUser iuser = ServerAPI.getInterface(UserApi.IUser.class);


        ClosureMethod cb = new ClosureMethod() {
            @Override
            public Object[] run(Object... args) {
                UserApi.Notice notice = (UserApi.Notice) args[0];

                try {
                    AlertDialog.Builder ab = new AlertDialog.Builder(HomeActivity.this)
                            .setTitle(notice.name)
                            .setMessage(notice.content)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    ab.create().show();
                } catch (Throwable e) {

                }
                return null;
            }
        };
        iuser.globalNotice(NoticeController.handler("global", cb));
        iuser.payNotice(NoticeController.handler("pay", cb));

    }


}
