package com.base.basepedo.ui

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.base.basepedo.R
import com.base.basepedo.config.Constant
import com.base.basepedo.service.StepService

class MainActivity : AppCompatActivity(), Handler.Callback {
    private val TAG = MainActivity::class.java.simpleName

    //循环取当前时刻的步数中间的间隔时间
    private val TIME_INTERVAL: Long = 500
    private var text_step: TextView? = null
    private var messenger: Messenger? = null
    private val mGetReplyMessenger = Messenger(Handler(this))
    private var delayHandler: Handler? = null
    var conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            try {
                messenger = Messenger(service)
                val msg = Message.obtain(null, Constant.MSG_FROM_CLIENT)
                msg.replyTo = mGetReplyMessenger
                messenger!!.send(msg)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            Constant.MSG_FROM_SERVER -> {
                // 更新界面上的步数
                text_step!!.text = msg.data.getInt("step").toString() + ""
                delayHandler!!.sendEmptyMessageDelayed(Constant.REQUEST_SERVER, TIME_INTERVAL)
            }
            Constant.REQUEST_SERVER -> try {
                val msg1 = Message.obtain(null, Constant.MSG_FROM_CLIENT)
                msg1.replyTo = mGetReplyMessenger
                messenger!!.send(msg1)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        startServiceForStrategy()
    }

    private fun startServiceForStrategy() {
        if (!isServiceWork(this, StepService::class.java.name)) {
            setupService(true)
        } else {
            setupService(false)
        }
    }

    private fun init() {
        text_step = findViewById(R.id.text_step)
        delayHandler = Handler(this)
    }

    override fun onStart() {
        super.onStart()
    }

    /**
     * 启动service
     *
     * @param flag true-bind和start两种方式一起执行 false-只执行bind方式
     */
    private fun setupService(flag: Boolean) {
        val intent = Intent(this, StepService::class.java)
        bindService(intent, conn, Context.BIND_AUTO_CREATE)
        if (flag) {
            startService(intent)
        }
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    fun isServiceWork(mContext: Context, serviceName: String): Boolean {
        var isWork = false
        val myAM = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val myList = myAM.getRunningServices(40)
        if (myList.size <= 0) {
            return false
        }
        for (i in myList.indices) {
            val mName = myList[i].service.className.toString()
            if (mName == serviceName) {
                isWork = true
                break
            }
        }
        return isWork
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(conn)
    }
}
