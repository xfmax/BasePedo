package com.base.basepedo.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.base.basepedo.utils.CountDownTimer;

import java.util.Timer;
import java.util.TimerTask;

public class StepDcretor implements SensorEventListener {
    float avg_v = 0;
    float min_v = 0;
    float max_v = 0;

    int acc_count = 0;
    int up_c = 0;
    int down_c = 0;
    long pre_time = 0;


    private final String TAG = "StepDcretor";
    // alpha 由 t / (t + dT)计算得来，其中 t 是低通滤波器的时间常数，dT 是事件报送频率
    private final float alpha = 0.8f;
    private long perCalTime = 0;

    //最新修改的精度值
    private final float minValue = 9.8f;
    private final float maxValue = 9.9f;
    //9.5f
//    private final float verminValue = 8.5f;
    //10.0f
//    private final float vermaxValue = 11.5f;
    private final float minTime = 150;
    private final float maxTime = 2000;
    /**
     * 0-准备计时   1-计时中  2-准备为正常计步计时  3-正常计步中
     */
    private int CountTimeState = 0;
    public static int CURRENT_SETP = 0;
    public static int TEMP_STEP = 0;
    private int lastStep = -1;
    // 加速计的三个维度数值
    public static float[] gravity = new float[3];
    public static float[] linear_acceleration = new float[3];
    //用三个维度算出的平均值
    public static float average = 0;

    private Timer timer;
    // 倒计时5秒，5秒内不会显示计步，用于屏蔽细微波动
    private long duration = 4000;
    private TimeCount time;

    OnSensorChangeListener onSensorChangeListener;

    public StepDcretor(Context context) {
        super();
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                calc_step(event);

            }
        }
    }





    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    public interface OnSensorChangeListener {
        void onChange();
    }

    public OnSensorChangeListener getOnSensorChangeListener() {
        return onSensorChangeListener;
    }

    public void setOnSensorChangeListener(
            OnSensorChangeListener onSensorChangeListener) {
        this.onSensorChangeListener = onSensorChangeListener;
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            // 如果计时器正常结束，则开始计步
            time.cancel();
            CURRENT_SETP += TEMP_STEP;
            lastStep = -1;
//            CountTimeState = 2;
            Log.v(TAG, "计时正常结束");

            timer = new Timer(true);
            TimerTask task = new TimerTask() {
                public void run() {
                    if (lastStep == CURRENT_SETP) {
                        timer.cancel();
                        CountTimeState = 0;
                        lastStep = -1;
                        TEMP_STEP = 0;
                        Log.v(TAG, "停止计步：" + CURRENT_SETP);
                    } else {
                        lastStep = CURRENT_SETP;
                    }
                }
            };
            timer.schedule(task, 0, 3000);
            CountTimeState = 3;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (lastStep == TEMP_STEP) {
                Log.v(TAG, "onTick 计时停止");
                time.cancel();
                CountTimeState = 0;
                lastStep = -1;
                TEMP_STEP = 0;
            } else {
                lastStep = TEMP_STEP;
            }
        }

    }


    void avg_check_v(float v) {
        acc_count++;
        //求移动平均线
        //50ms 1 second 20 , 3 sec60;
        if (acc_count < 64) {
            //avg_v=((acc_count-1)*avg_v+v)/acc_count;
            avg_v = avg_v + (v - avg_v) / acc_count;
        } else {
            //avg_v=(avg_v*99+v)/100;
            avg_v = avg_v * 63 / 64 + v / 64;
        }

        if (v > avg_v) {
            up_c++;
            if (up_c == 1) {
                //Log.e("wokao","diff:"+(max_v-min_v));
                max_v = avg_v;
            } else {
                max_v = Math.max(v, max_v);
            }
            if (up_c >= 2) {
                down_c = 0;
            }
        } else {
            down_c++;
            if (down_c == 1) {
                min_v = v;
            } else {
                min_v = Math.min(v, min_v);
            }
            if (down_c >= 2) {
                up_c = 0;
            }
        }
        //Log.e("wokao","avg_v:"+avg_v+",v:"+v+",uc"+up_c+",dc:"+down_c);

        if (up_c == 2 && (max_v - min_v) > 2) {
            //
            long cur_time = System.currentTimeMillis();
            if (cur_time - pre_time > 250) {
                pre_time = cur_time;
                preStep();
//                StepDcretor.CURRENT_SETP++;
//                //记步，通知ui
//                if (onSensorChangeListener != null) {
//                    onSensorChangeListener.onChange();
//                }
                Log.e("xfblog","CURRENT_SETP:"+CURRENT_SETP);
            } else {
                up_c = 1;
            }
        }
    }

    synchronized private void calc_step(SensorEvent arg0) {
        float v = (float) Math.sqrt(arg0.values[0] * arg0.values[0] + arg0.values[1] * arg0.values[1] + arg0.values[2] * arg0.values[2]);
        avg_check_v(v);
    }

    private void preStep(){
        if (CountTimeState == 0) {
            // 开启计时器
            time = new TimeCount(duration, 700);
            time.start();
            CountTimeState = 1;
            Log.v(TAG, "开启计时器");
        } else if (CountTimeState == 1) {
            TEMP_STEP++;
            Log.v(TAG, "计步中 TEMP_STEP:" + TEMP_STEP);
        } else if (CountTimeState == 3) {
            CURRENT_SETP++;
            if (onSensorChangeListener != null) {
                onSensorChangeListener.onChange();
            }
        }
    }



    private void oldCalStep(SensorEvent event){
        // 用低通滤波器分离出重力加速度
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        average = (float) Math.sqrt(Math.pow(gravity[0], 2)
                + Math.pow(gravity[1], 2) + Math.pow(gravity[2], 2));

//                if (average <= verminValue) {
        if (average <= minValue) {
            Log.v("xfblog","低");
            perCalTime = System.currentTimeMillis();
        }
//                } else if (average >= vermaxValue) {
        else if (average >= maxValue) {
            Log.v("xfblog","高");
            float betweentime = System.currentTimeMillis()
                    - perCalTime;
            if (betweentime >= minTime && betweentime < maxTime) {
                perCalTime = 0;
                if (CountTimeState == 0) {
                    // 开启计时器
                    time = new TimeCount(duration, 800);
                    time.start();
                    CountTimeState = 1;
                    Log.v(TAG, "开启计时器");
                } else if (CountTimeState == 1) {
                    TEMP_STEP++;
                    Log.v(TAG, "计步中 TEMP_STEP:" + TEMP_STEP);
                }
//                        else if (CountTimeState == 2) {
//                            timer = new Timer(true);
//                            TimerTask task = new TimerTask() {
//                                public void run() {
//                                    if (lastStep == CURRENT_SETP) {
//                                        timer.cancel();
//                                        CountTimeState = 0;
//                                        lastStep = -1;
//                                        TEMP_STEP = 0;
//                                        Log.v(TAG, "停止计步：" + CURRENT_SETP);
//                                    } else {
//                                        lastStep = CURRENT_SETP;
//                                    }
//                                }
//                            };
//                            timer.schedule(task, 0, 2000);
//                            CountTimeState = 3;
//                        }
                else if (CountTimeState == 3) {
                    CURRENT_SETP++;
                    if (onSensorChangeListener != null) {
                        onSensorChangeListener.onChange();
                    }
                }



            }
        }
//                  }
    }
}
