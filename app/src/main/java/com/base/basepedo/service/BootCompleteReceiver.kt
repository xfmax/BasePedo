package com.base.basepedo.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 开机完成广播
 *
 * Created by base on 2016/3/1.
 */
class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val i = Intent(context, StepService::class.java)
        context.startService(i)
    }
}
