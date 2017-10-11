package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.utils.URLConnectionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/2 0002.
 */
public class LoginActivity extends Activity implements View.OnClickListener{

    private String imei="";
    private ImageView iv_close;
    private EditText edit_user,edit_pswd;
    private TextView tv_login,tv_forget,tv_zc;

    /**
     * 登录的接口
     */
    private String name_space="login";
    private String result="";
    /**
     * 登录的请求参数
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        imei=((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        init();
    }

    private void init(){
        SharedPreferences sharedPreferences=getSharedPreferences("reportans",MODE_PRIVATE);
        String user=sharedPreferences.getString("user", "");
        String pswd=sharedPreferences.getString("pswd","");
        iv_close=(ImageView)findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);

        edit_user=(EditText)findViewById(R.id.edit_user);
        edit_user.setText(user);
        edit_pswd=(EditText)findViewById(R.id.edit_pswd);
        edit_pswd.setText(pswd);
        edit_user.addTextChangedListener(textWatcher);


        tv_login=(TextView)findViewById(R.id.tv_login);
        tv_login.setOnClickListener(this);
        tv_forget=(TextView)findViewById(R.id.tv_forget);
        tv_forget.setOnClickListener(this);
        tv_zc=(TextView)findViewById(R.id.tv_zc);
        tv_zc.setOnClickListener(this);

    }

    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            edit_pswd.setText("");
        }
    };
    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_close:
                finish();
                break;
            case R.id.tv_login:
                showLotter();
                break;
            case R.id.tv_forget:
                startActivity(new Intent(this,FindpwdActivity.class));
                break;
            case R.id.tv_zc:
                startActivity(new Intent(this,LoginZcActivity.class));
                break;
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
                map.put("username", edit_user.getText().toString());
                map.put("password", edit_pswd.getText().toString());
                map.put("imei", imei);
                // 请求网络
                try {
                    result = URLConnectionUtil.sendPostRequest(name_space,
                            map, "UTF-8", 0);
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
            String msg = jobj.getString("msg");
            if (status.equals("1")) {
                String token=jobj.getString("token");
                SharedPreferences sharedPreferences=getSharedPreferences("reportans",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("login",true);
                editor.putString("token", token);
                editor.putString("user",edit_user.getText().toString());
                editor.putString("pswd",edit_pswd.getText().toString());
                editor.commit();
                finish();
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
