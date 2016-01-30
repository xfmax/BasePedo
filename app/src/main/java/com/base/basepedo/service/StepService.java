package com.base.basepedo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.base.basepedo.R;
import com.base.basepedo.service.StepDcretor.OnSensorChangeListener;
import com.base.basepedo.ui.MainActivity;

import java.util.Calendar;

public class StepService extends Service {
    private SensorManager sensorManager;
    private StepDcretor stepDetector;
    private WakeLock mWakeLock;
    private NotificationManager nm;
    private NotificationCompat.Builder builder;

    @Override
    public void onCreate() {
        super.onCreate();
        //mNM = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        setupNotification("今日步数："+"0"+" 步");

    }


    private void setupNotification(String content){
        builder = new NotificationCompat.Builder(this);
        builder.setPriority(Notification.PRIORITY_MIN);

        //Notification.Builder builder = new Notification.Builder(this);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("BasePedo");
        builder.setContentTitle("BasePedo");
        //设置不可清除
        builder.setOngoing(true);
        builder.setContentText(content);
        Notification notification = builder.build();

        startForeground(0, notification);

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(R.string.app_name, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            public void run() {
                startStepDetector();
            }
        }).start();
        return START_REDELIVER_INTENT;
    }

    private void startStepDetector() {
        if (sensorManager != null && stepDetector != null) {
            sensorManager.unregisterListener(stepDetector);
            sensorManager = null;
            stepDetector = null;
        }
        getLock(this);
        if (sensorManager == null) {
            stepDetector = new StepDcretor(this);
            // 获取传感器管理器的实例
            sensorManager = (SensorManager) this
                    .getSystemService(SENSOR_SERVICE);
            // 获得传感器的类型，这里获得的类型是加速度传感器
            // 此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
            Sensor sensor = sensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // sensorManager.unregisterListener(stepDetector);
            sensorManager.registerListener(stepDetector, sensor,
                    SensorManager.SENSOR_DELAY_GAME);
            stepDetector
                    .setOnSensorChangeListener(new OnSensorChangeListener() {

                        @Override
                        public void onChange() {

                            setupNotification("今日步数："+StepDcretor.CURRENT_SETP+" 步");


                            // // Log.i("stepchanged",
                            // //
                            // "stepchanged&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                            // if (userid == null) {
                            // userid = SpfOptUtils.getstrspfattr(
                            // LocationService.this, Constants.USERID);
                            // }
                            // // Intent intent = new
                            // // Intent("com.pedometer.changeaction");
                            // // sendBroadcast(intent);
                            // long curtime = System.currentTimeMillis();
                            // if (curtime - perSaveStepTime >= 30000
                            // && StepDcretor.CURRENT_RANGE_NUM > 0) {
                            // Calendar curc = Calendar.getInstance();
                            // curc.setTimeInMillis(curtime);
                            // int minute = curc.get(Calendar.MINUTE);
                            //
                            // if (minute <= 15) {
                            // curc.set(Calendar.MINUTE, 0);
                            // } else if (minute > 15 && minute <= 30) {
                            // curc.set(Calendar.MINUTE, 15);
                            // } else if (minute > 30 && minute <= 45) {
                            // curc.set(Calendar.MINUTE, 30);
                            // } else if (minute > 45) {
                            // curc.set(Calendar.MINUTE, 45);
                            // }
                            // curc.set(Calendar.SECOND, 0);
                            // curc.set(Calendar.MILLISECOND, 0);
                            // if (stepDetector.CURRENT_SETP > 0) {
                            // int savedstepnum = getCurTimeStepNum(curc
                            // .getTimeInMillis());
                            // int savedamplitudenum = getCurTimeAmiNum(curc
                            // .getTimeInMillis());
                            // if (userid == null)
                            // userid = SpfOptUtils.getstrspfattr(
                            // LocationService.this,
                            // Constants.USERID);
                            // addStepNum(userid, curc.getTimeInMillis(),
                            // StepDcretor.CURRENT_SETP
                            // + savedstepnum,
                            // StepDcretor.CURRENT_RANGE_NUM
                            // + savedamplitudenum);
                            // StepDcretor.CURRENT_SETP = 0;
                            // StepDcretor.CURRENT_RANGE_NUM = 0;
                            // perSaveStepTime = curtime;
                            //
                            // int dayTotalStepNum = getDayTotalStepNum();
                            //
                            // int hisHeight = SpfOptUtils.getintspfattr(
                            // LocationService.this,
                            // Constants.MYSTEPNUMMAX);
                            // if (dayTotalStepNum > hisHeight) {
                            // SpfOptUtils.addintspfattr(
                            // LocationService.this,
                            // Constants.MYSTEPNUMMAX,
                            // dayTotalStepNum);
                            // }
                            //
                            // int dayTotalAmiNum = getDayTotalAmiNum();
                            // int hisAmiHeight = SpfOptUtils
                            // .getintspfattr(
                            // LocationService.this,
                            // Constants.MYAMINUMMAX);
                            // if (dayTotalAmiNum > hisAmiHeight) {
                            // SpfOptUtils.addintspfattr(
                            // LocationService.this,
                            // Constants.MYAMINUMMAX,
                            // dayTotalAmiNum);
                            // }
                            // }
                            // }
                        }
                    });
        }
    }

    @Override
    public void onDestroy() {
        //取消前台进程
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    synchronized private PowerManager.WakeLock getLock(Context context) {
        if (mWakeLock != null) {
            if (mWakeLock.isHeld())
                mWakeLock.release();
            mWakeLock = null;
        }

        if (mWakeLock == null) {
            PowerManager mgr = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    StepService.class.getName());
            mWakeLock.setReferenceCounted(true);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            int hour = c.get(Calendar.HOUR_OF_DAY);
            // if (LoopNewMainActivity.isOnDestroy) {
            if (hour >= 23 || hour <= 6) {
                mWakeLock.acquire(5000);
            } else {
                mWakeLock.acquire(300000);
            }
            // }
        }

        return (mWakeLock);
    }
}
