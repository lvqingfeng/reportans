package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.utils.URLConnectionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/4/2 0002.
 */
public class FindpwdActivity extends Activity implements View.OnClickListener{

    private ImageView iv_close;
    private EditText edit_user,edit_pswd,edit_pswdagain,edit_yzm,edit_phone;
    private TextView tv_yzm,tv_loginzc;

    int i = 45;
    public final static String APPKEY = "1128208fa354e";
    public final static String APPSECRET = "1a100aa05bf8df664be918b1c502df7d";

    /**
     * 注册的接口
     */
    private String name_space="retrievepwd";
    private String result="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_findpwd);
        initView();
    }


    private void initView(){
        iv_close=(ImageView)findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);

        edit_user=(EditText)findViewById(R.id.edit_user);
        edit_pswd=(EditText)findViewById(R.id.edit_pswd);
        edit_pswdagain=(EditText)findViewById(R.id.edit_pswdagain);
        edit_yzm=(EditText)findViewById(R.id.edit_yzm);
        edit_phone=(EditText)findViewById(R.id.edit_phone);
        tv_yzm=(TextView)findViewById(R.id.tv_yzm);
        tv_yzm.setOnClickListener(this);
        tv_loginzc=(TextView)findViewById(R.id.tv_loginzc);
        tv_loginzc.setOnClickListener(this);

        //短信验证
        SMSSDK.initSDK(this, APPKEY, APPSECRET);
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }

    /**
     * 页面点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        String phoneNums = edit_user.getText().toString().trim();
        switch (v.getId()){
            case R.id.iv_close:
                finish();
                break;
            case R.id.tv_yzm:
                //验证码
                if (!judgePhoneNums(phoneNums)) {
                    return;
                }
                if (!URLConnectionUtil.isOpenNetwork(this)) {
                    Toast.makeText(FindpwdActivity.this, "网络无连接，请检查网络！", Toast.LENGTH_SHORT).show();
                } else {
                    //  通过sdk发送短信验证
                    SMSSDK.getVerificationCode("86", phoneNums);

                    tv_yzm.setClickable(false);
                    tv_yzm.setText("重新发送（" + i + ")");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (; i > 0; i--) {
                                handler.sendEmptyMessage(-9);
                                if (i <= 0) {
                                    break;
                                }
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            handler.sendEmptyMessage(-8);
                        }
                    }).start();
                }
                break;
            case R.id.tv_loginzc:
                if (!edit_pswd.getText().toString().trim().equals(edit_pswdagain.getText().toString().trim())){
                    Toast.makeText(this,"两次输入的密码不一致",Toast.LENGTH_SHORT).show();
                    return;
                }
                showLotter();
                break;
        }
    }
    /**
     * 请求网络注册
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
                map.put("phone", edit_user.getText().toString());
                map.put("password", edit_pswd.getText().toString());
                map.put("code", edit_yzm.getText().toString());
                map.put("identify", "android");
                // 请求网络
                try {
                    result = URLConnectionUtil.sendPostRequest(name_space,
                            map, "UTF-8", 0);
                    Log.i("result", result);
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
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences=getSharedPreferences("reportans", MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("pswd",edit_pswd.getText().toString());
                editor.commit();
            }else {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送验证码
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                tv_yzm.setText("重新发送(" + i + ")");
            } else if (msg.what == -8) {
                tv_yzm.setText("获取验证码");
                tv_yzm.setClickable(true);
                i = 45;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                if (result == SMSSDK.RESULT_COMPLETE) {
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "验证码已经发送",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                }
            }
        }

    };
    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     * @return
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11) && isMobileNO(phoneNums)) {

            return true;
        }
        Toast.makeText(FindpwdActivity.this, "手机号码输入有误！", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     *
     * @param mobileNums
     * @return
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][3578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

}
