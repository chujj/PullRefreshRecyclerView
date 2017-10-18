package com.ssc.weipan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.ssc.weipan.home.HomeActivity;
import com.ssc.weipan.login.AccountManager;
import com.ssc.weipan.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);


        initHandler();

        mHandler.sendMessageDelayed(
                Message.obtain(mHandler, AccountManager.isLogin() ? 0x13d : 0x14d),
                2000);

    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0x14d:
//                        switchToRegister();
//                        break;
                    case 0x13d:
                        switchToMain();
                        break;

                }
                finish();
            }
        };
    }

    private void switchToRegister() {
        Intent it = new Intent(this, LoginActivity.class);
        startActivity(it);
    }

    private void switchToMain() {
        Intent it = new Intent(this, HomeActivity.class);
        startActivity(it);
    }
}
