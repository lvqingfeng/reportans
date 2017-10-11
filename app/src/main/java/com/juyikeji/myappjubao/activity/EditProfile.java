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
import android.widget.TextView;
import android.widget.Toast;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.utils.SharedPreferencesUtil;
import com.juyikeji.myappjubao.utils.URLConnectionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 修改资料
 */
public class EditProfile extends Activity implements View.OnClickListener {
    //返回
    private ImageView iv_fanhui;
    private EditText ed_msg;
    private Button bt_submit;
    private TextView tv_editnickname;

    private String result = "";
    private String url = "";

    String code2 = "";
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        init();
        Intent intent = getIntent();
        code2 = intent.getStringExtra("edit");
        name = intent.getStringExtra("name");
        ed_msg.setText(name);
        if ("1".equals(code2)) {
            tv_editnickname.setText("修改昵称");
            url = "editnickname";
        } else if ("2".equals(code2)) {
            tv_editnickname.setText("修改真实姓名");
            url = "editname";
        } else if ("3".equals(code2)) {
            tv_editnickname.setText("修改身份证号");
            url = "editidnum";
        }else if ("adress".equals(code2)){
            ed_msg.setText("");
            tv_editnickname.setText("现住址");
            url="editadd";
        }
        //设置EdiText光标的位置
        if (!"adress".equals(code2)) {
            ed_msg.setSelection(name.length());
        }
    }

    /**
     * 实例化控件
     */
    private void init() {
        iv_fanhui = (ImageView) findViewById(R.id.iv_fanhui);
        iv_fanhui.setOnClickListener(this);
        ed_msg = (EditText) findViewById(R.id.ed_msg);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        bt_submit.setOnClickListener(this);
        tv_editnickname = (TextView) findViewById(R.id.tv_editnickname);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.iv_fanhui:
                finish();
                break;
            //提交
            case R.id.bt_submit:
                String nickname = ed_msg.getText().toString().trim();
                if (!"".equals(nickname)) {
//                    Pattern p = Pattern.compile("[0-9]*");
//                    Matcher m = p.matcher(nickname);
//                    if(m.matches() ){
//                        Toast.makeText(EditProfile.this,"输入的是数字", Toast.LENGTH_SHORT).show();
//                    }
//                    p=Pattern.compile("[a-zA-Z]");
//                    m=p.matcher(nickname);
//                    if(m.matches()){
//                        Toast.makeText(EditProfile.this,"输入的是字母", Toast.LENGTH_SHORT).show();
//                    }
//                    p=Pattern.compile("[\u4e00-\u9fa5]");
//                    m=p.matcher(nickname);
//                    if(m.matches()){
//                        Toast.makeText(EditProfile.this,"输入的是汉字", Toast.LENGTH_SHORT).show();
//                    }
                    if ("1".equals(code2)) {
                        request(nickname);
                    } else if ("2".equals(code2)) {
                        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
                        char[] chars = nickname.toCharArray();
                        for (int i = 0; i < chars.length; i++) {
                            Matcher m = p.matcher(chars[i]+"");
                            if (!m.matches()) {
                                Toast.makeText(EditProfile.this, "真实姓名必须是汉字", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        request(nickname);

                    } else if ("3".equals(code2)) {
                        Pattern p = Pattern.compile("[0-9]*");
                        Matcher m = p.matcher(nickname);
                        if (m.matches()) {
                            if (nickname.length() == 18) {
                                request(nickname);
                            } else {
                                Toast.makeText(EditProfile.this, "身份证号为18位", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(EditProfile.this, "身份证号必须是数字", Toast.LENGTH_SHORT).show();
                        }
                    }else if ("adress".equals(code2)){
                        request(nickname);
                    }

                } else {
                    Toast.makeText(EditProfile.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

    /**
     * 请求网络
     */
    private void request(final String nickname) {
        final String token = SharedPreferencesUtil.getSharedPreferences(EditProfile.this).get("token").toString();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    JSONObject jobj = null;
                    try {
                        jobj = new JSONObject(result);
                        String code = jobj.getString("status");
                        if (code.equals("1")) {
                            Toast.makeText(EditProfile.this, "修改成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("nickname", ed_msg.getText().toString().trim());
                                setResult(111, intent);

                            finish();
                        } else {
                            Toast.makeText(EditProfile.this, "修改失败", Toast.LENGTH_SHORT).show();
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
                if ("1".equals(code2)) {
                    map.put("nickname", nickname);
                } else if ("2".equals(code2)) {
                    map.put("uname", nickname);
                } else if ("3".equals(code2)) {
                    map.put("idnumber", nickname);
                }else if ("adress".equals(code2)){
                    map.put("area",nickname);
                }

                try {
                    result = URLConnectionUtil.sendPostRequest(url, map, "utf-8", 0);
                    Log.i("EditProfile", result);
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
