package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.content.Intent;
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
 * 添加支付宝
 */
public class AddZhiFuBao extends Activity implements View.OnClickListener {
    private ImageView iv_fanhui;
    private EditText et_zfb, et_name;
    private Button bt_submit;

    private static final String url = "addtaobao";
    private String result = "";

    private String zhifubaocode="";
    private String zhifubaoname="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addzhifubao);
        Intent intent=getIntent();
        zhifubaocode=intent.getStringExtra("zhifubaocode");
        zhifubaoname=intent.getStringExtra("zhifubaoname");
        init();
    }

    /**
     * 实例化控件
     */
    private void init() {
        iv_fanhui = (ImageView) findViewById(R.id.iv_fanhui);
        iv_fanhui.setOnClickListener(this);
        et_zfb = (EditText) findViewById(R.id.et_zfb);
        et_name = (EditText) findViewById(R.id.et_name);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        bt_submit.setOnClickListener(this);

        et_zfb.setText(zhifubaocode);
        et_name.setText(zhifubaoname);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.iv_fanhui:
                finish();
                break;
            //绑定
            case R.id.bt_submit:
                String weixinname = et_name.getText().toString().trim();
                String weixincode = et_zfb.getText().toString().trim();
                if ("".equals(weixincode)) {
                    Toast.makeText(AddZhiFuBao.this, "请输入支付宝账号", Toast.LENGTH_SHORT).show();
                } else if ("".equals(weixinname)) {
                    Toast.makeText(AddZhiFuBao.this, "请输入支付宝姓名", Toast.LENGTH_SHORT).show();
                } else {
                    request(weixinname, weixincode);
                }
                break;
        }
    }

    /**
     * 请求网络
     */
    private void request(final String weixinname, final String weixincode) {
        final String token = SharedPreferencesUtil.getSharedPreferences(AddZhiFuBao.this).get("token").toString();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    JSONObject jobj = null;
                    try {
                        jobj = new JSONObject(result);
                        String code = jobj.getString("status");
                        if (code.equals("1")) {
                            Toast.makeText(AddZhiFuBao.this, "修改成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("nickname", et_zfb.getText().toString().trim());
                            setResult(111, intent);
                            finish();
                        } else {
                            Toast.makeText(AddZhiFuBao.this, "修改失败", Toast.LENGTH_SHORT).show();
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
                Map<String, String> map = new HashMap<String, String>();
                // 获取令牌放入请求参数；
                map.put("token", token);
                map.put("taobaoname", weixinname);
                map.put("taobaocode", weixincode);

                try {
                    result = URLConnectionUtil.sendPostRequest(url, map, "utf-8", 0);
                    Log.i("AddZhifubao", result);
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
