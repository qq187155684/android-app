package com.example.nicelook;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class Operaton 
{
	
    private static HttpClient mClient = null;
    
	/**
     * ͨ��HttpUrlConnection����POST����
     * 
     * @param username
     * @param password
     * @return
     */
    public static String loginByHttpClientPOST(Context context,String username, String password) {
        String path = "http://kb1122.com";
        try {
            mClient = new DefaultHttpClient(); // ����һ���ͻ���
            HttpPost httpPost = new HttpPost(path); // ��װPOST����
            HttpGet httpGet = new HttpGet(path);
            // ���÷��͵�ʵ�����
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair(username, username));
            parameters.add(new BasicNameValuePair(password, password));
            httpPost.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
            String url111 = httpPost.getEntity().toString();
            System.out.println("================== url111 ================="+url111);
            HttpResponse response = mClient.execute(httpPost); // ִ��POST����
            //HttpResponse response = client.execute(httpGet); // ִ��GET����
            // ����ʹ��Http�ͻ��˷������󣬲���ȡ��Ӧ����
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
             String result = "";
             InputStream instream = response.getEntity().getContent();  
             BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));  
             System.out.println("=================="+reader.readLine());
             System.out.println("=================="+Arrays.toString(response.getAllHeaders())); 
             //��ȡCookie
             String sessionID = response.getFirstHeader("Set-Cookie").getName();
             String s = response.getFirstHeader("Set-Cookie").getElements().toString();
             System.out.println("================ response Session ===================="+response.getHeaders("Session").toString());
             System.out.println("================ response s ===================="+s); 
             String cookies = getCookies((DefaultHttpClient)mClient);
             
             
                System.out.println("======== login httpClient sucessfull ================");
                String url = httpPost.getURI().toString();
                System.out.println("================== url!!!!! ================="+url);
//                Intent intent=new Intent();
//                intent.putExtra("web_url", url);
//                intent.putExtra("web_name", username);
//                intent.putExtra("web_pwd", password);
//                intent.putExtra("web_result", result);
//                intent.setClass(context, WebViewActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//                context.startActivity(intent);
                return "OK";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }
    
    Handler handler = new Handler();
    
    public static Map<String, String> cookieStore = new HashMap<String, String>();
    
    private static String getCookies(DefaultHttpClient httpClient){
            List<Cookie> cs = httpClient.getCookieStore().getCookies();// ��ȡԶ��cookie
            boolean flag = false;// �Ƿ����cookie�ļ�
            System.out.println("============== getCookies cs ==============="+cs);
            if (cs != null) {
                for (Cookie cookie : cs) {
                    String name = cookie.getName();
                    String val = cookie.getValue();
                    String cacheVal = cookieStore.get(name);
                    if (!val.equals(cacheVal)) {// ����cookie��Զ��cookie��ͬ�����򸲸Ǳ���cookie
                        flag = true;
                        cookieStore.put(name, val);// �����ڴ��е�cookie
                        cookieStore.put("Path", cookie.getPath());
                        cookieStore.put("Domain", cookie.getDomain());
                        // cookieStore.put("Comment", cookie.getComment());
                        // cookieStore.put("Ports",
                        // String.valueOf(cookie.getPorts()));
                        // cookieStore.put("CommentURL",
                        // cookie.getCommentURL());
                        continue;
                    }
                }
            }
            System.out.println("=============== cookieStore ==============="+cookieStore);
            return null;
    }
    
    public static String getTestCookies(){
        List<Cookie> cs = ((DefaultHttpClient)mClient).getCookieStore().getCookies();// ��ȡԶ��cookie
        boolean flag = false;// �Ƿ����cookie�ļ�
        System.out.println("============== getCookies test cs !!!!! ==============="+cs);
        if (cs != null) {
            for (Cookie cookie : cs) {
                String name = cookie.getName();
                String val = cookie.getValue();
                String cacheVal = cookieStore.get(name);
                if (!val.equals(cacheVal)) {// ����cookie��Զ��cookie��ͬ�����򸲸Ǳ���cookie
                    flag = true;
                    cookieStore.put(name, val);// �����ڴ��е�cookie
                    cookieStore.put("Path", cookie.getPath());
                    cookieStore.put("Domain", cookie.getDomain());
                    // cookieStore.put("Comment", cookie.getComment());
                    // cookieStore.put("Ports",
                    // String.valueOf(cookie.getPorts()));
                    // cookieStore.put("CommentURL",
                    // cookie.getCommentURL());
                    continue;
                }
            }
        }
        System.out.println("=============== cookieStore ==============="+cookieStore);
        return null;
}
}
