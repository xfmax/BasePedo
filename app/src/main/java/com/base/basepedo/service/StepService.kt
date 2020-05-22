package com.base.basepedo.service

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.os.RemoteException
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.base.basepedo.R
import com.base.basepedo.base.StepMode
import com.base.basepedo.callback.StepCallBack
import com.base.basepedo.config.Constant
import com.base.basepedo.pojo.StepData
import com.base.basepedo.ui.MainActivity
import com.base.basepedo.utils.CountDownTimer
import com.base.basepedo.utils.DbUtils
import java.text.SimpleDateFormat
import java.util.*

@TargetApi(Build.VERSION_CODES.CUPCAKE)
class StepService : Service(), StepCallBack {

    private val TAG = "StepService"
    private val DB_NAME = "basepedo"
    private var nm: NotificationManager? = null
    private var builder: NotificationCompat.Builder? = null
    private val messenger = Messenger(MessenerHandler())
    private var mBatInfoReceiver: BroadcastReceiver? = null
    private var mWakeLock: WakeLock? = null
    private var time: TimeCount? = null

    //当天的日期
    private var CURRENTDATE = ""
    private val todayDate: String
        private get() {
            val date = Date(System.currentTimeMillis())
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            return sdf.format(date)
        }

    override fun onCreate() {
        super.onCreate()
        Log.v(TAG, "onCreate")
        initBroadcastReceiver()
        startStep()
        startTimeCount()
    }

    private fun initBroadcastReceiver() {
        Log.v(TAG, "initBroadcastReceiver")
        val filter = IntentFilter()
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        //日期修改
        filter.addAction(Intent.ACTION_DATE_CHANGED)
        //关机广播
        filter.addAction(Intent.ACTION_SHUTDOWN)
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON)
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT)
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        mBatInfoReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (Intent.ACTION_SCREEN_ON == action) {
                    Log.v(TAG, "screen on")
                } else if (Intent.ACTION_SCREEN_OFF == action) {
                    Log.v(TAG, "screen off")
                    //改为60秒一存储
                    duration = 60000
                    //解决某些厂商的rom在锁屏后收不到sensor的回调
                    val runnable = Runnable { startStep() }
                    Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY)
                } else if (Intent.ACTION_USER_PRESENT == action) {
                    Log.v(TAG, "screen unlock")
                    save()
                    //改为30秒一存储
                    duration = 30000
                } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS == intent.action) {
                    Log.v(TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS")
                    //保存一次
                    save()
                } else if (Intent.ACTION_SHUTDOWN == intent.action) {
                    Log.v(TAG, " receive ACTION_SHUTDOWN")
                    save()
                } else if (Intent.ACTION_DATE_CHANGED == intent.action) {
                    Log.v(TAG, " receive ACTION_DATE_CHANGED")
                    initTodayData()
                    clearStepData()
                    Log.v(TAG, "归零数据：" + StepMode.CURRENT_SETP)
                    Step(StepMode.CURRENT_SETP)
                }
            }
        }
        registerReceiver(mBatInfoReceiver, filter)
    }

    private fun startStep() {
        var mode: StepMode = StepInPedometer(this, this)
        var isAvailable = mode.step
        if (!isAvailable) {
            mode = StepInAcceleration(this, this)
            isAvailable = mode.step
            if (isAvailable) {
                Log.v(TAG, "acceleration can execute!")
            }
        }
    }

    private fun startTimeCount() {
        time = TimeCount(duration.toLong(), 1000)
        time!!.start()
    }

    override fun Step(stepNum: Int) {
        StepMode.CURRENT_SETP = stepNum
        Log.v(TAG, "Step:$stepNum")
        updateNotification("今日步数：$stepNum 步")
    }

    override fun onStart(intent: Intent, startId: Int) {
        super.onStart(intent, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        return messenger.binder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        initTodayData()
        updateNotification("今日步数：" + StepMode.CURRENT_SETP + " 步")
        return START_STICKY
    }

    private fun initTodayData() {
        CURRENTDATE = todayDate
        DbUtils.createDb(this, DB_NAME)
        //获取当天的数据，用于展示
        val list = DbUtils.getQueryByWhere(StepData::class.java, "today", arrayOf(CURRENTDATE))
        if (list.size == 0 || list.isEmpty()) {
            StepMode.CURRENT_SETP = 0
        } else if (list.size == 1) {
            StepMode.CURRENT_SETP = list[0].step!!.toInt()
        } else {
            Log.v(TAG, "It's wrong！")
        }
    }

    /**
     * update notification
     */
    private fun updateNotification(content: String) {
        builder = NotificationCompat.Builder(this)
        builder!!.priority = Notification.PRIORITY_MIN
        val contentIntent = PendingIntent.getActivity(this, 0,
            Intent(this, MainActivity::class.java), 0)
        builder!!.setContentIntent(contentIntent)
        builder!!.setSmallIcon(R.mipmap.ic_launcher)
        builder!!.setTicker("BasePedo")
        builder!!.setContentTitle("BasePedo")
        //设置不可清除
        builder!!.setOngoing(true)
        builder!!.setContentText(content)
        val notification = builder!!.build()
        startForeground(0, notification)
        nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm!!.notify(R.string.app_name, notification)
    }

    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }

    private fun save() {
        val tempStep = StepMode.CURRENT_SETP
        val list = DbUtils.getQueryByWhere(StepData::class.java, "today", arrayOf(CURRENTDATE))
        if (list.size == 0 || list.isEmpty()) {
            val data = StepData()
            data.today = CURRENTDATE
            data.step = tempStep.toString() + ""
            DbUtils.insert(data)
        } else if (list.size == 1) {
            val data = list[0]
            data.step = tempStep.toString() + ""
            DbUtils.update(data)
        } else {
        }
    }

    private fun clearStepData() {
        StepMode.CURRENT_SETP = 0
    }

    override fun onDestroy() {
        //取消前台进程
        stopForeground(true)
        DbUtils.closeDb()
        unregisterReceiver(mBatInfoReceiver)
        val intent = Intent(this, StepService::class.java)
        startService(intent)
        super.onDestroy()
    }

    //    private  void unlock(){
    //        setLockPatternEnabled(android.provider.Settings.Secure.LOCK_PATTERN_ENABLED,false);
    //    }
    //
    //    private void setLockPatternEnabled(String systemSettingKey, boolean enabled) {
    //        //推荐使用
    //        android.provider.Settings.Secure.putInt(getContentResolver(), systemSettingKey,enabled ? 1 : 0);
    //    }
    @Synchronized
    private fun getLock(context: Context): WakeLock? {
        if (mWakeLock != null) {
            if (mWakeLock?.isHeld!!) mWakeLock?.release()
            mWakeLock = null
        }
        if (mWakeLock == null) {
            val mgr = context
                .getSystemService(Context.POWER_SERVICE) as PowerManager
            mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                StepService::class.java.name)
            mWakeLock?.setReferenceCounted(true)
            val c = Calendar.getInstance()
            c.timeInMillis = System.currentTimeMillis()
            val hour = c[Calendar.HOUR_OF_DAY]
            if (hour >= 23 || hour <= 6) {
                mWakeLock?.acquire(5000)
            } else {
                mWakeLock?.acquire(300000)
            }
        }
        return mWakeLock
    }

    private class MessenerHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constant.MSG_FROM_CLIENT -> try {
                    val messenger = msg.replyTo
                    val replyMsg = Message.obtain(null, Constant.MSG_FROM_SERVER)
                    val bundle = Bundle()
                    bundle.putInt("step", StepMode.CURRENT_SETP)
                    replyMsg.data = bundle
                    messenger.send(replyMsg)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    internal inner class TimeCount(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            // 如果计时器正常结束，则开始计步
            time!!.cancel()
            save()
            startTimeCount()
        }

        override fun onTick(millisUntilFinished: Long) {}
    }

    companion object {
        private const val SCREEN_OFF_RECEIVER_DELAY = 500L

        //默认为30秒进行一次存储
        private var duration = 30000
    }
}
