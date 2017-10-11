package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.utils.SharedPreferencesUtil;
import com.juyikeji.myappjubao.utils.URLConnectionUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 个人中心
 */
public class PersonalActivity extends Activity implements View.OnClickListener {
    //登录注册//修改密码//关于我们//意见反馈
    private RelativeLayout LoginOrRegister, rl_xgmm, rl_about, rl_feedback;
    private ImageView iv_right, iv_touxiang,iv_fanhui;
    private TextView tv_user, tv_phone;

    private Button bt_exit;

    private String result = "";
    private String name_space = "getinfo";
    private String phone = "";

    private String result2 = "";
    private String name_space2 = "logout";
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        init();
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        if ((Boolean) SharedPreferencesUtil.getSharedPreferences(PersonalActivity.this).get("login")) {
            iv_right.setVisibility(View.GONE);
            LoginOrRegister.setClickable(false);

            request();
        } else {
            tv_user.setText("立即登录/注册");
            tv_phone.setText("");
            iv_right.setVisibility(View.VISIBLE);
            LoginOrRegister.setClickable(true);
        }
    }

    /**
     * 实例化控件并设置监听
     */
    private void init() {
        iv_fanhui= (ImageView) findViewById(R.id.iv_fanhui);
        iv_fanhui.setOnClickListener(this);
        LoginOrRegister = (RelativeLayout) findViewById(R.id.LoginOrRegister);
        LoginOrRegister.setOnClickListener(this);
        rl_xgmm = (RelativeLayout) findViewById(R.id.rl_xgmm);
        rl_xgmm.setOnClickListener(this);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        tv_user = (TextView) findViewById(R.id.tv_user);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        iv_touxiang = (ImageView) findViewById(R.id.iv_touxiang);
        iv_touxiang.setOnClickListener(this);
        rl_about = (RelativeLayout) findViewById(R.id.rl_about);
        rl_about.setOnClickListener(this);
        rl_feedback = (RelativeLayout) findViewById(R.id.rl_feedback);
        rl_feedback.setOnClickListener(this);
        bt_exit= (Button) findViewById(R.id.bt_exit);
        bt_exit.setOnClickListener(this);
    }

    /**
     * 获取用户信息
     */
    private void request() {
        final String token = SharedPreferencesUtil.getSharedPreferences(PersonalActivity.this).get("token").toString();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    JSONObject jobj = null;
                    try {
                        jobj = new JSONObject(result);
                        JSONObject jo = jobj.getJSONObject("data");
                        String headImg = jo.getString("headImg");
                        String nickname = jo.getString("nickname");
                        phone = jo.getString("phone");
                        tv_phone.setText(phone);
                        tv_user.setText(nickname);
                        imageLoader.displayImage(headImg, iv_touxiang, SharedPreferencesUtil.getDefaultOptions());

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
                Log.i("PersonalActivity", token);
                try {
                    result = URLConnectionUtil.sendPostRequest(name_space, map, "utf-8", 0);
                    Log.i("PersonalActivity", result);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //登录、注册
            case R.id.LoginOrRegister:
                startActivity(new Intent(PersonalActivity.this, LoginActivity.class));
                break;
            //个人资料
            case R.id.iv_touxiang:
                Intent intent = new Intent(PersonalActivity.this, Myprofile.class);
//                intent.putExtra("result", result);
                startActivity(intent);
                break;
            //修改密码
            case R.id.rl_xgmm:
                startActivity(new Intent(PersonalActivity.this, XgPassword.class));
                break;
            //关于我们
            case R.id.rl_about:
                startActivity(new Intent(PersonalActivity.this, AboutUs.class));
                break;
            //意见反馈
            case R.id.rl_feedback:
                startActivity(new Intent(PersonalActivity.this, Feedback.class));
                break;
            //退出
            case R.id.bt_exit:
                request2();
                break;
            //返回
            case R.id.iv_fanhui:
                finish();
                break;
        }
    }

    /**
     * 退出登录
     */
    private void request2() {
        final String token = SharedPreferencesUtil.getSharedPreferences(PersonalActivity.this).get("token").toString();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    JSONObject jobj = null;
                    try {
                        jobj = new JSONObject(result);
                        String status=jobj.getString("status");
                        if("1".equals(status)){
                            Toast.makeText(PersonalActivity.this,"成功退出",Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences=getSharedPreferences("reportans",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putBoolean("login", false);
                            editor.commit();
                            finish();
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
                Log.i("PersonalActivity", token);
                try {
                    result2 = URLConnectionUtil.sendPostRequest(name_space2, map, "utf-8", 0);
                    Log.i("PersonalActivity", result2);
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
