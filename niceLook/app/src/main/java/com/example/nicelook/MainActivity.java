package com.example.nicelook;

import android.R.color;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    
    private static final String URL="http://2016la.com";
    
    private Button mLogin;
    private EditText mEtusername;
    private EditText mEtpassword;
    private TextView mLine1;
    private TextView mLine2;
    private TextView mLine3;
    private String mUsername;
    private String mPassword;
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private Activity mActivity;
    private Handler mHandler = new Handler();
    public static MainActivity instance = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		instance = this;
		init();
    }
    private void init() 
    {
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        mActivity = this;
        mContext = this;
        mEtusername = (EditText) findViewById(R.id.etusername);
        mEtpassword = (EditText) findViewById(R.id.etpassword);
        mLogin = (Button) findViewById(R.id.login);
        mLogin.setOnClickListener(new LoginOnclick());
        mProgressDialog =new ProgressDialog(MainActivity.this);
        mLine1 = (TextView)findViewById(R.id.line1);
        mLine1.setTextColor(getResources().getColor(R.color.select_line_text_press_color));
        mLine1.setBackgroundResource(R.drawable.select_line_press);
        mLine2 = (TextView)findViewById(R.id.line2);
        mLine3 = (TextView)findViewById(R.id.line3);
        mLine1.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                mLine1.setBackgroundResource(R.drawable.select_line_press);
                mLine2.setBackgroundResource(R.drawable.select_line);
                mLine3.setBackgroundResource(R.drawable.select_line);
                changeTextColor((TextView) v);
            }
        });
        mLine2.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                mLine1.setBackgroundResource(R.drawable.select_line);
                mLine2.setBackgroundResource(R.drawable.select_line_press);
                mLine3.setBackgroundResource(R.drawable.select_line);
                changeTextColor((TextView) v);
            }
        });
        mLine3.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                mLine1.setBackgroundResource(R.drawable.select_line);
                mLine2.setBackgroundResource(R.drawable.select_line);
                mLine3.setBackgroundResource(R.drawable.select_line_press);
                changeTextColor((TextView) v);
            }
            
        });
        mProgressDialog.setTitle("登录中");
        mProgressDialog.setMessage("登录中，马上就好");
    }

    private void changeTextColor(TextView view){
        if(mLine1 == view){
            mLine1.setTextColor(getResources().getColor(R.color.select_line_text_press_color));
            mLine2.setTextColor(getResources().getColor(R.color.select_line_text_color));
            mLine3.setTextColor(getResources().getColor(R.color.select_line_text_color));
        }else if(mLine2 == view){
            mLine1.setTextColor(getResources().getColor(R.color.select_line_text_color));
            mLine2.setTextColor(getResources().getColor(R.color.select_line_text_press_color));
            mLine3.setTextColor(getResources().getColor(R.color.select_line_text_color));
        }else if(mLine3 == view){
            mLine1.setTextColor(getResources().getColor(R.color.select_line_text_color));
            mLine2.setTextColor(getResources().getColor(R.color.select_line_text_color));
            mLine3.setTextColor(getResources().getColor(R.color.select_line_text_press_color));
        }
    }
    
    private class LoginOnclick implements OnClickListener
    {
        public void onClick(View arg0) {
            mUsername = mEtusername.getText().toString().trim();
            if (mUsername == null || mUsername.length()<=0 ) 
            {       
                mEtusername.requestFocus();
                Toast.makeText(getApplicationContext(), "对不起，用户名不能为空",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            else 
            {
                mUsername=mEtusername.getText().toString().trim();
            }
            mPassword=mEtpassword.getText().toString().trim();
            if (mPassword==null||mPassword.length()<=0) 
            {       
                mEtpassword.requestFocus();
                Toast.makeText(getApplicationContext(), "对不起，密码不能为空",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            else 
            {
                mPassword=mEtpassword.getText().toString().trim();
            }
            mProgressDialog.show();
            new Thread(new Runnable() {
                public void run() {
                    Operaton operaton=new Operaton();
                    String result = operaton.loginByHttpClientPOST(getApplicationContext(),mUsername,mPassword);              
                    Message msg=new Message();
                    msg.obj=result;
                    handler.sendMessage(msg);
                    handler.postDelayed(new Runnable(){

                        @Override
                        public void run() {
                            System.out.println("================== get delay cookies =================");
                            Operaton operaton=new Operaton();
                            operaton.getTestCookies();
                        }
                        
                    }, 2000);
                }
            }).start();
        }
    }

    Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            String string=(String) msg.obj;
            mProgressDialog.dismiss();
            Toast.makeText(MainActivity.this, string, 0).show();
            super.handleMessage(msg);
        }   
    };
}
