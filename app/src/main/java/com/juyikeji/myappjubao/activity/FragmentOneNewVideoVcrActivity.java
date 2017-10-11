package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;


import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.utils.luxiang.MovieRecorderView;
import com.juyikeji.myappjubao.utils.luxiang.Utils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 录制视频
 * Created by Administrator on 2016/1/25 0025.
 */
public class FragmentOneNewVideoVcrActivity extends Activity{
    SharedPreferences sharedPreferences;
    private MovieRecorderView mRecorderView;
    private Button mShootBtn;
    private boolean isFinish = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_one_newvideo_vcr);
        sharedPreferences=this.getSharedPreferences("image",
                MODE_PRIVATE);

        mRecorderView = (MovieRecorderView) findViewById(R.id.movieRecorderView);
        mShootBtn = (Button) findViewById(R.id.shoot_button);
        showVRC();
    }

    /**
     * 按着拍
     */
    private void showVRC(){
        //按住录制视频
        mShootBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mRecorderView.record(new MovieRecorderView.OnRecordFinishListener() {

                        @Override
                        public void onRecordFinish() {
                            handler.sendEmptyMessage(1);
                        }
                    });
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (mRecorderView.getTimeCount() > 3) {//录制时间不能小于4秒
                        handler.sendEmptyMessage(1);
                    } else {
                        if (mRecorderView.getmRecordFile() != null) {
                            mRecorderView.getmRecordFile().delete();
                        }
                        mRecorderView.stop();
                        Toast.makeText(FragmentOneNewVideoVcrActivity.this, "视频录制时间太短", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        isFinish = true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isFinish = false;
        mRecorderView.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            finishActivity();
        }
    };

    private void finishActivity() {
        if (isFinish) {
            mRecorderView.stop();
            // 按住拍文件路径
            String path = "";
            path = mRecorderView.getmRecordFile().getAbsolutePath();
            // 通过路径获取第一帧的缩略图并显示
            Bitmap bitmap = Utils.createVideoThumbnail(path);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, 550, 800);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("video",path);
            editor.putString("videoname",path+".mp4");
            editor.putString("image",Bitmap2Base64(bitmap));
            editor.commit();
            // 跳转到发送页面
            Intent intent = new Intent(this,FragmentOneNewPmSaveActivity.class);
            startActivity(intent);
        }
        // isFinish = false;
        finish();
    }

    /**
     * bitmap转base64
     */
    public String Bitmap2Base64(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        // base64 encode
        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
        String encodeString = new String(encode);
        return encodeString;
    }

    /**
     * 获取联系人页面数据
     */
    private Map<String ,String > getMessage(){
        Map<String ,String > map=new HashMap<String, String>();
        Intent intent=this.getIntent();
        map.put("attn", intent.getStringExtra("attn"));
        map.put("uid", intent.getStringExtra("uid"));
        map.put("uname", intent.getStringExtra("uname"));
        return map;
    }
}
