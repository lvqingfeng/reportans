package com.juyikeji.myappjubao.app;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.service.LocationService;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jyg on 2016/3/1.
 */
public class MyApplication extends Application {
    public LocationService locationService;
    public Vibrator mVibrator;

    public String s = "";//获取地理位置全名称
    public String ssy = "";//获取地理位置全名称

    private static Context instance;

    public LocationClient mLocationClient;//定位SDK的核心类
    public MyLocationListener mMyLocationListener;//定义监听类
    public TextView mLocationResult;
    public TextView sytextview;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);

/***
 * 初始化定位sdk，Application中创建
 */
        locationService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(
                Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());
//        instance = this;
//        //初始化推送
//        JPushInterface.setDebugMode(true);
//        JPushInterface.init(this);

        /**
         * 初始化异步图片加载
         */
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 获取Context对象
     *
     * @return
     */
    public static Context getInstance() {
        return instance;
    }

    /**
     * ImageLoader加载设置默认图片
     *
     * @return
     */
    public static DisplayImageOptions getDefaultOptions() {
        // 设置图片加载的属性
        DisplayImageOptions.Builder b = new DisplayImageOptions.Builder();
        b.showImageForEmptyUri(R.mipmap.no_pic_proc);
        b.showImageOnFail(R.mipmap.no_pic_proc);
        b.showImageOnLoading(R.mipmap.loading_proc);
        b.resetViewBeforeLoading(Boolean.TRUE);
        b.cacheOnDisk(Boolean.TRUE);
        b.cacheInMemory(Boolean.TRUE);
        b.imageScaleType(ImageScaleType.EXACTLY_STRETCHED);
        return b.bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
//            sb.append("time : ");
//            sb.append(location.getTime());//获得当前时间
//            sb.append("\nerror code : ");
//            sb.append(location.getLocType());//获得erro code得知定位现状
//            sb.append("\nlatitude : ");
//            sb.append(location.getLatitude());//获得纬度
//            sb.append("\nlontitude : ");
//            sb.append(location.getLongitude());//获得经度
//            sb.append("\nradius : ");
//            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {//通过GPS定位
//                sb.append("\nspeed : ");
//                sb.append(location.getSpeed());//获得速度
//                sb.append("\nsatellite : ");
//                sb.append(location.getSatelliteNumber());
//                sb.append("\ndirection : ");
//                sb.append("\naddr : ");
                String s = location.getAddrStr();
                s = s.substring(2);
                sb.append(s);//获得当前地址
                int sheng = s.indexOf("省");
                int shi = s.indexOf("市");
                ssy = s.substring(sheng + 1, shi + 1);

//                sb.append(location.getDirection());//获得方位
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {//通过网络连接定位
//                sb.append("\naddr : ");
                String s = location.getAddrStr();
                s = s.substring(2);
                sb.append(s);//获得当前地址

                int sheng = s.indexOf("省");
                int shi = s.indexOf("市");
                ssy = s.substring(sheng + 1, shi + 1);
//                //运营商信息
//                sb.append("\noperationers : ");
//                sb.append(location.getOperators());//获得经营商？
            }

            logMsg(sb.toString(),ssy);
            Log.i("BaiduLocationApiDem", sb.toString());
        }
    }

    /**
     * 显示请求字符串
     *
     * @param str
     */
    public void logMsg(String str,String stt) {
        try {
            if (mLocationResult != null)
                mLocationResult.setText(str);
            sytextview.setText(stt);
            mLocationClient.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
