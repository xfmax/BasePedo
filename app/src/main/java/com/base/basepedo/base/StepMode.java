package com.base.basepedo.base;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.base.basepedo.callback.StepCallBack;


/**
 * 计步模式分为 加速度传感器 google内置计步器
 * <p/>
 * Created by base on 2016/8/17.
 */
public abstract class StepMode implements SensorEventListener {
    private Context context;
    public StepCallBack stepCallBack;
    public SensorManager sensorManager;
    public static int CURRENT_SETP = 0;
    public boolean isAvailable = false;

    public StepMode(Context context, StepCallBack stepCallBack) {
        this.context = context;
        this.stepCallBack = stepCallBack;
    }

    public boolean getStep() {
        prepareSensorManager();
        registerSensor();
        return isAvailable;
    }

    protected abstract void registerSensor();

    private void prepareSensorManager() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            sensorManager = null;
        }
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
//        getLock(this);
//        android4.4以后可以使用计步传感器
//        int VERSION_CODES = android.os.Build.VERSION.SDK_INT;
//        if (VERSION_CODES >= 19) {
//            addCountStepListener();
//        } else {
//            addBasePedoListener();
//        }

    }


}
