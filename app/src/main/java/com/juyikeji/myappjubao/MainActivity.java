package com.juyikeji.myappjubao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.juyikeji.myappjubao.activity.BulletinBoardActivity;
import com.juyikeji.myappjubao.activity.BulletinBoarddetailsActivity;
import com.juyikeji.myappjubao.activity.CheckPlanActivity;
import com.juyikeji.myappjubao.activity.CityDingWeiActivity;
import com.juyikeji.myappjubao.activity.DiscloseActivity;
import com.juyikeji.myappjubao.activity.LoginActivity;
import com.juyikeji.myappjubao.activity.PersonalActivity;
import com.juyikeji.myappjubao.activity.WebClass;
import com.juyikeji.myappjubao.app.MyApplication;
import com.juyikeji.myappjubao.slip.PullToRefreshLayout;
import com.juyikeji.myappjubao.utils.SlideShowView;
import com.juyikeji.myappjubao.utils.URLConnectionUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 首页
 */
public class MainActivity extends Activity implements PullToRefreshLayout.OnRefreshListener {

    private SharedPreferences sharedPreferences;

    private GridView gv;
    private MainGvAdapter gvAdapter;

    private ListView lv;
    private MainLvAdapter lvAdapter;
    private List<Map<String, Object>> lvlist;

//    private SlideShowView ssview;
    private TextView tv_adress;
    private String name_space = "index/getinfo";
    private String result = "";

    private LocationClient mLocationClient;//定位SDK的核心类
    private MyApplication myApplication;
    private int dwbs=0;

    //下拉刷新
    private PullToRefreshLayout refresh_view;
    private int sXbs = 1;

    //获取登录信息
    private boolean login = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
//        ssview = (SlideShowView) findViewById(R.id.ssview);
        sharedPreferences = getSharedPreferences("reportans", MODE_PRIVATE);
        timerTask();
        lvlist = new ArrayList<Map<String, Object>>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sXbs = 1;//刷新标识
        init();
        /**
         * 定位
         */
        if (dwbs==0) {
            myApplication = (MyApplication) getApplication();
            mLocationClient = myApplication.mLocationClient;
            InitLocation();//初始化
            ((MyApplication) getApplication()).sytextview = tv_adress;//调用LocationApplication，获得需要的信息
            mLocationClient.start();
            dwbs++;
        }

    }

    /**
     * 控件加载完成的方法
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
//            ssview.startPlay();//启动轮播
            //获取登录信息
            login = sharedPreferences.getBoolean("login", false);
            sXbs = 1;//初始化下拉刷新标志
            showLotter(sXbs);
//            if (URLConnectionUtil.isOpenNetwork(this)) {
//                if (URLConnectionUtil.isCacheDataFailure("info")) {
//
//                } else {
//                    result = (String) URLConnectionUtil.readObject("info");//读取SD卡缓存
//                    if (result != null) {
//                        getRegister(result);
//                    }
//                }
//            } else {
//                result = (String) URLConnectionUtil.readObject("info");//读取SD卡缓存
//                if (result != null) {
//                    getRegister(result);
//                }
//
//            }
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, BulletinBoarddetailsActivity.class);
                    intent.putExtra("infoid", lvlist.get(position).get("infoid").toString());
                    startActivity(intent);

                }
            });
        }
    }

    /**
     * 实例化控件
     */
    private void init() {
        refresh_view = (PullToRefreshLayout) findViewById(R.id.refresh_view);
        refresh_view.setOnRefreshListener(this);
        tv_adress = (TextView) findViewById(R.id.tv_adress);
        tv_adress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationClient.stop();
                Intent intent = new Intent(MainActivity.this, CityDingWeiActivity.class);
                intent.putExtra("city", tv_adress.getText().toString());
                startActivityForResult(intent, 1);
            }
        });
        gv = (GridView) findViewById(R.id.gv);
        gvAdapter = new MainGvAdapter(this);
        gv.setAdapter(gvAdapter);

        lv = (ListView) findViewById(R.id.lv);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (login) {
                    switch (position) {
                        case 0:
                            startActivity(new Intent(MainActivity.this, DiscloseActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(MainActivity.this, CheckPlanActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(MainActivity.this, BulletinBoardActivity.class));
                            break;
                        case 3:
                            startActivity(new Intent(MainActivity.this, PersonalActivity.class));
                            break;
                    }
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }


            }
        });

    }

    /**
     * 初始化定位
     */
    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置高精度定位定位模式
        option.setCoorType("bd09ll");//设置百度经纬度坐标系格式
        option.setScanSpan(1000);//设置发起定位请求的间隔时间为1000ms
        option.setIsNeedAddress(true);//反编译获得具体位置，只有网络定位才可以
        mLocationClient.setLocOption(option);
    }

    // 城市定位回传值
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            String s = data.getStringExtra("i");
            tv_adress.setText(s );
            return;
        }
//        if (requestCode == 1 && resultCode == 3) {
//            String s = data.getStringExtra("city");
//            tv_adress.setText(s + "市");
//        }
    }

    /**
     * 请求网络注册
     */
    private void showLotter(final int sxbs) {
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
                map.put("pagenumber", sxbs + "");
                map.put("pagesize", "3");
                // 请求网络
                try {
                    result = URLConnectionUtil.sendPostRequest(name_space,
                            map, "UTF-8", 1);
                    URLConnectionUtil.saveObject(result, "info");//保存到SD卡
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
    String totalRow="";
    private boolean bool=true;
    private void getRegister(String json) {
        Log.i("mainactivity",json);
        try {
            JSONObject jobj = new JSONObject(json);
            String status = jobj.getString("status");
            String msg = jobj.getString("msg");
            if (status.equals("1")) {
                JSONObject jsonObject = jobj.getJSONObject("data");
                totalRow=jsonObject.getString("totalRow");
                boolean lastpage=jsonObject.getBoolean("lastPage");
                if (bool) {
                    JSONArray data = jsonObject.getJSONArray("list");
                    for (int i = 0; i < data.length(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        JSONObject date = data.getJSONObject(i);
                        //id
                        String infoid = date.getString("infoid");
                        map.put("infoid", infoid);
                        //创建时间
                        String crttime = date.getString("crttime");
                        map.put("crttime", crttime);
                        //标题
                        String title = date.getString("title");
                        map.put("title", title);
                        //内容
                        String content = date.getString("content");
                        map.put("content", content);
                        //图片
                        List<String> imlist = new ArrayList<String>();
                        if (date.length() > 4) {
                            JSONObject imglist = date.getJSONObject("imglist");
                            for (int j = 1; j <= imglist.length(); j++) {
                                String img = imglist.getString("img" + j);
                                imlist.add(img);
                            }
                        }
                        map.put("imlist", imlist);
                        lvlist.add(map);
                    }
                    lvAdapter = new MainLvAdapter(this, lvlist);
                    lv.setAdapter(lvAdapter);
                    sXbs++;
                }
                if (lastpage){
                    bool=false;
                }

            } else {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
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
                    showLotter(sXbs);
                }

            }
        }.sendEmptyMessageDelayed(0, 1000);

    }

    // 加载更多操作
    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
        showLotter(sXbs);
        new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (totalRow.equals(lvlist.size()+"")){
                    Toast.makeText(MainActivity.this,"没有更多内容",Toast.LENGTH_SHORT).show();
                }
            }
        }.sendEmptyMessageDelayed(0, 1500);
    }

    private int count=1;
    /**
     * 消息处理器的应用
     */
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    dwbs++;
                    mLocationClient.stop();
                    mTimer.cancel();//
                    mTimer=null;
                    Log.i("adab",dwbs+"");
            }
            super.handleMessage(msg);
        }
    };

    public Timer mTimer = new Timer();// 定时器


    public void timerTask() {
        //创建定时线程执行更新任务
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!"".equals(tv_adress.getText().toString().trim())){
                    mHandler.sendEmptyMessage(2);// 向Handler发送消息停止继续执行
                    Log.i("abab", "adfasfdsf");
                }
                count++;
            }
        }, 1000, 1000);// 定时任务
    }
    /**
     * 销毁定时器的方式
     */
    @Override
    protected void onStop() {
        if (mTimer!=null) {
            mTimer.cancel();// 程序退出时cancel timer
        }
        super.onStop();
    }

}
