package com.juyikeji.myappjubao;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Administrator on 2016/4/26 0026.
 */
public class ServiceLogout extends Service{

    private static final String TAG = "ServiceLogout" ;
    public static final String ACTION = "com.juyikeji.myappjubao.ServiceLogout";

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "ServiceDemo onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "ServiceDemo onCreate");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.v(TAG, "ServiceDemo onStart");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "ServiceDemo onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }
}
