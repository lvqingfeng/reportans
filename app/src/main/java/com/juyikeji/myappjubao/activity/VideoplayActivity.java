package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;


import com.juyikeji.myappjubao.R;

import java.io.File;

/**
 * 视频预览
 * Created by Administrator on 2016/1/26 0026.
 */
public class VideoplayActivity extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_one_newpmsave_dialog);
        VideoView vv_video = (VideoView) findViewById(R.id.vv_video);
        Intent intent=this.getIntent();
        String path=intent.getStringExtra("video");
        playVideo(path, vv_video);
    }

    /**
     * 播放视频的方法
     */
    private void playVideo(String uri,VideoView vv_video) {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        vv_video.setLayoutParams(layoutParams);
        // 播放视频
        MediaController mediaco = new MediaController(this);
        File file = new File(uri);
        vv_video.setVideoPath(file.getAbsolutePath());
        vv_video.setMediaController(mediaco);
        mediaco.setMediaPlayer(vv_video);
        // 让VideiView获取焦点
        vv_video.start();
        vv_video.requestFocus();
    }
}
