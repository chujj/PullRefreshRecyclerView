package com.biaoyixin.shangcheng.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.Topbar;

/**
 * Created by zhujj on 17-11-9.
 */
public class HuiWeiDuiActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.hui_wei_dui_activity);


        Topbar topbar = (Topbar) findViewById(R.id.topbar);
        topbar.setTitle("护卫队");
        topbar.setBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
