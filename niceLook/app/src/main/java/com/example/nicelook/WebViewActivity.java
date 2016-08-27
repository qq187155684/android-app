package com.example.nicelook;

import java.util.List;
import java.util.Map;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends Activity{

    private String mUrl;
    private String mUserName;
    private String mPassword;
    private String mResult;
    private String mCookie;
    private WebView mWebView;
    private CookieManager mCookieManager;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.web);
            ActionBar actionBar = getActionBar();
            actionBar.hide();
            mUrl = getIntent().getStringExtra("web_url");
            mUserName = getIntent().getStringExtra("web_name");
            mPassword = getIntent().getStringExtra("web_pwd");
            mResult =  getIntent().getStringExtra("web_result");
            mCookie = getIntent().getStringExtra("web_cookie");
            mWebView = (WebView)findViewById(R.id.webview);
            initWebViewSettings();
            CookieSyncManager.createInstance(this);  
            mCookieManager = CookieManager.getInstance();
            mCookieManager.removeSessionCookie();//remove cookie
            mCookieManager.setAcceptCookie(true);
            getCookies();
            CookieSyncManager.getInstance().sync();
            //synCookies(this, mUrl,getCookies());
            showWebView(mUrl,mUserName,mPassword,mResult);
        }
       
        private void showWebView(String url,String username,String password,String result){
            System.out.println("================== showWebView url ===================="+url);
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            WebViewClient client = new webViewClient();
            mWebView.setWebViewClient(client);
            mWebView.loadUrl("http://kb1122.com");
       }
           /**
             * init WebView Settings
            * */
            private void initWebViewSettings(){
                mWebView.getSettings().setSupportZoom(true);
                mWebView.getSettings().setBuiltInZoomControls(true);
                mWebView.getSettings().setDefaultFontSize(12);
                mWebView.getSettings().setLoadWithOverviewMode(true);
                // ���ÿ��Է����ļ�
                mWebView.getSettings().setAllowFileAccess(true);
                //������ʵ�ҳ������Javascript����webview��������֧��Javascript
                mWebView.getSettings().setJavaScriptEnabled(true);
                //mWebView.getSettings().setUserAgentString(this.getApplication().getUserAgent());
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                mWebView.getSettings().setAllowFileAccess(true);
                mWebView.getSettings().setAppCacheEnabled(true);
                mWebView.getSettings().setDomStorageEnabled(true);
                mWebView.getSettings().setDatabaseEnabled(true);
                mWebView.setDrawingCacheEnabled(true);
                mWebView.getSettings().setBuiltInZoomControls(true);  
            }

        /** 
         * ͬ��һ��cookie 
         */  
        public static void synCookies(Context context, String url,String cookies) { 
            System.out.println("============== synCookies cookies ==============="+cookies);
            //cookieManager.setCookie(url, cookies);//cookies����HttpClient�л�õ�cookie  
            CookieSyncManager.getInstance().sync();  
        }
        
    private String getCookies(){
        Map<String, String> cookies = Operaton.cookieStore;
        String Domain = null;
        String path = null;
        String cookie = null;
        // �õ�Domain �� Path
        for (String key : cookies.keySet()) {
            if (key.equalsIgnoreCase("Domain")) {
                Domain = cookies.get(key);
            } else if (key.equalsIgnoreCase("Path")) {
                path = cookies.get(key);
            }
        }
        //ƴ�ӳ���Ҫ��ʽ
        for (String key : cookies.keySet()) {
            if (!key.equals("Domain") && !key.equals("Path")) {
                String value = cookies.get(key);
                StringBuilder sb = new StringBuilder();
                sb.append(key + "=" + value + ";");
                mCookieManager.setCookie(mUrl,key + "=" + value);
                mCookieManager.setCookie(mUrl,"domain=" + Domain);
                mCookieManager.setCookie(mUrl,"path=" + path+ ";");
                mCookieManager.setCookie(mUrl,"AVS=" + "v26p3emj1ar0l0t2mu6s0uei85");
                //sb.append("domain=" + Domain + ";" + "path=" + path);
                //cookieManager.setCookie(Constant.URL_JCHD, sb.toString());
                //cookie = sb.toString();
                sb = null;
            }
        }
        mCookieManager.getCookie(mUrl);
        System.out.println("=============== getCookies 000000 ==============="+mCookieManager.getCookie(mUrl));
        System.out.println("=================== $$$$cookie ====================="+cookie);
        return cookie;
    }

    private boolean isExit = false;
    
    @Override  
    public void onBackPressed() {  
        //super.onBackPressed();
        System.out.println("============== ������back��   onBackPressed() ===================");
        if(mWebView != null &&mWebView.canGoBack()){
            mWebView.goBack();
        }else{
            if(!isExit){
                Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",
                        Toast.LENGTH_SHORT).show();
                isExit = true; 
            }else{
                finish();
                MainActivity.instance.finish();
            }
        }
    }

    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        System.out.println("ִ�� onDestroy()");  
    }
    
    private class webViewClient extends WebViewClient{
    
    @Override 
    public void onPageStarted(WebView view, String url, Bitmap favicon) {  
      System.out.println("============== onPageStarted =================");
    }  
 
    @Override  
    public void onPageFinished(WebView view, String url) {  
       System.out.println("============== onPageFinished =================");
    }

    @Override 
    //��дshouldOverrideUrlLoading������ʹ������Ӻ�ʹ��������������򿪡� 
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url); 
        System.out.println("=============== webViewClient url =============="+url);
        return true;
    }

    /** 
    /* ʵ��onProgressChanged������� */  
    public void onProgressChanged(WebView view, int newProgress) {  
        System.out.println("================== newProgress ======================"+newProgress);
    }  
   }
} 