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
public class BulletinBoarddetailsActivity extends Activity {

    private ImageView iv_close;
    private TextView tv_title,tv_time,tv_text;

    private MyGridView gv;
    private CheckPlandetailsAdapter adapter;
    private List<String> list;

    private SharedPreferences sharedPreferences;
    private String infoid;

    /**
     * 进度查询接口
     */
    private String name_space="notice/getdetail";
    private String result="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bulletinboard_details);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLotter();
    }

    private void initView(){
        sharedPreferences=getSharedPreferences("reportans", MODE_PRIVATE);
        Intent intent=getIntent();
        infoid=intent.getStringExtra("infoid");
        iv_close=(ImageView)findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_time=(TextView)findViewById(R.id.tv_time);
        tv_text=(TextView)findViewById(R.id.tv_text);

        gv=(MyGridView)findViewById(R.id.gv);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
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
                map.put("token", sharedPreferences.getString("token", ""));
                map.put("infoid", infoid);
                // 请求网络
                try {
                    result = URLConnectionUtil.sendPostRequest(name_space,
                            map, "UTF-8", 1);
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
                JSONObject data=jobj.getJSONObject("data");
                    //标题
                    String title=data.getString("title");
                    tv_title.setText(title);
                    //内容
                    String content=data.getString("content");
                    tv_text.setText("\t\t"+content);
                    //时间
                    String crttime=data.getString("crttime");
                    tv_time.setText(crttime);
                    //图片
                    JSONObject imglist=data.getJSONObject("imglist");
                    for (int j=1;j<=imglist.length();j++){
                        list.add(imglist.getString("img"+j));
                    adapter=new CheckPlandetailsAdapter(BulletinBoarddetailsActivity.this,list);
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
