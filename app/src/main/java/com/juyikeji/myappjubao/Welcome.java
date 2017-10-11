package com.juyikeji.myappjubao;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;

import com.juyikeji.myappjubao.utils.SharedPreferencesUtil;
import com.juyikeji.myappjubao.utils.URLConnectionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by jyg on 2016/3/1
 */
public class Welcome extends Activity {

    private boolean isFirstIn = false;
    private static final int TIME = 2000;
    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    private String result="";
    private String name_space="login";
    private String imei="";
    private String user="";
    private String pswd="";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;

                case GO_GUIDE:
                    goHome();
//                    goGuide();//第一次的引导页
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        imei=((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        init();
        sharedPreferences = getSharedPreferences("reportans", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        user=sharedPreferences.getString("user", "");
        pswd=sharedPreferences.getString("pswd", "");
        if ("".equals(user)||"".equals(pswd)){
            editor.putBoolean("login", false);
            editor.commit();
        }else {
            showLotter();
        }
    }

    /**
     * 请求网络登录
     */
    private void showLotter() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    getRegister(result);
                }
            }
        };
        // 启动线程来执行任务
        new Thread() {
            public void run() {
                Map<String, String> map = new HashMap<String, String>();
                map.put("username", user);
                map.put("password", pswd);
                map.put("imei", imei);
                // 请求网络
                try {
                    result = URLConnectionUtil.sendPostRequest(name_space,
                            map, "UTF-8",0);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Message m = new Message();
                m.what = 1;
                // 发送消息到Handler
                handler.sendMessage(m);
            }
        }.start();
    }

    private void getRegister(String json) {
        try {
            JSONObject jobj = new JSONObject(json);
            String status = jobj.getString("status");
            if (status.equals("1")) {
                String token=jobj.getString("token");
                editor.putString("token",token);
                editor.putBoolean("login",true);
                editor.commit();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    private void init() {
        //第一次登陆

        SharedPreferences perPreferences = getSharedPreferences("jike", MODE_PRIVATE);
        isFirstIn = perPreferences.getBoolean("isFirstIn", true);
        if (!isFirstIn) {
            mHandler.sendEmptyMessageDelayed(GO_HOME, TIME);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, TIME);
            SharedPreferences.Editor editor = perPreferences.edit();
            editor.putBoolean("isFirstIn", false);
            editor.commit();
        }

    }

    private void goHome() {
        boolean login = (boolean) SharedPreferencesUtil.getSharedPreferences(this).get("login");
        if (login) {
            Intent intent = new Intent(Welcome.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(Welcome.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void goGuide() {
        Intent i = new Intent(Welcome.this, Guide.class);
        startActivity(i);
        finish();
    }

}
