package com.juyikeji.myappjubao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juyikeji.myappjubao.R;
import com.juyikeji.myappjubao.utils.SharedPreferencesUtil;
import com.juyikeji.myappjubao.utils.URLConnectionUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 我的资料
 */
public class Myprofile extends Activity implements View.OnClickListener {
    private ImageView fanhui, iv_touxiang;
    private RelativeLayout rl_nickname, rl_zsname, rl_id, rl_editsex, rl_yh, rl_zfb,rl_adress;
    private String name_space2 = "getinfo";
    private String result = "";
    private String result1 = "";
    private String url = "editsex";
    private ImageLoader imageLoader;
    //用户昵称//真实姓名//手机号//身份证号//性别//支付宝//银行
    private TextView tv_nickname, tv_zsname, tv_phone, tv_id, tv_sex, tv_zhifubao, tv_yh,tv_adress;
    private String resultphoto = "";
    //修改头像接口
    private String name_space = "editheadimg";
    /**
     * 定义从相机或者相册 0是相机 1相册2剪辑后的图片
     */
    protected static final String IMAGE_FILE_NAME = "image.jsp";
    protected static final int REQUESTCODE_TAKE = 0;
    protected static final int REQUESTCODE_PICK = 1;
    protected static final int REQUESTCODE_CUTTING = 2;

    String id = "";

    private String zhifubaocode="";
    private String zhifubaoname="";

    private String bankname="";
    private String bankcode="";
    private String bankusername="";
    private String bankbranchname="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(Myprofile.this));

        init();
        request();
    }

    /**
     * 获取用户信息
     */
    private void request() {
        final String token = SharedPreferencesUtil.getSharedPreferences(Myprofile.this).get("token").toString();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    JSONObject jobj = null;
                    try {
                        jobj = new JSONObject(result);
                        JSONObject jo = jobj.getJSONObject("data");
                        String headImg = jo.getString("headImg");
                        String nickname = jo.getString("nickname");
                        String phone = jo.getString("phone");

                        tv_phone.setText(phone.substring(0, 3) + "****" +
                                phone.substring(phone.length() - 4, phone.length()));
                        tv_nickname.setText(nickname);
                        tv_zsname.setText(jo.getString("uname"));

                        id = jo.getString("idnumber");
                        if (!"null".equals(id)) {
                            tv_id.setText("****" + id.substring(id.length() - 4, id.length()));
                        }
                        String sex = jo.getString("sex");
                        if ("null".equals(sex)) {
                            tv_sex.setText("男");
                        } else {
                            tv_sex.setText(sex);
                        }
                        zhifubaocode = jo.getString("taobaocode");
                        zhifubaoname = jo.getString("taobaoname");
                        if (!"null".equals(zhifubaocode)) {
                            tv_zhifubao.setText(zhifubaocode);
                        }
                        //开户行
                        bankbranchname=jo.getString("bankbranchname");
                        //持卡人姓名
                        bankusername=jo.getString("bankusername");
                        //银行卡name
                        bankname = jo.getString("bankname");
                        //银行卡code
                        bankcode = jo.getString("bankcode");
                        String bankcode1 = bankcode.substring(bankcode.length() - 4, bankcode.length());
                        if ("null".equals(bankname) || "null".equals(bankcode)) {
                            tv_yh.setText("还没有绑定银行卡");
                        } else {
                            tv_yh.setText(bankname + "(" + bankcode1+ ")");
                        }
                        imageLoader.displayImage(headImg, iv_touxiang, SharedPreferencesUtil.getDefaultOptions());

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
                Log.i("Myprofile", token);
                try {
                    result = URLConnectionUtil.sendPostRequest(name_space2, map, "utf-8", 0);
                    Log.i("Myprofile2", result);
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
     * 实例化控件并设置监听
     */
    private void init() {
        iv_touxiang = (ImageView) findViewById(R.id.iv_touxiang);
        iv_touxiang.setOnClickListener(this);

        fanhui = (ImageView) findViewById(R.id.fanhui);
        fanhui.setOnClickListener(this);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        tv_zsname = (TextView) findViewById(R.id.tv_zsname);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_id = (TextView) findViewById(R.id.tv_id);
        tv_zhifubao = (TextView) findViewById(R.id.tv_zhifubao);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_yh = (TextView) findViewById(R.id.tv_yh);
        tv_adress = (TextView) findViewById(R.id.tv_adress);

        rl_nickname = (RelativeLayout) findViewById(R.id.rl_nickname);
        rl_nickname.setOnClickListener(this);
        rl_zsname = (RelativeLayout) findViewById(R.id.rl_zsname);
        rl_zsname.setOnClickListener(this);
        rl_id = (RelativeLayout) findViewById(R.id.rl_id);
        rl_id.setOnClickListener(this);
        rl_editsex = (RelativeLayout) findViewById(R.id.rl_editsex);
        rl_editsex.setOnClickListener(this);
        rl_yh = (RelativeLayout) findViewById(R.id.rl_yh);
        rl_yh.setOnClickListener(this);
        rl_zfb = (RelativeLayout) findViewById(R.id.rl_zfb);
        rl_zfb.setOnClickListener(this);
        rl_adress = (RelativeLayout) findViewById(R.id.rl_adress);
        rl_adress.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.fanhui:
                finish();
                break;
            //头像
            case R.id.iv_touxiang:
                showTouxiangDialog();
                break;
            //修改昵称
            case R.id.rl_nickname:
                Intent intent = new Intent(Myprofile.this, EditProfile.class);
                intent.putExtra("edit", "1");
                intent.putExtra("name", tv_nickname.getText().toString().trim());
                startActivityForResult(intent, 111);
                break;
            //修改真实姓名
            case R.id.rl_zsname:
                Intent intent1 = new Intent(Myprofile.this, EditProfile.class);
                intent1.putExtra("edit", "2");
                intent1.putExtra("name", tv_zsname.getText().toString().trim());
                startActivityForResult(intent1, 222);
                break;
            //修改身份证号
            case R.id.rl_id:
                Intent intent2 = new Intent(Myprofile.this, EditProfile.class);
                intent2.putExtra("edit", "3");
                intent2.putExtra("name", id);
                startActivityForResult(intent2, 333);
                break;
            //修改现住址
            case R.id.rl_adress:
                Intent intentadress=new Intent(this,EditProfile.class);
                intentadress.putExtra("edit", "adress");
                intentadress.putExtra("name", "现住址");
                startActivityForResult(intentadress, 666);
                break;
            //修改性别
            case R.id.rl_editsex:
                showsexDialog(tv_sex.getText().toString().trim());
                break;
            //银行
            case R.id.rl_yh:
                Intent intent4=new Intent(Myprofile.this, AddBank.class);
                intent4.putExtra("bankcode", bankcode);
                intent4.putExtra("bankname",bankname);
                intent4.putExtra("bankbranchname",bankbranchname);
                intent4.putExtra("bankusername",bankusername);
                startActivityForResult(intent4, 555);
                break;
            //支付宝
            case R.id.rl_zfb:
                Intent intent3=new Intent(Myprofile.this, AddZhiFuBao.class);
                intent3.putExtra("zhifubaocode",zhifubaocode);
                intent3.putExtra("zhifubaoname",zhifubaoname);
                startActivityForResult(intent3, 444);
                break;
        }
    }

    /**
     * 性别选择弹出框
     */
    public void showsexDialog(String sex) {
        final AlertDialog dialog = new AlertDialog.Builder(Myprofile.this).create();
        dialog.show();

        Window window = dialog.getWindow();
        // 设置布局
        window.setContentView(R.layout.activity_editsex);
        // 设置宽高
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        final ImageView iv_nan = (ImageView) window.findViewById(R.id.iv_nan);
        final ImageView iv_nv = (ImageView) window.findViewById(R.id.iv_nv);

        if ("女".equals(sex)) {
            iv_nan.setImageResource(R.mipmap.icon_x2);
            iv_nv.setImageResource(R.mipmap.icon_x1);
        } else if ("男".equals(sex)) {
            iv_nan.setImageResource(R.mipmap.icon_x1);
            iv_nv.setImageResource(R.mipmap.icon_x2);
        }
        iv_nan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_nan.setImageResource(R.mipmap.icon_x1);
                iv_nv.setImageResource(R.mipmap.icon_x2);
                tv_sex.setText("男");
                request2("男");
                dialog.dismiss();
            }
        });
        iv_nv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_nan.setImageResource(R.mipmap.icon_x2);
                iv_nv.setImageResource(R.mipmap.icon_x1);
                tv_sex.setText("女");
                request2("女");
                dialog.dismiss();
            }
        });
    }

    /**
     * 请求网络
     */
    private void request2(final String sex) {
        final String token = SharedPreferencesUtil.getSharedPreferences(Myprofile.this).get("token").toString();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    JSONObject jobj = null;
                    try {
                        jobj = new JSONObject(result1);
                        String code = jobj.getString("status");
                        if (code.equals("1")) {
                            Toast.makeText(Myprofile.this, "修改成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Myprofile.this, "修改失败", Toast.LENGTH_SHORT).show();
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
                Map<String, String> map = new HashMap<String, String>();
                // 获取令牌放入请求参数；
                map.put("token", token);
                map.put("sex", sex);

                try {
                    result1 = URLConnectionUtil.sendPostRequest(url, map, "utf-8", 0);
                    Log.i("Myprofile", result1);
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
     * 头像选择弹出框
     */
    public void showTouxiangDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(Myprofile.this).create();
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
//                if(!URLConnectionUtil.isOpenNetwork(this)){
//                    Toast.makeText(this, "网络无连接，请检查网络！", Toast.LENGTH_SHORT).show();
//                }else {
                if (data != null) {
                    SavaPic(data);
                }
//                }
                break;
            case 111://昵称
                if (resultCode == 111) {
                    tv_nickname.setText(data.getStringExtra("nickname"));
                }
                break;
            case 222://真实姓名
                if (resultCode == 111) {
                    tv_zsname.setText(data.getStringExtra("nickname"));
                }
                break;
            case 333://身份证号
                if (resultCode == 111) {
                    String idnumber = data.getStringExtra("nickname");
//                    idnumber=idnumber.substring(0,6)+"****"+idnumber.substring(15,idnumber.length());
                    tv_id.setText(idnumber);
                }
                break;
            case 444://支付宝
                if (resultCode == 111) {
                    String idnumber = data.getStringExtra("nickname");
                    tv_zhifubao.setText(idnumber);
                }
                break;
            case 555://银行卡
                if (resultCode == 111) {
                    String bankname1 = data.getStringExtra("bankname");
                    String bankcode1 = data.getStringExtra("bankcode");
                    bankcode1 = bankcode1.substring(bankcode1.length() - 4, bankcode1.length());
                    tv_yh.setText(bankname1+bankcode1);
                }
                break;
            case 666://现住址
                if (resultCode==111){
                    String bankname1 = data.getStringExtra("nickname");
                    tv_adress.setText(bankname1);
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
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
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
        final String token = SharedPreferencesUtil.getSharedPreferences(Myprofile.this).get("token").toString();

        final Bundle extras = picdata.getExtras();
        if (extras != null) {
            // 取得SDCard图片路径做显示
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(null, photo);
            // 设置到头像
            iv_touxiang.setImageDrawable(drawable);

            final String strphoto = BitmapToBase64(photo);
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        JSONObject jobj = null;
                        try {
                            jobj = new JSONObject(resultphoto);
                            String code = jobj.getString("status");
                            String headimg=jobj.getString("headimg");
                            if (code.equals("1")) {
                                Toast.makeText(Myprofile.this, "上传成功", Toast.LENGTH_SHORT).show();
                                request();
//                                imageLoader.displayImage(headimg, iv_touxiang, SharedPreferencesUtil.getDefaultOptions());
                            } else {
                                Toast.makeText(Myprofile.this, "上传失败", Toast.LENGTH_SHORT).show();
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
                    Map<String, String> map = new HashMap<String, String>();
                    // 获取令牌放入请求参数；
                    map.put("token", token);
                    // 转base64后上传服务器
                    map.put("imgbase", strphoto);
                    // 照片名
                    map.put("imgname", ".png");
                    try {
                        resultphoto = URLConnectionUtil.sendPostRequest(name_space, map, "utf-8", 0);
                        Log.i("Myprofile", resultphoto);
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
    }

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
}
