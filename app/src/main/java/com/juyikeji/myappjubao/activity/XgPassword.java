package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.utils.SharedPreferencesUtil;
import com.juyikeji.myappjubao.utils.URLConnectionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改密码
 */
public class XgPassword extends Activity implements View.OnClickListener{
    private EditText et_oldpwd,et_newpwd,et_newpwd2;
    private Button bt_confirm;
    private ImageView iv_fanhui;

    private static final String url="editpassword";
    private String result="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xgpassword);

        init();
    }

    /**
     * 实例化控件
     */
    private void init(){
        et_oldpwd= (EditText) findViewById(R.id.et_oldpwd);
        et_newpwd= (EditText) findViewById(R.id.et_newpwd);
        et_newpwd2= (EditText) findViewById(R.id.et_newpwd2);
        bt_confirm= (Button) findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(this);
        iv_fanhui= (ImageView) findViewById(R.id.iv_fanhui);
        iv_fanhui.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_confirm:
                String password=et_oldpwd.getText().toString().trim();
                String repassword=et_newpwd.getText().toString().trim();
                String repassword2=et_newpwd2.getText().toString().trim();
                if(repassword.equals(repassword2)) {
                    request(password,repassword);
                }else{
                    Toast.makeText(XgPassword.this,"新密码输入不一致",Toast.LENGTH_SHORT).show();
                }
                break;
            //返回
            case R.id.iv_fanhui:
                finish();
                break;
        }
    }

    /**
     * 请求服务器的方法
     */
    private void request(final String password, final String repassword) {
        final String token = SharedPreferencesUtil.getSharedPreferences(XgPassword.this).get("token").toString();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    JSONObject jobj = null;
                    try {
                        jobj = new JSONObject(result);
                        String status = jobj.getString("status");
                        if ("1".equals(status)) {
                            Toast.makeText(XgPassword.this, "修改成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(XgPassword.this, jobj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                super.run();
                Map<String, String> map = new HashMap<String, String>();
                map.put("token", token);
                map.put("password", password);
                map.put("repassword", repassword);

                Log.i("DiscloseActivity", map + "");
                try {
                    result = URLConnectionUtil.sendPostRequest(url, map, "utf-8", 0);
                    Log.i("Xgpasswoed", result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message m = new Message();
                m.what = 1;
                // 发送消息到Handler
                handler.sendMessage(m);
            }
        }.start();
    }
}
