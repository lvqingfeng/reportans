package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.adapter.CheckPlandetailsAdapter;
import com.juyikeji.myappjubao.utils.MyGridView;
import com.juyikeji.myappjubao.utils.URLConnectionUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/5 0005.
 */
public class CheckPlandetailsActivity extends Activity {

    private ImageView iv_close,iv_xx;
    private TextView tv_zt,tv_title,tv_time,tv_adress,tv_text,tv_main,tv_phone;

    private MyGridView gv;
    private CheckPlandetailsAdapter adapter;
    private List<String> list;

    private SharedPreferences sharedPreferences;
    private String infoid;

    /**
     * 进度查询接口
     */
    private String name_space="schedule/getdetail";
    private String result="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_checkplan_details);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLotter(infoid);
    }

    private void initView(){
        sharedPreferences=getSharedPreferences("reportans", MODE_PRIVATE);
        Intent intent=getIntent();
        infoid=intent.getStringExtra("id");
        iv_xx=(ImageView)findViewById(R.id.iv_xx);
        iv_close=(ImageView)findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_zt=(TextView)findViewById(R.id.tv_zt);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_time=(TextView)findViewById(R.id.tv_time);
        tv_adress=(TextView)findViewById(R.id.tv_adress);
        tv_text=(TextView)findViewById(R.id.tv_text);
        tv_main=(TextView)findViewById(R.id.tv_main);
        tv_phone=(TextView)findViewById(R.id.tv_phone);

        gv=(MyGridView)findViewById(R.id.gv);
    }
    /**
     * 请求网络注册
     */
    private void showLotter(final  String isrec) {
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
                map.put("token", sharedPreferences.getString("token", ""));
                map.put("infoid", infoid);
                // 请求网络
                try {
                    result = URLConnectionUtil.sendPostRequest(name_space,
                            map, "UTF-8", 1);
                    Log.i("shared", result);
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
                list=new ArrayList<String>();
                JSONArray data=jobj.getJSONArray("data");
                for (int i=0;i<data.length();i++){
                    JSONObject date=data.getJSONObject(i);
                    //地区
                    String address=date.getString("address");
                    tv_adress.setText(address);
                    //标题
                    String reporttitle=date.getString("reporttitle");
                    tv_title.setText(reporttitle);
                    //联系人
                    String uname=date.getString("uname");
                    tv_main.setText(uname);
                    //电话
                    String phone=date.getString("phone");
                    tv_phone.setText(phone);
                    //内容
                    String reportcontent=date.getString("reportcontent");
                    tv_text.setText("事件说明:"+reportcontent);
                    //标识
                    switch (date.getString("isrec")){
                        case "0":
                            //未采纳
                            iv_xx.setImageResource(R.mipmap.icon_xx2);
                            tv_zt.setText("未采纳");
                            break;
                        case "1":
                            //已采纳
                            iv_xx.setImageResource(R.mipmap.icon_xx1);
                            tv_zt.setText("已采纳");
                            break;
                        case "2":
                            //处理中
                            iv_xx.setImageResource(R.mipmap.icon_xx3);
                            tv_zt.setText("处理中");
                            break;
                    }
                    //时间
                    String reporttime=date.getString("reporttime");
                    tv_time.setText(reporttime);
                    //图片
                    JSONObject imglist=date.getJSONObject("imglist");
                    for (int j=1;j<=imglist.length();j++){
                        list.add(imglist.getString("img"+j));
                    }
                    adapter=new CheckPlandetailsAdapter(CheckPlandetailsActivity.this,list);
                    gv.setAdapter(adapter);
                }
            }else {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
