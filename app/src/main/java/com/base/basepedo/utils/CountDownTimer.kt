package com.base.basepedo.utils

import android.os.Handler
import android.os.Message
import android.os.SystemClock

abstract class CountDownTimer
/**
 * @param millisInFuture The number of millis in the future from the call
 * to [.start] until the countdown is done and [.onFinish]
 * is called.
 * @param countDownInterval The interval along the way to receive
 * [.onTick] callbacks.
 */(
    /**
     * Millis since epoch when alarm should stop.
     */
    private val mMillisInFuture: Long,
    /**
     * The interval in millis that the user receives callbacks
     */
    private val mCountdownInterval: Long) {

    private var mStopTimeInFuture: Long = 0
    private var mCancelled = false

    /**
     * Cancel the countdown.
     *
     * Do not call it from inside CountDownTimer threads
     */
    fun cancel() {
        mHandler.removeMessages(MSG)
        mCancelled = true
    }

    /**
     * Start the countdown.
     */
    @Synchronized
    fun start(): CountDownTimer {
        if (mMillisInFuture <= 0) {
            onFinish()
            return this
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture
        mHandler.sendMessage(mHandler.obtainMessage(MSG))
        mCancelled = false
        return this
    }

    /**
     * Callback fired on regular interval.
     * @param millisUntilFinished The amount of time until finished.
     */
    abstract fun onTick(millisUntilFinished: Long)

    /**
     * Callback fired when the time is up.
     */
    abstract fun onFinish()

    // handles counting down
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            synchronized(this@CountDownTimer) {
                val millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime()
                if (millisLeft <= 0) {
                    onFinish()
                } else if (millisLeft < mCountdownInterval) {
                    // no tick, just delay until done
                    sendMessageDelayed(obtainMessage(MSG), millisLeft)
                } else {
                    val lastTickStart = SystemClock.elapsedRealtime()
                    onTick(millisLeft)

                    // take into account user's onTick taking time to execute
                    var delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime()

                    // special case: user's onTick took more than interval to
                    // complete, skip to next interval
                    while (delay < 0) delay += mCountdownInterval
                    if (!mCancelled) {
                        sendMessageDelayed(obtainMessage(MSG), delay)
                    }else{

                    }
                }
            }
        }
    }

    companion object {
        private const val MSG = 1
    }

}
