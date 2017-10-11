package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.juyikeji.myappjubao.R;

/**
 * 关于我们
 */
public class AboutUs extends Activity implements View.OnClickListener {
    private ImageView iv_fanhui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        init();
    }

    /**
     * 实例化控件
     */
    private void init(){
        iv_fanhui= (ImageView) findViewById(R.id.iv_fanhui);
        iv_fanhui.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //返回
            case R.id.iv_fanhui:
                finish();
                break;

        }
    }
}
