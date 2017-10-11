package com.juyikeji.myappjubao.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.adapter.CityDingWeiAdapter;
import com.juyikeji.myappjubao.app.MyApplication;
import com.juyikeji.myappjubao.service.LocationService;

/**
 * 首页城市定位功能页面
 * @ClassName CityDingWeiActivity
 * @Description: TODO (城市定位)
 * @author liuhuicheng
 * @date 2015-11-30 上午10:13:47
 *
 */
public class CityDingWeiActivity extends Activity implements OnClickListener {
    private Button bt_back;
    private CityDingWeiAdapter adapter;
    private GridView gv;
    private Button bt_dangqiancity, bt_dingweicity;
    private LocationService locationService;
    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_citydingwei);
        init();
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent();
                intent.putExtra("i", list.get(arg2));
                setResult(2, intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        locationService = ((MyApplication) getApplication()).locationService;
        locationService.registerListener(mListener);
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService
                    .getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();// 定位SDK
        Intent i=getIntent();
        String city=i.getStringExtra("city");
        bt_dangqiancity.setText(city);
    }

    @Override
    protected void onStop() {
        locationService.unregisterListener(mListener); // 注销掉监听
        locationService.stop(); // 停止定位服务
        super.onStop();
    }

    private void init() {
        bt_back = (Button) findViewById(R.id.bt_back);
        bt_dangqiancity = (Button) findViewById(R.id.bt_dangqiancity);
        bt_dingweicity = (Button) findViewById(R.id.bt_dingweicity);// 城市定位
        gv = (GridView) findViewById(R.id.gv);
        adapter = new CityDingWeiAdapter(this, getData());
        gv.setAdapter(adapter);
        bt_back.setOnClickListener(this);
        bt_dangqiancity.setOnClickListener(this);
//        bt_dingweicity.setOnClickListener(this);
    }

    private List<String> getData() {
        list = new ArrayList<String>();
        list.add("鹿城区");
        list.add("瓯海区");
        list.add("龙湾区");
        list.add("开发区");
        list.add("乐清市");
        list.add("永嘉县");
        list.add("瑞安市");
        list.add("平阳县");
        list.add("苍南县");
        list.add("洞头县");
        list.add("文成县");
        list.add("泰顺县");
        return list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_back:
                finish();
                break;
//            case R.id.bt_dingweicity:
//                if(isOpenNetwork()){
//                    Intent intent = new Intent();
//                    intent.putExtra("city", bt_dingweicity.getText());
//                    setResult(3, intent);
//                    finish();
//                }
//
//                break;
        }

    }

    public void logMsg(String str) {
        try {
            if (bt_dingweicity != null)
                bt_dingweicity.setText(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {

            if (isOpenNetwork()) {
                if (null != location
                        && location.getLocType() != BDLocation.TypeServerError) {
                    String city = location.getCity();
                    city = city.substring(0, city.length() - 1);
                    logMsg(city);
                }
            } else {
                Toast.makeText(getApplicationContext(), "网络不可用，定位失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }
}
