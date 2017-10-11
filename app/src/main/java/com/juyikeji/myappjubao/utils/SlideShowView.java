package com.juyikeji.myappjubao.utils;

/**
 * Created by jyg on 2016/3/3 0003.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.activity.WebClass;
import com.juyikeji.myappjubao.app.MyApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/*
 *自定义view实现轮播效果
 */
public class SlideShowView extends FrameLayout {
    // 使用universal-image-loader插件读取网络图片，需要工程导入universal-image-loader-1.8.6-with-sources.jar
    private ImageLoader imageLoader;
    // 轮播图图片数量
    private final static int IMAGE_COUNT = 4;
    // 自动轮播的时间间隔
    private final static int TIME_INTERVAL = 5;
    // 自动轮播启用开关
    private final static boolean isAutoPlay = true;
    private boolean isCycle = false; // 是否循环

    // 自定义轮播图的资源
    private String[] imageUrls;
    // 放轮播图片的ImageView 的list
    private List<ImageView> imageViewsList;
    // 放圆点的View的list
    private List<View> dotViewsList;

    private ViewPager viewPager;
    // 当前轮播页
    private int currentItem = 0;
    // 定时任务
    private ScheduledExecutorService scheduledExecutorService;

    //跳转网页集合
    private String[] url;

    private Context context;
    String imgpath;
    private ACache mCache;
    String mc;
    // Handler
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentItem);
        }

    };

    public SlideShowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        initData();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//        if (isAutoPlay) {
//            startPlay();
//        }

    }

    public void setCycle(boolean isCycle) {
        this.isCycle = isCycle;
    }

    /**
     * 开始轮播图切换
     */
    public void startPlay() {
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4,
                TimeUnit.SECONDS);
    }


    /**
     * 初始化相关Data
     */
    private void initData() {
        imageViewsList = new ArrayList<ImageView>();
        dotViewsList = new ArrayList<View>();
        mCache = ACache.get(context);
        mc = mCache.getAsString("lbtu");
        // 异步任务获取图片
        new GetListTask().execute("");

    }

    /**
     * 初始化Views等UI
     */
    private void initUI(Context context) {
        if (imageUrls == null || imageUrls.length == 0)
            return;

        LayoutInflater.from(context).inflate(
                R.layout.activity_slideshow, this, true);

        LinearLayout dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
        dotLayout.removeAllViews();

        // 热点个数与图片特殊相等
        for (int i = 0; i < imageUrls.length; i++) {
            ImageView view = new ImageView(context);
            view.setTag(imageUrls[i]);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            imageViewsList.add(view);

            ImageView dotView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = 6;
            params.rightMargin = 6;
            dotLayout.addView(dotView, params);
            dotViewsList.add(dotView);
        }

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setFocusable(true);

        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
    }

    /**
     * 填充ViewPager的页面适配器
     */
    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(imageViewsList.get(position));
        }

        @Override
        public Object instantiateItem(View container, final int position) {
            ImageView imageView = imageViewsList.get(position);

            imageLoader.displayImage(imageView.getTag() + "", imageView, MyApplication.getDefaultOptions());

            ((ViewPager) container).addView(imageViewsList.get(position));

            //轮播图得点击监听
            imageViewsList.get(position).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent it = new Intent( Intent.ACTION_VIEW );
//                    it.setData( Uri.parse( "http://www.pingan.com/") ); //这里面是需要调转的rul
//                    it.setData(Uri.parse(url[position])); //这里面是需要调转的rul
//                    it = Intent.createChooser( it, null );//跳转系统自带浏览器
//                    context.startActivity( it );
                    Intent intent=new Intent(context, WebClass.class);
                    intent.putExtra("url","https://www.baidu.com/");
                    context.startActivity( intent );

                }
            });
            //轮播图滑动事件，手动滑动取消轮播
            imageViewsList.get(position).setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            scheduledExecutorService.shutdownNow();
                            break;
                        case MotionEvent.ACTION_MOVE:
//                            scheduledExecutorService.shutdown();//关闭轮播任务
                            scheduledExecutorService.shutdownNow();
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                    }
                    return true;
                }
            });
            return imageViewsList.get(position);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }


    }

    /**
     * ViewPager的监听器 当ViewPager中页面的状态发生改变时调用
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
                case 1:// 手势滑动，空闲中
                    isAutoPlay = false;
                    break;
                case 2:// 界面切换中
                    isAutoPlay = true;
                    break;
                case 0:// 滑动结束，即切换完毕或者加载完毕
                    // 当前为最后一张，此时从右向左滑，则切换到第一张
                    if (viewPager.getCurrentItem() == viewPager.getAdapter()
                            .getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    }
                    // 当前为第一张，此时从左向右滑，则切换到最后一张
                    else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        viewPager
                                .setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int pos) {

            currentItem = pos;
            for (int i = 0; i < dotViewsList.size(); i++) {
                if (i == pos) {
                    ((View) dotViewsList.get(pos))
                            .setBackgroundResource(R.mipmap.icon_point_pre);
                } else {
                    ((View) dotViewsList.get(i))
                            .setBackgroundResource(R.mipmap.icon_point);
                }
            }
        }

    }

    /**
     * 执行轮播图切换任务
     */
    private class SlideShowTask implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            synchronized (viewPager) {
                currentItem = (currentItem + 1) % imageViewsList.size();
                handler.obtainMessage().sendToTarget();
            }
        }

    }


    /**
     * 异步任务,获取数据
     */
    class GetListTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
//            imageUrls=new String[3];
//            imageUrls[0]="http://7xrfaf.com1.z0.glb.clouddn.com/banner2.jpg";
//            imageUrls[1]="http://7xrfaf.com1.z0.glb.clouddn.com/banner1.jpg";
//            imageUrls[2]="http://7xrfaf.com1.z0.glb.clouddn.com/banner.jpg";
            Map<String, String> map = new HashMap<String, String>();
            try {
//                if (mc == null) {
                String result = URLConnectionUtil.sendPostRequest(
                        "index", map, "utf-8", 1);
                mCache.put("lbtu", result);
                getDate(result);
//                } else {
//                    getDate(mc);
//                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        // 解析数据
        private void getDate(String json) {

            try {


                JSONObject jobj = new JSONObject(json);
                JSONArray data=jobj.getJSONArray("data");
                imageUrls = new String[data.length()];
                url=new String[data.length()];
                for (int i=0;i<data.length();i++){
                    JSONObject date=data.getJSONObject(i);
                    String item=date.getString("url");
                    imageUrls[i] = item;
                        url[i]=date.getString("link");
                }
//                String item = jobj.getString("url1");
//                imageUrls[0] = item;
//                String item1 = jobj.getString("url2");
//                imageUrls[1] = item1;
//                String item2 = jobj.getString("url3");
//                imageUrls[2] = item2;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                initUI(context);
            }
        }
    }

}

