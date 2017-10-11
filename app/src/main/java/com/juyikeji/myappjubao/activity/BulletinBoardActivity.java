package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.adapter.BulletinBoardAdapter;
import com.juyikeji.myappjubao.adapter.CheckPlanAdapter;
import com.juyikeji.myappjubao.slip.PullToRefreshLayout;
import com.juyikeji.myappjubao.slip.PullableListView;
import com.juyikeji.myappjubao.utils.URLConnectionUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告栏
 * Created by Administrator on 2016/4/5 0005.
 */
public class BulletinBoardActivity extends Activity implements PullToRefreshLayout.OnRefreshListener{

    private ImageView iv_close;
    private PullToRefreshLayout pull_layout;
    private PullableListView lv;
    private List<Map<String,String>> lvlist;
    private BulletinBoardAdapter adapter;
    private SharedPreferences sharedPreferences;

    private int bs=1;

    /**
     * 公告栏接口
     */
    private String name_space="notice";
    private String result="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bulletinboard);
        sharedPreferences=getSharedPreferences("reportans",MODE_PRIVATE);
        pull_layout=(PullToRefreshLayout)findViewById(R.id.pull_layout);
        pull_layout.setOnRefreshListener(this);
        lv=(PullableListView)findViewById(R.id.lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(BulletinBoardActivity.this,BulletinBoarddetailsActivity.class);
                intent.putExtra("infoid",lvlist.get(position).get("infoid"));
                startActivity(intent);
            }
        });
        iv_close=(ImageView)findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        bs=1;
        showLotter(bs);
        lvlist=new ArrayList<Map<String, String>>();
    }
    /**
     * 请求网络注册
     */
    private void showLotter(final  int isrec) {
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
                map.put("pagenumber", isrec+"");
                map.put("pagesize", "20");
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

    private boolean bool=true;
    private void getRegister(String json) {
        try {
            JSONObject jobj = new JSONObject(json);
            String status = jobj.getString("status");
            String msg = jobj.getString("msg");
            if (status.equals("1")) {
                JSONObject data=jobj.getJSONObject("data");
                boolean lastpage=data.getBoolean("lastPage");
                if (bool) {
                    bs++;
                }
                if (lastpage){
                    bool=false;
                }
                JSONArray list=data.getJSONArray("list");
                for (int i=0;i<list.length();i++){
                    Map<String,String> map=new HashMap<String,String>();
                    JSONObject date=list.getJSONObject(i);
                    //id
                    String infoid=date.getString("infoid");
                    map.put("infoid",infoid);
                    //时间
                    String crttime=date.getString("crttime");
                    map.put("crttime",crttime);
                    //标题
                    String title=date.getString("title");
                    map.put("title",title);
                    //内容
                    String content=date.getString("content");
                    map.put("content",content);
                    //头像图片
                    String picurl=date.getString("picurl");
                    map.put("picurl",picurl);
                    lvlist.add(map);
                }
                adapter=new BulletinBoardAdapter(this,lvlist);
                lv.setAdapter(adapter);
            }else {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 下拉刷新操作
    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {

        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (!isOpenNetwork()) {
                    //刷新失败
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                } else {
                    //刷新成功
                    pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                    showLotter(bs);
                }

            }
        }.sendEmptyMessageDelayed(0, 1000);

    }

    // 加载更多操作
    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {

        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                showLotter(bs);
            }
        }.sendEmptyMessageDelayed(0, 1000);

    }
    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

}
