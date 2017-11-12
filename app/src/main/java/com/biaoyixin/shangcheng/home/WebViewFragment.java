package com.biaoyixin.shangcheng.home;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.biaoyixin.shangcheng.Consts;
import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.R2;
import com.biaoyixin.shangcheng.account.AccountHelper;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.base.BaseActivity;
import com.biaoyixin.shangcheng.base.BaseFragment;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhujj on 17-10-29.
 */
public class WebViewFragment extends BaseFragment {




    @BindView(R2.id.webview)
    WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.webview_fragment, container, false);
        return root;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);


        WebSettings setting = mWebView.getSettings();
        setting.setBuiltInZoomControls(false);
        setting.setSupportZoom(false);
        setting.setAllowFileAccess(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setJavaScriptEnabled(true);
        setting.setTextSize(WebSettings.TextSize.NORMAL);
        setting.setDatabaseEnabled(true);
        setting.setDomStorageEnabled(true);
        WebApi webApi = new WebApi();
        webApi.mActivity = (BaseActivity) getActivity();
        mWebView.addJavascriptInterface(webApi, "WebAPI");


        try {
            if (Build.VERSION.SDK_INT >= 21) {
                setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            String originUa = setting.getUserAgentString();

            String ua = "WeiPan   " + originUa;
            setting.setUserAgentString(ua);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mWebView.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                mWebView.loadUrl("javascript:WebAPI.userID=" + userID);
                mWebView.loadUrl("javascript:document.dispatchEvent(new Event(\"readyApp\"))");
            }
        });



        String url = Consts.HOST + "#/invitation";
//        url = "http://192.168.43.242:9004/#/invitation";
        String domian = URI.create(url).getHost();


        List<HttpCookie> cookies = AccountHelper.getCookieStore().get(URI.create(ServerAPI.HOST));
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        for(HttpCookie cookie : cookies) {
            String domin_name = cookie.getDomain();
            domin_name = domian;
            cookieManager.setCookie(domin_name, cookie.getName() + "=" + cookie.getValue()
                    + "; domain=" + domin_name + "; path=" + cookie.getPath());
        }

        mWebView.loadUrl(url);
    }
}
