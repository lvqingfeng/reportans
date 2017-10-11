package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.utils.SharedPreferencesUtil;
import com.juyikeji.myappjubao.utils.URLConnectionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 添加银行卡
 */
public class AddBank extends Activity implements View.OnClickListener {
    private EditText et_bankcode, et_bankname, et_khh;
    private Button bt_confirm;
    private ImageView iv_fanhui;
    private Spinner spinner1;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private String hankstr = "";

    private static final String url = "addbank";
    private String result = "";

    private String bankname = "";
    private String bankcode = "";
    private String bankusername = "";
    private String bankbranchname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addhank);

        Intent intent = getIntent();
        bankname = intent.getStringExtra("bankname");
        bankcode = intent.getStringExtra("bankcode");
        bankusername = intent.getStringExtra("bankusername");
        bankbranchname = intent.getStringExtra("bankbranchname");

        init();
    }

    /**
     * 实例化控件
     */
    private void init() {
        et_bankcode = (EditText) findViewById(R.id.et_bankcode);
        et_bankname = (EditText) findViewById(R.id.et_bankname);
        et_khh = (EditText) findViewById(R.id.et_khh);
        bt_confirm = (Button) findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(this);

        iv_fanhui = (ImageView) findViewById(R.id.iv_fanhui);
        iv_fanhui.setOnClickListener(this);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        //数据
        data_list = new ArrayList<String>();
        data_list.add("--请选择--");
        data_list.add("中国银行");
        data_list.add("农业银行");
        data_list.add("工商银行");
        data_list.add("建设银行");
        data_list.add("交通银行");
        data_list.add("邮政储蓄银行");

        //适配器
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner1.setAdapter(arr_adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hankstr = data_list.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et_bankcode.setText(bankcode);
        et_bankname.setText(bankusername);
        et_khh.setText(bankbranchname);
        for (int i = 0; i < data_list.size(); i++) {
            if (bankname.equals(data_list.get(i))) {
                spinner1.setSelection(i);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.iv_fanhui:
                finish();
                break;
            //添加
            case R.id.bt_confirm:
                String bankcode = et_bankcode.getText().toString().trim();
                String khh = et_khh.getText().toString().trim();
                bankusername = et_bankname.getText().toString().trim();
                if ("".equals(bankusername) || "".equals(bankcode) || "".equals(khh)) {
                    Toast.makeText(AddBank.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    Pattern p = Pattern.compile("[0-9]*");
                    Matcher m = p.matcher(bankcode);

                    if (!m.matches()) {
                        Toast.makeText(AddBank.this, "银行卡号必须是数字", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if("--请选择--".equals(hankstr)){
                        Toast.makeText(AddBank.this, "请选择银行", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Pattern p1 = Pattern.compile("[\u4e00-\u9fa5]");
                    char[] chars = bankusername.toCharArray();
                    for (int i = 0; i < chars.length; i++) {
                        Matcher m2 = p1.matcher(chars[i]+"");
                        if (!m2.matches()) {
                            Toast.makeText(AddBank.this, "真实姓名必须是汉字", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    request(bankcode, khh, bankusername);
                }

                break;
        }
    }

    /**
     * 请求服务器的方法
     */
    private void request(final String bankcode, final String khh, final String bankusername) {
        final String token = SharedPreferencesUtil.getSharedPreferences(AddBank.this).get("token").toString();
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
                            Toast.makeText(AddBank.this, "添加成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("bankcode", et_bankcode.getText().toString().trim());
                            intent.putExtra("bankname", hankstr);
                            setResult(111, intent);
                            finish();
                        } else {
                            Toast.makeText(AddBank.this, "添加成功", Toast.LENGTH_SHORT).show();
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
                map.put("bankname", hankstr);
                map.put("bankbranchname", khh);
                map.put("bankusername", bankusername);
                map.put("bankcode", bankcode);
                try {
                    result = URLConnectionUtil.sendPostRequest(url, map, "utf-8", 0);
                    Log.i("AddBank", result);
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
