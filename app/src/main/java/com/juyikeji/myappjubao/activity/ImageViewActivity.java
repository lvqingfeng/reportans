package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.utils.imagecache.MyScrollInterface;
import com.juyikeji.myappjubao.utils.imagecache.ThreeGroup;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jyg on 2016/4/7 0007.
 * 图片放大
 */
public class ImageViewActivity extends Activity {

    ImageLoader loader=ImageLoader.getInstance();
    private ThreeGroup iv;
    private TextView tv1,tv2;

    private Intent intent;
    private String[] s;
    private int size;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_imageview_big);
        intent=getIntent();
        position=intent.getIntExtra("position", 0)+1;
        size=intent.getIntExtra("imgsize", 0);
        s=new String[size];
        for (int i=0;i<size;i++){
            s[i]=intent.getStringExtra("img"+i);
        }
        tv1=(TextView)findViewById(R.id.tv1);
        tv2=(TextView)findViewById(R.id.tv2);
        tv1.setText(position+"");
        tv2.setText("/" + size);

        RelativeLayout rel=(RelativeLayout)findViewById(R.id.rel);
        iv=(ThreeGroup)findViewById(R.id.iv);
        for (int i=0;i<size;i++){
            iv.addView(getimageView().get(i));
        }
        iv.setListener(new MyScrollInterface() {
            boolean move_direction;

            @Override
            public void scrollStart(boolean direction) {
                //根据滑动方向修改返回值为true为往右滑动反之往左滑动
                move_direction = direction;
                if (direction) {
                    if (position == size) {
                        position = 0;
                    }
                    position++;
                } else {
                    if (position == 1) {
                        position = size + 1;
                    }
                    position--;
                }
                tv1.setText(position + "");
            }

            @Override
            public void scrollEnd() {
                if (move_direction) {
                    View view = iv.getChildAt(0);
                    iv.removeViewAt(0);
                    iv.addView(view, 2);
                } else {
                    View view = iv.getChildAt(2);
                    iv.removeViewAt(2);
                    iv.addView(view, 0);
                }
            }
        });
    }

    private List<ImageView> getimageView(){
        List<ImageView> list=new ArrayList<ImageView>();
        for (int i=0;i<size;i++){
            ImageView v=new ImageView(this);
            v.setScaleType(ImageView.ScaleType.FIT_XY);
            v.setLayoutParams(new LinearLayout.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
            loader.displayImage(s[i], v);
            //设置gridview不能获取焦点
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(ImageViewActivity.this, "dddddd", Toast.LENGTH_SHORT).show();
//                }
//            });
            list.add(v);
        }

        return list;
    }

}
