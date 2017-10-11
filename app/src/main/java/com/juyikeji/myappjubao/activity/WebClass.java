package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.juyikeji.myappjubao.MainActivity;
import com.juyikeji.myappjubao.R;

/**
 * Created by Administrator on 2016/4/27 0027.
 */
public class WebClass extends Activity{

    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_webview);
        wv=(WebView)findViewById(R.id.webview);
        Intent intent=getIntent();
        String s=intent.getStringExtra("url");
        wv.getSettings().setJavaScriptEnabled(true);
//        wv.setScrollBarStyle();
        WebSettings webSettings = wv.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        wv.loadUrl(s);//加载网页
        //加载数据
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    WebClass.this.setTitle("加载完成");
                } else {
                    WebClass.this.setTitle("加载中.......");

                }
            }
        });
//这个是当网页上的连接被点击的时候
//        wv.setWebViewClient(new WebViewClient() {
//            public boolean shouldOverrideUrlLoading(final WebView view,
//                                                    final String url) {
//                loadurl(view, url);
//                return true;
//            }
//        });
//        // goBack()表示返回webView的上一页面
//        public boolean onKeyDown(int keyCoder, KeyEvent event) {
//            if (wv.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
//                wv.goBack();
//                return true;
//            }
//            return false;
//        }
    }
}
