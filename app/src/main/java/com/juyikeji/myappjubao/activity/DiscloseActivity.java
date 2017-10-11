package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.adapter.DiscloseGridviewAdapter;
import com.juyikeji.myappjubao.adapter.DiscloseGvluxiangAdapter;
import com.juyikeji.myappjubao.adapter.DiscloseGvluyinAdapter;
import com.juyikeji.myappjubao.app.MyApplication;
import com.juyikeji.myappjubao.utils.DateTimePickDialogUtil;
import com.juyikeji.myappjubao.utils.SharedPreferencesUtil;
import com.juyikeji.myappjubao.utils.URLConnectionUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/2 0002.
 */
public class DiscloseActivity extends Activity implements View.OnClickListener, DiscloseGvluyinAdapter.Callback {
    //返回
    private ImageView iv_fanhui, iv_address, iv_time, iv_riqi, iv_luyin ,iv_luxiang;
    //日期
    private EditText et_bt, et_ms;
    private TextView et_riqi, tv_wz, tv_time;
    private RelativeLayout rl_address;
    //上传
    private Button bt_sc;
    //图片gridview
    private GridView gv_image;
    //图片数据源
    private List<Bitmap> list;
    //图片adapter
    DiscloseGridviewAdapter adapter;

    //录音展示的gridview；
    private GridView gv_luyin;
    //适配器
    private DiscloseGvluyinAdapter lygvadapter;
    //容器
    private List<Map<String, Object>> pathlist = new ArrayList<Map<String, Object>>();//录音的地址

    //录像展示的gridview；
    private GridView gv_luxiang;
    //适配器
    private DiscloseGvluxiangAdapter lxgvadapter;
    //容器
    private List<Map<String,Object>> lxlist=new ArrayList<Map<String, Object>>();


    private static final String url = "publish/publishinfo";
    private String result = "";

    private CheckBox cb_nm, cb_sm;
    private int cb = 1;

    private boolean dw = false;//定位按钮标识;
    //匿名
    private static final String url2 = "isall";
    private String result2 = "";
    private int isall;

    private String title = "";
    private String base = "";
    private String ms = "";
    private String sj = "";
    private String add = "";

    //拆开时间年月日选择
    Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    int hour = c.get(Calendar.HOUR_OF_DAY);
    int minute = c.get(Calendar.MINUTE);

    /**
     * 定义从相机或者相册 0是相机 1相册2剪辑后的图片
     */
    protected static final String IMAGE_FILE_NAME = "image.jsp";
    protected static final int REQUESTCODE_TAKE = 0;
    protected static final int REQUESTCODE_PICK = 1;
    protected static final int REQUESTCODE_CUTTING = 2;
    private final int LUYIN = 4;//录音界面

    private LocationClient mLocationClient;//定位SDK的核心类


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclose);

        mLocationClient = ((MyApplication) getApplication()).mLocationClient;
        InitLocation();//初始化
        init();
    }

    /**
     * 实例化控件并设置监听事件
     */
    private void init() {

        gv_luyin = (GridView) findViewById(R.id.gv_luyin);//录音的gridview
        iv_luxiang=(ImageView)findViewById(R.id.iv_luxiang);//录像的按钮
        iv_luxiang.setOnClickListener(this);
        gv_luxiang = (GridView) findViewById(R.id.gv_luxiang);//录像的gridview

        tv_wz = (TextView) findViewById(R.id.tv_wz);
        iv_address = (ImageView) findViewById(R.id.iv_address);
        iv_luyin = (ImageView) findViewById(R.id.iv_luyin);
        iv_luyin.setOnClickListener(this);
        //日期
        et_riqi = (TextView) findViewById(R.id.et_riqi);
        et_riqi.setOnClickListener(this);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        if (hour < 10 && minute < 10) {
            tv_time.setText("0" + hour + ":" + "0" + minute);
        } else if (hour < 10 && minute > 10) {
            tv_time.setText("0" + hour + ":" + minute);
        } else if (hour > 10 && minute < 10) {
            tv_time.setText("0" + hour + ":" + "0" + minute);
        } else {
            tv_time.setText(hour + ":" + minute);
        }

        iv_time = (ImageView) findViewById(R.id.iv_time);
        iv_time.setOnClickListener(this);
        iv_riqi = (ImageView) findViewById(R.id.iv_time);
        iv_riqi.setOnClickListener(this);


        et_bt = (EditText) findViewById(R.id.et_bt);
        et_ms = (EditText) findViewById(R.id.et_ms);
        //上传
        bt_sc = (Button) findViewById(R.id.bt_sc);
        rl_address = (RelativeLayout) findViewById(R.id.rl_address);
        rl_address.setOnClickListener(this);
        bt_sc.setOnClickListener(this);
        //返回
        iv_fanhui = (ImageView) findViewById(R.id.iv_fanhui);
        iv_fanhui.setOnClickListener(this);
        //图片gridview
        gv_image = (GridView) findViewById(R.id.gv_image);
        list = new ArrayList<Bitmap>();
        adapter = new DiscloseGridviewAdapter(DiscloseActivity.this, list);
        gv_image.setAdapter(adapter);
        gv_image.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == list.size()) {
                    showTouxiangDialog();
                }
            }
        });
        initcheckbox();
        cb_sm = (CheckBox) findViewById(R.id.cb_sm);
        cb_sm.setChecked(true);
        cb_sm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_sm.isChecked()) {
                    cb_nm.setChecked(false);
                } else {
                    cb_nm.setChecked(true);
                }
            }
        });
    }

    /**
     * 复选框选中取消事件
     */
    private void initcheckbox() {
        cb_nm = (CheckBox) findViewById(R.id.cb_nm);

        //取消监听器
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!((CheckBox) v).isChecked()) {
                    cb = 1;
                    cb_sm.setChecked(true);
                }
            }
        };
        //选中监听器
        CompoundButton.OnCheckedChangeListener occl = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    cb = 0;
                    cb_sm.setChecked(false);
                }
            }
        };
        //取消监听
        cb_nm.setOnClickListener(ocl);
        //选中监听
        cb_nm.setOnCheckedChangeListener(occl);
    }

    @Override
    public void onClick(View v) {
        DateTimePickDialogUtil dateTimePicKDialog;
        switch (v.getId()) {
            case R.id.iv_luxiang:

                break;
            case R.id.iv_luyin:
                //录音按钮
                Intent intent = new Intent(this, RecordActivity.class);
                startActivityForResult(intent, LUYIN);
                break;
            //返回
            case R.id.iv_fanhui:
                finish();
                break;
            //日期
            case R.id.et_riqi:
                dateTimePicKDialog = new DateTimePickDialogUtil(
                        DiscloseActivity.this);
                dateTimePicKDialog.dateTimePicKDialog(et_riqi);
                break;
            case R.id.iv_riqi:
                dateTimePicKDialog = new DateTimePickDialogUtil(
                        DiscloseActivity.this);
                dateTimePicKDialog.dateTimePicKDialog(et_riqi);
                break;
            //日期精确到分钟
            case R.id.tv_time:
                showTime();
                break;
            case R.id.iv_time:
                showTime();
                break;
            //上传
            case R.id.bt_sc:
                if (cb_sm.isChecked()) {//实名
                    request2();
                } else {//匿名
                    title = et_bt.getText().toString().trim();
                    sj = et_riqi.getText().toString().trim() + " " + tv_time.getText().toString().trim() + ":00";
                    ms = et_ms.getText().toString().trim();
                    base = "";
                    for (int i = 0; i < list.size(); i++) {
                        base = base + BitmapToBase64(list.get(i)) + "-";
                    }
                    if (!"".equals(base)) {
                        base = base.substring(0, base.length() - 1);
                    }
                    add = tv_wz.getText().toString().trim();
                    if ("显示位置".equals(add)) {
                        add = "";
                        Log.i("address222222222222222", add + 1);
                    }
                    if ("".equals(title)) {
                        Toast.makeText(DiscloseActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
                    } else if ("".equals(ms)) {
                        Toast.makeText(DiscloseActivity.this, "请输入描述内容", Toast.LENGTH_SHORT).show();
                    } else {
                        upLoading(title, sj, ms, base, add);//请求网络提交信息
//                        request(title, sj, ms, base, add);
                    }
                }
                break;
            //获取当前位置
            case R.id.rl_address:
                if (dw) {
                    dw = false;
                    mLocationClient.stop();
                    tv_wz.setText("显示位置");
                } else {
                    ((MyApplication) getApplication()).mLocationResult = tv_wz;//调用LocationApplication，获得需要的信息
                    mLocationClient.start();
                    dw = true;
                }

                break;
        }
    }

    /**
     * 时间控件
     */
    private void showTime() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();
        // 新建一个空edittext让弹出界面出现软键盘，在show方法之前
//        dialog.setView(new EditText(getActivity()));

        dialog.show();
        Window window = dialog.getWindow();
        // 设置布局
        window.setContentView(R.layout.activity_disclose_timedialog);
        // 设置宽高
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置弹出的动画效果
        window.setWindowAnimations(R.style.AnimBottom);// 动画效果

        View view = (View) window.findViewById(R.id.view);
        TextView tv_no = (TextView) window.findViewById(R.id.tv_savedialogno);
        TextView tv_yes = (TextView) window.findViewById(R.id.tv_savedialogyes);
        //实例化事件控件
        TimePicker tp_time = (TimePicker) window.findViewById(R.id.tp_time);
        //设置为24小时制显示时间
        tp_time.setIs24HourView(true);
        String s = tv_time.getText().toString();
        //小时
        int h = Integer.parseInt(s.substring(0, s.indexOf(":")));
        // 分钟
        String d = s.substring(s.indexOf(":") + 1);
        int m = Integer.parseInt(d.substring(0));
        tp_time.setCurrentHour(h);
        tp_time.setCurrentMinute(m);
        //监听事件改变的方法
        tp_time.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minite) {
                //事件改变的处理
                hour = hourOfDay;
                minute = minite;
            }
        });
        //取消
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //确定
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String h, m;
                if (hour < 10) {
                    h = "0" + hour;
                } else {
                    h = hour + "";
                }
                if (minute < 10) {
                    m = "0" + minute;
                } else {
                    m = minute + "";
                }
                tv_time.setText(h + ":" + m);
                dialog.dismiss();
            }
        });

    }

    /**
     * 初始化
     */

    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置高精度定位定位模式
        option.setCoorType("bd09ll");//设置百度经纬度坐标系格式
        option.setScanSpan(1000);//设置发起定位请求的间隔时间为1000ms
        option.setIsNeedAddress(true);//反编译获得具体位置，只有网络定位才可以
        mLocationClient.setLocOption(option);
    }

//    @Override
//    protected void onStop() {
//        mLocationClient.stop();
//        super.onStop();
//    }

    /**
     * 图片转base64
     *
     * @param bitmap
     * @return
     */
    public String BitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        // base64 encode
        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
        String encodeString = new String(encode);
        return encodeString;
    }

    /**
     * 请求服务器的方法
     */
    private void request(final String title, final String sj, final String ms,
                         final String base, final String add) {
        final String token = SharedPreferencesUtil.getSharedPreferences(DiscloseActivity.this).get("token").toString();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    JSONObject jobj = null;
                    try {
                        jobj = new JSONObject(result);
                        String status = jobj.getString("status");
                        if ("1".equals(status)) {
                            finish();
                        } else {
                            Toast.makeText(DiscloseActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                super.run();
                Map<String, String> map = new HashMap<String, String>();
                map.put("token", token);
                map.put("reporttitle", title);
                map.put("reporttime", sj);
                map.put("reportcontent", ms);
                map.put("imgbase", base);
                map.put("address", add);
                map.put("audiobase",Yinpin());
                map.put("suffix",".amr");
                if (cb_sm.isChecked()) {
                    map.put("reporttype", 1 + "");
                    Log.i("cb", 1 + "");
                } else {
                    map.put("reporttype", 0 + "");
                    Log.i("cb", 0 + "");
                }
//                Log.i("cb", cb + "");
                Log.i("DiscloseActivity", map + "");
                try {
                    result = URLConnectionUtil.sendPostRequest(url, map, "utf-8", 1);
                    Log.i("DiscloseActivity", result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message m = new Message();
                m.what = 1;
                // 发送消息到Handler
                handler.sendMessage(m);
            }
        }.start();
    }

    /**
     * 请求服务器的方法
     */

    private void upLoading(final String title, final String sj, final String ms,
                           final String base, final String add){
        RequestParams params = new RequestParams();
//        params.addHeader("name", "value");
//        params.addQueryStringParameter("name", "value");

        // 只包含字符串参数时默认使用BodyParamsEntity，
        // 类似于UrlEncodedFormEntity（"application/x-www-form-urlencoded"）。
//        params.addBodyParameter("name", "value");

        // 加入文件参数后默认使用MultipartEntity（"multipart/form-data"），
        // 如需"multipart/related"，xUtils中提供的MultipartEntity支持设置subType为"related"。
        // 使用params.setBodyEntity(httpEntity)可设置更多类型的HttpEntity（如：
        // MultipartEntity,BodyParamsEntity,FileUploadEntity,InputStreamUploadEntity,StringEntity）。
        // 例如发送json参数：params.setBodyEntity(new StringEntity(jsonStr,charset));
//        params.addBodyParameter("file", new File("path"));
        final String token = SharedPreferencesUtil.getSharedPreferences(DiscloseActivity.this).get("token").toString();
        params.addBodyParameter("token", token);
        params.addBodyParameter("reporttitle", title);
        params.addBodyParameter("reporttime", sj);
        params.addBodyParameter("reportcontent", ms);
        params.addBodyParameter("imgbase", base);
        params.addBodyParameter("address", add);
        params.addBodyParameter("audiobase",Yinpin());
        params.addBodyParameter("suffix",".amr");
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                "http://122.114.53.137/reportservice/publish/publishinfo",
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        //开始上传
                        Toast.makeText(DiscloseActivity.this,"conn...",Toast.LENGTH_SHORT).show();
//                        testTextView.setText("conn...");
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        //上传中
                        if (isUploading) {
                            Toast.makeText(DiscloseActivity.this,"发送中",Toast.LENGTH_SHORT).show();
//                            Toast.makeText(DiscloseActivity.this,"upload: " + current + "/" + total,Toast.LENGTH_SHORT).show();
//                            testTextView.setText("upload: " + current + "/" + total);
                        } else {
                            Toast.makeText(DiscloseActivity.this,"发送中",Toast.LENGTH_SHORT).show();
//                            Toast.makeText(DiscloseActivity.this,"reply: " + current + "/" + total,Toast.LENGTH_SHORT).show();
//                            testTextView.setText("reply: " + current + "/" + total);
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        //上传成功
                        Toast.makeText(DiscloseActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
//                        Toast.makeText(DiscloseActivity.this,"reply: " + responseInfo.result,Toast.LENGTH_SHORT).show();
//                        testTextView.setText("reply: " + responseInfo.result);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        //上传失败
                        Toast.makeText(DiscloseActivity.this,error.getExceptionCode() + ":" + msg,Toast.LENGTH_SHORT).show();
//                        testTextView.setText(error.getExceptionCode() + ":" + msg);
                    }
                });
    }

    /**
     * 根据音频的路径获取文件并转baze64字符串
     * @return
     */
    private String Yinpin(){
        String paths="";
        for (int i=0;i<pathlist.size();i++){
            try {
                if (i!=pathlist.size()-1) {
                    paths = paths + encodeBase64File(pathlist.get(i).get("path").toString()) + "-";
                }else {
                    paths = paths + encodeBase64File(pathlist.get(i).get("path").toString());
                }
            }catch ( Exception e){
                e.printStackTrace();
            }
        }
        return paths;
    }

    /**
     * 音频文件转baze64
     */
    public String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }
    /**
     * 头像选择弹出框
     */
    public void showTouxiangDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(DiscloseActivity.this).create();
        dialog.show();
        Window window = dialog.getWindow();
        // 设置布局
        window.setContentView(R.layout.activity_touxiangdialog);
        // 设置宽高
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置弹出的动画效果
//        window.setWindowAnimations(R.style.AnimBottom);// 动画效果
        View view = (View) window.findViewById(R.id.view);
        final TextView tv_photo = (TextView) window.findViewById(R.id.tv_photo);
        final TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_xiangce);
        TextView tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建Intent 来调用相机
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //获取照片路径
                String imagePath = Environment.getExternalStorageDirectory()
                        + "";
                // 设置文件路径
                File file = new File(imagePath);
                // 判断文件夹是否存在，不存在创建一个
                if (!file.exists()) {
                    file.mkdirs();
                }
                // 设置URI，指定相册拍照后保存图片的路径
                Uri imageUri = Uri
                        .fromFile(new File(imagePath, IMAGE_FILE_NAME));
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takeIntent, REQUESTCODE_TAKE);
                // 拍照完以后，文件就会保存在这个指定的目录下了。Uri 里指定了相机拍照的路径
                dialog.dismiss();
            }
        });
        tv_xiangce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                // 如果要限制上传到服务器的图片类型时可以直接写如：image/jpeg 、 image/png等的类型
                pickIntent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/png");

                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE_PICK:// 直接从相册获取
                try {
                    CutPhoto(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUESTCODE_TAKE:// 调用相机拍照
                File temp = new File(Environment.getExternalStorageDirectory()
                        + "/" + IMAGE_FILE_NAME);
                CutPhoto(Uri.fromFile(temp));
                break;
            case REQUESTCODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    SavaPic(data);
                }
                break;
            case LUYIN://录音界面的回调
                try {
                    Map<String, Object> map = new HashMap<>();
                    map.put("path", data.getStringExtra("path"));
                    map.put("pathname", data.getStringExtra("pathname"));
                    map.put("time", data.getIntExtra("time", 0));
                    pathlist.add(map);
                    List<Integer> gvlist = new ArrayList<Integer>();
                    for (int i = 0; i < pathlist.size(); i++) {
                        int a = (Integer) pathlist.get(i).get("time");
                        gvlist.add(a);
                    }
                    if (gvlist.size() >= 3) {
                        iv_luyin.setVisibility(View.GONE);
                    }
                    lygvadapter = new DiscloseGvluyinAdapter(DiscloseActivity.this, gvlist, this);
                    gv_luyin.setAdapter(lygvadapter);
                } catch (NullPointerException e) {
                    e.printStackTrace();//取消
                }
                break;
        }
    }

    /**
     * 裁剪图片方法
     *
     * @param uri
     */
    public void CutPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/png");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", true);
        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * 保存裁剪之后的图片
     *
     * @param picdata
     */
    private void SavaPic(Intent picdata) {

        final Bundle extras = picdata.getExtras();
        if (extras != null) {
            // 取得SDCard图片路径做显示
            Bitmap photo = extras.getParcelable("data");
            Matrix matrix = new Matrix();
            matrix.postScale(0.5f, 0.5f); //长和宽放大缩小的比例
            Bitmap resizeBmp = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
//            Drawable drawable = new BitmapDrawable(null, photo);
//            list.add(drawable);
            list.add(resizeBmp);
            Log.i("logsdf", "原宽高" + photo.getWidth() + "，" + photo.getHeight() + "现宽高" + resizeBmp.getWidth() + "，" + resizeBmp.getHeight());
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 删除图片
     */
    public void clear(int po) {
        list.remove(po);
        adapter.notifyDataSetChanged();
    }


    /**
     * 请求服务器的方法--实名
     */
    private void request2() {
        final String token = SharedPreferencesUtil.getSharedPreferences(DiscloseActivity.this).get("token").toString();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    JSONObject jobj = null;
                    try {
                        jobj = new JSONObject(result2);
                        isall = jobj.getInt("isall");
                        if (isall == 1) {//信息完善
                            title = et_bt.getText().toString().trim();
                            sj = et_riqi.getText().toString().trim() + " " + tv_time.getText().toString().trim() + ":00";
                            ms = et_ms.getText().toString().trim();
                            base = "";
                            for (int i = 0; i < list.size(); i++) {
                                base = base + BitmapToBase64(list.get(i)) + "-";
                            }
                            if (!"".equals(base)) {
                                base = base.substring(0, base.length() - 1);
                            }
                            add = tv_wz.getText().toString().trim();
                            if ("".equals(title)) {
                                Toast.makeText(DiscloseActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
                            } else if ("".equals(ms)) {
                                Toast.makeText(DiscloseActivity.this, "请输入描述内容", Toast.LENGTH_SHORT).show();
                            } else if ("显示位置".equals(add) && cb_sm.isChecked()) {
                                Toast.makeText(DiscloseActivity.this, "请获取您的位置", Toast.LENGTH_SHORT).show();
                            } else {
                                request(title, sj, ms, base, add);
                            }
                        } else {
                            showMyself();
//                        Toast.makeText(DiscloseActivity.this, "信息不完善，请完善信息", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                super.run();
                Map<String, String> map = new HashMap<String, String>();
                map.put("token", token);
                Log.i("DiscloseActivity", map + "");
                try {
                    result2 = URLConnectionUtil.sendPostRequest(url2, map, "utf-8", 0);
                    Log.i("DiscloseActivity", result2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message m = new Message();
                m.what = 1;
                // 发送消息到Handler
                handler.sendMessage(m);
            }
        }.start();
    }


    /**
     * 界面不完善跳转弹出框
     */
    private void showMyself() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .create();
        dialog.show();
        Window window = dialog.getWindow();
        // 设置布局
        window.setContentView(R.layout.activity_tomyself_dialog);
        // 设置宽高
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置弹出的动画效果
//        window.setWindowAnimations(android.R.anim.overshoot_interpolator);
        window.setWindowAnimations(R.style.AnimBottom);// 动画效果

        TextView tv_yes = (TextView) window.findViewById(R.id.tv_yes);
        TextView tv_no = (TextView) window.findViewById(R.id.tv_no);
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DiscloseActivity.this, Myprofile.class));
                dialog.dismiss();
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }
        });
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    /**
     * 接口方法，响应ListView按钮点击事件
     */
    @Override
    public void click(View v) {
        //按钮所在的item位置
        int position = (Integer) v.getTag();
        pathlist.remove(position);
        List<Integer> gvlist = new ArrayList<Integer>();
        for (int i = 0; i < pathlist.size(); i++) {
            int a = (Integer) pathlist.get(i).get("time");
            gvlist.add(a);
        }
        lygvadapter = new DiscloseGvluyinAdapter(DiscloseActivity.this, gvlist, DiscloseActivity.this);
        gv_luyin.setAdapter(lygvadapter);
        if (pathlist.size() < 3) {
            iv_luyin.setVisibility(View.VISIBLE);
        }
    }


}
