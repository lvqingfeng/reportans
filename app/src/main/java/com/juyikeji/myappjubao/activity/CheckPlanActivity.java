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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.adapter.CheckPlanAdapter;
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
public class CheckPlanActivity extends Activity implements View.OnClickListener{

    private RelativeLayout rel_progress;
    private ImageView iv_close;
    private TextView tv_all,tv_ycn,tv_wcn,tv_clz;
    private View v_all,v_ycn,v_wcn,v_clz;

    private ListView lv;
    private CheckPlanAdapter adapter;
    private List<Map<String,String>> list;

    private SharedPreferences sharedPreferences;

    /**
     * 进度查询接口
     */
    private String name_space="schedule";
    private String result="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_checkplan);
        initView();
        showLotter("-1");
    }

    private void initView(){
        sharedPreferences=getSharedPreferences("reportans", MODE_PRIVATE);

        rel_progress=(RelativeLayout)findViewById(R.id.rel_progress);

        iv_close=(ImageView)findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);

        tv_all=(TextView)findViewById(R.id.tv_all);
        tv_all.setOnClickListener(this);
        tv_ycn=(TextView)findViewById(R.id.tv_ycn);
        tv_ycn.setOnClickListener(this);
        tv_wcn=(TextView)findViewById(R.id.tv_wcn);
        tv_wcn.setOnClickListener(this);
        tv_clz=(TextView)findViewById(R.id.tv_clz);
        tv_clz.setOnClickListener(this);

        v_all=(View)findViewById(R.id.v_all);
        v_ycn=(View)findViewById(R.id.v_ycn);
        v_wcn=(View)findViewById(R.id.v_wcn);
        v_clz=(View)findViewById(R.id.v_clz);

        lv=(ListView)findViewById(R.id.lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(CheckPlanActivity.this,CheckPlandetailsActivity.class);
                intent.putExtra("id",list.get(position).get("infoid"));
                startActivity(intent);
            }
        });
    }

    /**
     * 页面点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_close:
                finish();
                break;
            case R.id.tv_all:
                rel_progress.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);
                clearView();
                v_all.setBackgroundResource(R.color.blue);
                showLotter("-1");
                break;
            case R.id.tv_ycn:
                rel_progress.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);
                clearView();
                v_ycn.setBackgroundResource(R.color.blue);
                showLotter("1");
                break;
            case R.id.tv_wcn:
                rel_progress.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);
                clearView();
                v_wcn.setBackgroundResource(R.color.blue);
                showLotter("0");
                break;
            case R.id.tv_clz:
                rel_progress.setVisibility(View.VISIBLE);
                lv.setVisibility(View.GONE);
                clearView();
                v_clz.setBackgroundResource(R.color.blue);
                showLotter("2");
                break;
        }
    }
    /**
     * 清空选中状态
     */
    private void clearView(){
        v_all.setBackgroundResource(R.color.white);
        v_ycn.setBackgroundResource(R.color.white);
        v_wcn.setBackgroundResource(R.color.white);
        v_clz.setBackgroundResource(R.color.white);
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
                map.put("isrec", isrec);
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
                list=new ArrayList<Map<String, String>>();
                JSONArray data=jobj.getJSONArray("data");
                for (int i=0;i<data.length();i++){
                    Map<String,String> map=new HashMap<String,String>();
                    JSONObject date=data.getJSONObject(i);
                    //处理状态
                    String isrec=date.getString("isrec");
                    map.put("isrec",isrec);
                    //id
                    String infoid=date.getString("infoid");
                    map.put("infoid",infoid);
                    //标题
                    String reportinfotype=date.getString("reportinfotype");
                    map.put("reportinfotype",reportinfotype);
                    //内容
                    String reportcontent=date.getString("reportcontent");
                    map.put("reportcontent",reportcontent);
                    //头像图片
                    String imgurl=date.getString("imgurl");
                    map.put("imgurl",imgurl);
                    list.add(map);
                }
                rel_progress.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
                adapter=new CheckPlanAdapter(this,list);
                lv.setAdapter(adapter);

            }else {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
