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
	public static int CURRENT_SETP = 0;
	public static int TEMP_STEP = 0;
	public boolean current_state = false;
	private int lastStep = -1;
	// alpha 由 t / (t + dT)计算得来，其中 t 是低通滤波器的时间常数，dT 是事件报送频率
	private final float alpha = 0.8f;
	private long perCalTime = 0;
	/**0-准备计时   1-计时中  2-准备为正常计步计时  3-正常计步中*/
	private int CountTimeState = 0;
	private Timer timer;

	private final float minValue = 8.8f;
	private final float maxValue = 10.5f;
	private final float verminValue = 9.5f;
	private final float vermaxValue = 10.5f;

	// 加速计的三个维度数值
	public static float[] gravity = new float[3];
	public static float[] linear_acceleration = new float[3];
	public static float average = 0;

	// 等待时长
	private long duration = 8000;
	private TimeCount time;

	OnSensorChangeListener onSensorChangeListener;

	public StepDcretor(Context context) {
		super();
	}

	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
		synchronized (this) {
			if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

				// 用低通滤波器分离出重力加速度
				gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
				gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
				gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

				average = (float) Math.sqrt(Math.pow(gravity[0], 2)
						+ Math.pow(gravity[1], 2) + Math.pow(gravity[2], 2));

				if (average <= 9.5) {
					if (average <= minValue) {
						perCalTime = System.currentTimeMillis();
					}
				} else if (average >= 10) {
					if (average >= maxValue) {
						float betweentime = System.currentTimeMillis()
								- perCalTime;
						if (betweentime >= 150 && betweentime < 2000) {
							perCalTime = 0;
							if (CountTimeState == 0) {
								// 开启计时器
								time = new TimeCount(duration, 800);
								time.start();
								CountTimeState = 1;
								Log.v("xf", "开启计时器");
							} else if (CountTimeState == 1) {
								TEMP_STEP++;
								Log.v("xf", "计步中 TEMP_STEP:" + TEMP_STEP);
							} else if (CountTimeState == 2) {
								timer = new Timer(true);
								TimerTask task = new TimerTask() {
									public void run() {
										if (lastStep == CURRENT_SETP) {
											timer.cancel();
											CountTimeState = 0;
											lastStep = -1;
											TEMP_STEP = 0;
											Log.v("xf", "真正停下来了哦！！！！"+CURRENT_SETP);
										} else {
											lastStep = CURRENT_SETP;
										}
									}
								};
								timer.schedule(task, 0, 2000);
								CountTimeState = 3;
							}else if(CountTimeState == 3){
								CURRENT_SETP++;
							}
						}
					}
				}

				if (onSensorChangeListener != null) {
					onSensorChangeListener.onChange();
				}
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
			// lastStep = TEMP_STEP;
		}

		@Override
		public void onFinish() {
			// 如果计时器正常结束，则开始计步
			time.cancel();
			CURRENT_SETP += TEMP_STEP;
			lastStep = -1;
			CountTimeState = 2;
			Log.v("xf","onFinish");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (lastStep == TEMP_STEP) {
				Log.v("xf","onTick 停止");
				time.cancel();
				CountTimeState = 0;
				lastStep = -1;
				TEMP_STEP = 0;
			} else {
				//Log.v("xf","onTick 未停止");
				lastStep = TEMP_STEP;
			}
		}

	}
}