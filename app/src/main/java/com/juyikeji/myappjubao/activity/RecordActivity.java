package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.utils.luyin.RecordImage;
import com.juyikeji.myappjubao.utils.luyin.RecordImp;
import com.juyikeji.myappjubao.utils.musicplay.Player;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/4/21 0021.
 */
public class RecordActivity extends Activity implements View.OnClickListener {

    private ImageView iv_close, iv_clear, iv_save, iv_luyin1;

    //录音
    private RecordImage iv_luyin;
    //录音名字
    private String fileName = "";

    //录音的时间
    private int time = 0;
    //音频路径
    private String record = "";
    //录音名
    private String recordname = "";


    //地址的集合，在没有回调之前，如果删除则删除本界面所有录音文件
    private List<String> listpath = new ArrayList<String>();

    //音乐播放停止按钮
    private ImageView iv_play;
    //播放音频进度条
    private SeekBar skbProgress;
    private Player player;
    //播放音频的按钮判断条件
    private boolean vv = false;
    //播放音频的按钮判断条件
    public int vplay = 0;

    @Override
    protected void onStart() {
        super.onStart();


    }

    /**
     * 加载布局
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_disclose_luyin);
        initView();
    }

    /**
     * 实例化控件
     */
    private void initView() {
        iv_luyin1 = (ImageView) findViewById(R.id.iv_luyin1);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);
        iv_luyin = (RecordImage) findViewById(R.id.iv_luyin);
        iv_luyin.setImage(iv_luyin1);
        iv_luyin.setAudioRecord(new AudioRecorder2());//调用录音的类
        iv_clear = (ImageView) findViewById(R.id.iv_clear);
        iv_clear.setOnClickListener(this);
        iv_save = (ImageView) findViewById(R.id.iv_save);
        iv_save.setOnClickListener(this);

        skbProgress = (SeekBar) findViewById(R.id.skbProgress);
        skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
        player = new Player(skbProgress);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv_play.setOnClickListener(this);

        //play播放完的监听
        player.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                iv_play.setBackgroundResource(R.mipmap.video_play);
                skbProgress.setProgress(100);//在播放完成时将seekbar设置到最大值
                vv = false;
            }
        });
    }

    /**
     * 页面监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                player.stop();
                finish();
                break;
            case R.id.iv_play:
                //播放
//                player.playPath(record);
                if ("".equals(record)) {
                    Toast.makeText(this, "没有录音", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (vv) {
                    iv_play.setBackgroundResource(R.mipmap.video_play);
                    vv = false;
                    player.pause();
                } else {
                    iv_play.setBackgroundResource(R.mipmap.video_stop);
                    vv = true;
                    vplay = iv_luyin.vplay;
                    if (vplay == 0) {
//                        player.playUrl("http://abv.cn/music/光辉岁月.mp3");
                        player.playPath(record);
                        vplay++;
                        iv_luyin.vplay = vplay;
                    } else {
                        player.play();
                    }
                }
                break;
            case R.id.iv_luyin:
                //录音

                break;
            case R.id.iv_clear:
                //清除
                if (!"".equals(record)) {
                    try {
                        for (int i = 0; i < listpath.size(); i++) {
                            File file = new File(listpath.get(i));
                            if (!file.exists()) {
                                return;
                            }
                            file.delete();
                            listpath.remove(i);
                        }
                        Clear();
                    } catch (Exception e) {
                        // TODO: handle exception
                        return;
                    }
                }

                break;
            case R.id.iv_save:
                //完成发送
                if ("".equals(record)) {
                    Toast.makeText(this, "没有录音", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //将最后一次录音保存，之前本界面录音全部删除
                    try {
                        for (int i = 0; i < listpath.size(); i++) {
                            File file = new File(listpath.get(i));
                            if (!file.exists()) {
                                return;
                            }
                            if (i != listpath.size() - 1) {
                                file.delete();//将最后一次录音保存，之前本界面录音全部删除
                                listpath.remove(i);
                            }
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra("path", record);
                    intent.putExtra("time", time);
                    intent.putExtra("pathname", recordname);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    private void Clear() {
        record = "";
        time = 0;
        recordname = "";
        vplay = 0;
        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
        skbProgress.setProgress(0);
    }

    /**
     * 录音的类
     *
     * @author Administrator
     */
    class AudioRecorder2 implements RecordImp {
        private MediaRecorder recorder;

        private String fileFolder = Environment.getExternalStorageDirectory()
                .getPath() + "/TestRecord";

        private boolean isRecording = false;

        @Override
        public void ready() {
            // TODO Auto-generated method stub
            File file = new File(fileFolder);
            if (!file.exists()) {
                file.mkdir();
            }
            fileName = getCurrentDate();
            recorder = new MediaRecorder();
            recorder.setOutputFile(fileFolder + "/" + fileName + ".mp3");
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置MediaRecorder的音频源为麦克风
            recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);// 设置MediaRecorder录制的音频格式
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 设置MediaRecorder录制音频的编码为amr

        }

        // 以当前时间作为文件名
        private String getCurrentDate() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
            Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
            String str = formatter.format(curDate);
            return str;
        }

        @Override
        public void start() {
            // TODO Auto-generated method stub
            if (!isRecording) {
                try {
                    recorder.prepare();
                    recorder.start();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                isRecording = true;
            }
        }

        @Override
        public void stop() {
            // TODO Auto-generated method stub
            if (isRecording) {
                recorder.stop();
                recorder.release();
                isRecording = false;
                //音频路径
                record = fileFolder + "/" + fileName + ".mp3";
                listpath.add(record);
                //将音频的时间设到控件上，显示出来
                time = (int) iv_luyin.recodeTime;//录音时间
                recordname = fileName + ".mp3";//录音名
//                SharedPreferences sharedPreferences=getSharedPreferences("luyin",MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("luyinpath", record);// 存储string类型
//                editor.putInt("luyintime", time);
//                editor.putString("luyinname",fileName + ".amr");
//                editor.commit();

//                try {
//                    String s2=encodeBase64File(s);
//                    Log.i("asd", s2);
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
            }
        }

        /**
         * 保存录音格式
         */
        @Override
        public void deleteOldFile() {
            // TODO Auto-generated method stub
            record = fileFolder + "/" + fileName + ".mp3";//录音格式
            File file = new File(record);
            file.deleteOnExit();
        }

        @Override
        public double getAmplitude() {
            // TODO Auto-generated method stub
            if (!isRecording) {
                return 0;
            }
            return recorder.getMaxAmplitude();
        }

    }


    //播放音频的seekbar
    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            this.progress = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            player.mediaPlayer.seekTo(progress);
        }
    }
}
