package com.base.basepedo.base

import android.content.Context
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.base.basepedo.callback.StepCallBack

/**
 * 计步模式分为 加速度传感器 google内置计步器
 *
 *
 * Created by base on 2016/8/17.
 */
abstract class StepMode(private val context: Context, var stepCallBack: StepCallBack) : SensorEventListener {
    @JvmField
    var sensorManager: SensorManager? = null
    @JvmField
    var isAvailable = false
    val step: Boolean
        get() {
            prepareSensorManager()
            registerSensor()
            return isAvailable
        }

    protected abstract fun registerSensor()
    private fun prepareSensorManager() {
        if (sensorManager != null) {
            sensorManager!!.unregisterListener(this)
            sensorManager = null
        }
        sensorManager = context
            .getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //        getLock(this);
//        android4.4以后可以使用计步传感器
//        int VERSION_CODES = android.os.Build.VERSION.SDK_INT;
//        if (VERSION_CODES >= 19) {
//            addCountStepListener();
//        } else {
//            addBasePedoListener();
//        }
    }

    companion object {
        @JvmField
        var CURRENT_SETP = 0
    }

}
