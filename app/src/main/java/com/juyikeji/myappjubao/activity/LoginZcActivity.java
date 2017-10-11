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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2016/4/2 0002.
 */
public class LoginZcActivity extends Activity implements View.OnClickListener {

    private ImageView iv_close;
    private EditText edit_user, edit_pswd, edit_pswdagain, edit_yzm, edit_phone;
    private TextView tv_yzm, tv_loginzc, tv_tjr, tv_tjdw;
    private Spinner sp_choose, sp_sp;
    private String[] sp1,sp2,sp3,ssp,sp1id,sp2id,sp3id;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter1;
    private int spbs=0;
    private String tj1 = "";//获取spinner选择的参数
    private boolean spp = false;//判断是推荐单位还是推荐人，默认为推荐单位

    int i = 45;
    public final static String APPKEY = "1128208fa354e";
    public final static String APPSECRET = "1a100aa05bf8df664be918b1c502df7d";

    /**
     * 注册的接口
     */
    private String name_space = "register";
    private String result = "";

    /**
     * sp的接口
     */
    private String sp_space = "getpartment";
    private String sp_result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loginzc);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);

        edit_user = (EditText) findViewById(R.id.edit_user);
        edit_pswd = (EditText) findViewById(R.id.edit_pswd);
        edit_pswdagain = (EditText) findViewById(R.id.edit_pswdagain);
        edit_yzm = (EditText) findViewById(R.id.edit_yzm);
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        tv_yzm = (TextView) findViewById(R.id.tv_yzm);
        tv_yzm.setOnClickListener(this);
        tv_loginzc = (TextView) findViewById(R.id.tv_loginzc);
        tv_loginzc.setOnClickListener(this);
        tv_tjr = (TextView) findViewById(R.id.tv_tjr);
        tv_tjr.setOnClickListener(this);
        tv_tjdw = (TextView) findViewById(R.id.tv_tjdw);
        tv_tjdw.setOnClickListener(this);
        sp_sp = (Spinner) findViewById(R.id.sp_sp);
        sp_choose = (Spinner) findViewById(R.id.sp_choose);

        //添加事件Spinner事件监听SpinnerSelectedListener()
        sp_choose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (spbs) {
                    case 0:
                        tj1 = sp1id[position];
                        break;
                    case 1:
                        tj1 = sp2id[position];
                        break;
                    case 2:
                        tj1 = sp3id[position];
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //添加事件Spinner事件监听SpinnerSelectedListener()
        sp_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
//                        sp1= new String[]{"梧田派出所", "开发区", "新桥", "郭溪", "娄桥", "潘桥", "瞿溪", "茶山", "仙岩", "景山", "三垟", "丽岙", "泽雅"};
                        adapter = new ArrayAdapter<String>(LoginZcActivity.this, android.R.layout.simple_spinner_item, sp1);
                        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                        sp_choose.setAdapter(adapter);
                        tj1 = sp1id[0];
                        spbs = 0;
                        break;
                    case 1:
//                    sp1= new String[]{"情报大队", "刑侦大队", "户政大队", "流管大队", "治安大队", "经侦大队", "禁毒大队", "网警大队", "特巡警大队"
                        adapter = new ArrayAdapter<String>(LoginZcActivity.this, android.R.layout.simple_spinner_item, sp2);
                        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                        sp_choose.setAdapter(adapter);
                        tj1 = sp2id[0];
                        spbs = 1;
                        break;
                    case 2:
                        adapter = new ArrayAdapter<String>(LoginZcActivity.this, android.R.layout.simple_spinner_item, sp3);
                        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                        sp_choose.setAdapter(adapter);
                        tj1 = sp3id[0];
                        spbs = 2;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        //请求网络获取spinner数据
        showSp();
    }

    /**
     * 页面点击事件
     *
     * @param v
     */
    int blue = 0xff2867d1;
    int black = 0xff000000;

    @Override
    public void onClick(View v) {
        String phoneNums = edit_user.getText().toString().trim();
        switch (v.getId()) {
            case R.id.tv_tjr:
                spp=true;
                tv_tjr.setTextColor(blue);
                tv_tjdw.setTextColor(black);
                sp_sp.setVisibility(View.GONE);
                sp_choose.setVisibility(View.GONE);
                edit_phone.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_tjdw:
                spp=false;
                tv_tjr.setTextColor(black);
                tv_tjdw.setTextColor(blue);
                sp_sp.setVisibility(View.VISIBLE);
                sp_choose.setVisibility(View.VISIBLE);
                edit_phone.setVisibility(View.GONE);
                break;
            case R.id.iv_close:
                finish();
                break;
            case R.id.tv_yzm:
                //验证码
                if (!judgePhoneNums(phoneNums)) {
                    return;
                }
                if (!URLConnectionUtil.isOpenNetwork(this)) {
                    Toast.makeText(LoginZcActivity.this, "网络无连接，请检查网络！", Toast.LENGTH_SHORT).show();
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
                if (!edit_pswd.getText().toString().trim().equals(edit_pswdagain.getText().toString().trim())) {
                    Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                showLotter();
                break;
        }
    }

    /**
     * 请求网络获取推荐机构
     */
    private void showSp() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    getSp(sp_result);
                }
            }
        };
        // 启动线程来执行任务
        new Thread() {
            public void run() {
                Map<String, String> map = new HashMap<String, String>();
                // 请求网络
                try {
                    sp_result = URLConnectionUtil.sendPostRequest(sp_space,
                            map, "UTF-8", 0);
                    Log.i("sp_result", sp_result);
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

    private void getSp(String json) {
        try {
            JSONObject jobj = new JSONObject(json);
            if ("1".equals(jobj.getString("status"))) {
                JSONArray data = jobj.getJSONArray("data");
                ssp = new String[data.length()];
                for (int i = 0; i < data.length(); i++) {
                    JSONObject tjdanwei = data.getJSONObject(i);
                    ssp[i]=tjdanwei.getString("parentname")+" ▼";//获取大类数据
                    JSONArray tjxiaolei=tjdanwei.getJSONArray("depts");//小类数组
                    int size=tjxiaolei.length();//数组长度
                        switch (i) {//根据大类给小类赋值
                            case 0:
                                sp1 = new String[size];
                                sp1id=new String[size];
                                for (int j=0;j<size;j++) {
                                    JSONObject xiaolei=tjxiaolei.getJSONObject(j);
                                    sp1[j] =xiaolei.getString("secdeptname");
                                    sp1id[j] =xiaolei.getString("deptid");
                                }
                                break;
                            case 1:
                                sp2 = new String[size];
                                sp2id=new String[size];
                                for (int j=0;j<size;j++) {
                                    JSONObject xiaolei=tjxiaolei.getJSONObject(j);
                                    sp2[j] =xiaolei.getString("secdeptname");
                                    sp2id[j] =xiaolei.getString("deptid");
                                }
                                break;
                            case 2:
                                sp3 = new String[size];
                                sp3id=new String[size];
                                for (int j=0;j<size;j++) {
                                    JSONObject xiaolei=tjxiaolei.getJSONObject(j);
                                    sp3[j] =xiaolei.getString("secdeptname");
                                    sp3id[j] =xiaolei.getString("deptid");
                                }
                                break;
                        }
                }
            } else {
                ssp = new String[1];
                ssp[0] = "未获取到推荐单位";
                sp1=new String[1];
                sp1[0]="未获取到推荐单位";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ssp = new String[1];
            ssp[0] = "未获取到推荐单位";
            sp1=new String[1];
            sp1[0]="未获取到推荐单位";
        }
        adapter1 = new ArrayAdapter<String>(LoginZcActivity.this, android.R.layout.simple_spinner_item, ssp);
        //设置下拉列表的风格
        adapter1.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        //将adapter 添加到spinner中
        sp_sp.setAdapter(adapter1);
        //设置默认值
        sp_sp.setVisibility(View.VISIBLE);
        /**
         * 第二个spinner
         */
        adapter = new ArrayAdapter<String>(LoginZcActivity.this, android.R.layout.simple_spinner_item, sp1);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        //将adapter 添加到spinner中
        sp_choose.setAdapter(adapter);
        //设置默认值
        sp_choose.setVisibility(View.VISIBLE);

        tj1 = sp1id[0];
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
                if (spp) {
                    map.put("recommend", edit_phone.getText().toString());
                    map.put("rectype", "1");
                } else {
                    map.put("recommend", tj1);
                    map.put("rectype", "0");
                }
                map.put("identify", "android");
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
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("reportans", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user", edit_user.getText().toString());
                editor.putString("pswd", edit_pswd.getText().toString());
                editor.commit();
                finish();
            } else {
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
        Toast.makeText(LoginZcActivity.this, "手机号码输入有误！", Toast.LENGTH_SHORT).show();
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
