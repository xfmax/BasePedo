package com.base.basepedo.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.util.Log
import com.base.basepedo.base.StepMode
import com.base.basepedo.callback.StepCallBack

/**
 * Created by base on 2016/8/17.
 */
class StepInPedometer(context: Context?, stepCallBack: StepCallBack?) : StepMode(context!!, stepCallBack!!) {
    private val TAG = "StepInPedometer"
    private val lastStep = -1
    private var liveStep = 0
    private val increment = 0

    //0-TYPE_STEP_DETECTOR 1-TYPE_STEP_COUNTER
    private var sensorMode = 0
    override fun registerSensor() {
        addCountStepListener()
    }

    override fun onSensorChanged(event: SensorEvent) {
        liveStep = event.values[0].toInt()
        if (sensorMode == 0) {
            CURRENT_SETP += liveStep
        } else if (sensorMode == 1) {
            CURRENT_SETP = liveStep
        }
        stepCallBack.Step(CURRENT_SETP)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    private fun addCountStepListener() {
        val detectorSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        val countSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (detectorSensor != null) {
            sensorManager!!.registerListener(this, detectorSensor, SensorManager.SENSOR_DELAY_UI)
            isAvailable = true
            sensorMode = 0
        } else if (countSensor != null) {
            sensorManager!!.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI)
            isAvailable = true
            sensorMode = 1
        } else {
            isAvailable = false
            Log.v(TAG, "Count sensor not available!")
        }
    }
}
